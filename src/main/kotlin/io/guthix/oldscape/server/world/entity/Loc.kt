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

import io.guthix.oldscape.server.template.type.LocTemplate
import io.guthix.oldscape.server.world.map.Tile
import io.guthix.oldscape.server.world.map.dim.TileUnit

class Loc(
    private val template: LocTemplate,
    val type: Int,
    override val pos: Tile,
    override var orientation: Int
) : Entity() {
    val id: Int get() = template.id
    val impenetrable: Boolean get() = template.impenetrable
    val clipType: Int get() = template.clipType
    val width: TileUnit get() = template.width
    val length: TileUnit get() = template.length
    override val sizeX: TileUnit get() = if (orientation == 0 || orientation == 2) width else length
    override val sizeY: TileUnit get() = if (orientation == 0 || orientation == 2) length else width

    val accessBlockFlags: Int
        get() = if (orientation != 0) {
            (template.accessBlockFlags shl orientation and 0xF) + (template.accessBlockFlags shr 4 - orientation)
        } else {
            template.accessBlockFlags
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
    }
}