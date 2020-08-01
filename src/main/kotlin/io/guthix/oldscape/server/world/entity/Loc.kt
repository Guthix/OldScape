/*
 * Copyright 2018-2020 Guthix
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.guthix.oldscape.server.world.entity

import io.guthix.cache.js5.Js5Archive
import io.guthix.oldscape.cache.config.LocationConfig
import io.guthix.oldscape.server.blueprints.LocationBlueprint
import io.guthix.oldscape.server.dimensions.TileUnit
import io.guthix.oldscape.server.world.map.Tile
import mu.KotlinLogging
import java.io.IOException

private val logger = KotlinLogging.logger { }

fun LocationBlueprint.create(
    type: Int,
    pos: Tile,
    orientation: Int
): Loc = Loc(this, type, pos, orientation)

class Loc(
    private val blueprint: LocationBlueprint,
    val type: Int,
    override val pos: Tile,
    override var orientation: Int
) : Entity() {
    val id: Int get() = blueprint.id
    val impenetrable: Boolean get() = blueprint.impenetrable
    val clipType: Int get() = blueprint.clipType
    val width: TileUnit get() = blueprint.width
    val length: TileUnit get() = blueprint.length
    override val sizeX: TileUnit get() = if (orientation == 0 || orientation == 2) width else length
    override val sizeY: TileUnit get() = if (orientation == 0 || orientation == 2) length else width

    val accessBlockFlags: Int
        get() = if (orientation != 0) {
            (blueprint.accessBlockFlags shl orientation and 0xF) + (blueprint.accessBlockFlags shr 4 - orientation)
        } else {
            blueprint.accessBlockFlags
        }

    val slot: Int get() = MAP_SLOTS[type]

    internal val mapKey get() = (pos.x.relativeZone.value shl 5) or (pos.y.relativeZone.value shl 2) or slot

    companion object {
        internal const val UNIQUE_SLOTS: Int = 4

        internal val MAP_SLOTS: IntArray = intArrayOf(
            0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3
        )

        internal fun generateMapKey(localX: TileUnit, localY: TileUnit, slot: Int): Int = (localX.value shl 5) or
            (localY.value shl 2) or slot

        internal lateinit var blueprints: Map<Int, LocationBlueprint>

        internal operator fun get(index: Int): LocationBlueprint = blueprints[index]
            ?: throw IOException("Could not find blueprint $index.")

        internal fun loadBlueprints(archive: Js5Archive) {
            val locConfigs = LocationConfig.load(archive.readGroup(LocationConfig.id))
            val tempLocs = mutableMapOf<Int, LocationBlueprint>()
            locConfigs.forEach { (id, config) ->
                tempLocs[id] = LocationBlueprint(config)
            }
            blueprints = tempLocs.toMap()
            logger.info { "Loaded ${blueprints.size} location blueprints" }
        }
    }
}