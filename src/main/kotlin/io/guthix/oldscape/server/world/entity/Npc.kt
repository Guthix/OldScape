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

import io.guthix.oldscape.cache.config.NpcConfig
import io.guthix.oldscape.server.blueprints.*
import io.guthix.oldscape.server.dimensions.TileUnit
import io.guthix.oldscape.server.dimensions.tiles
import io.guthix.oldscape.server.net.game.out.NpcInfoSmallViewportPacket
import io.guthix.oldscape.server.plugin.ConfigDataMissingException
import io.guthix.oldscape.server.task.Task
import io.guthix.oldscape.server.world.entity.interest.NpcUpdateType
import io.guthix.oldscape.server.world.map.Tile
import mu.KotlinLogging
import java.io.IOException

private val logger = KotlinLogging.logger { }

class Npc(private val blueprint: NpcBlueprint, index: Int, override var pos: Tile) : Character(index) {
    val spawnPos: Tile = pos.copy()

    override val updateFlags = sortedSetOf<NpcUpdateType>()

    val id: Int get() = blueprint.id

    override val size: TileUnit get() = blueprint.size.tiles

    val contextMenu: Array<String?> get() = blueprint.contextMenu

    val wanderRadius: TileUnit get() = blueprint.wanderRadius

    override fun processTasks() {
        while (true) {
            val resumed = tasks.values.flatMap { routineList -> routineList.toList().map(Task::run) } // TODO optimize
            if (resumed.all { !it }) break // TODO add live lock detection
        }
    }

    override fun addOrientationFlag(): Boolean = updateFlags.add(NpcInfoSmallViewportPacket.orientation)

    override fun addTurnToLockFlag(): Boolean = updateFlags.add(NpcInfoSmallViewportPacket.turnLockTo)

    override fun addSequenceFlag(): Boolean = updateFlags.add(NpcInfoSmallViewportPacket.sequence)

    override fun checkSequenceFlag(): Boolean = updateFlags.contains(NpcInfoSmallViewportPacket.sequence)

    override fun addSpotAnimationFlag(): Boolean = updateFlags.add(NpcInfoSmallViewportPacket.spotAnimation)

    override fun addHitUpdateFlag(): Boolean = updateFlags.add(NpcInfoSmallViewportPacket.hit)

    override fun addShoutFlag(): Boolean = updateFlags.add(NpcInfoSmallViewportPacket.shout)

    companion object {
        internal lateinit var blueprints: Map<Int, NpcBlueprint>

        internal inline operator fun <reified T : NpcBlueprint> get(index: Int): T {
            val bp = blueprints[index] ?: throw IOException("Could not find blueprint $index.")
            if (bp !is T) {
                throw TypeCastException("")
            }
            return bp
        }

        internal fun loadBlueprints(
            cConfigs: Map<Int, NpcConfig>,
            extraNpcConfigs: List<ExtraNpcConfig>
        ) {
            blueprints = mutableMapOf<Int, NpcBlueprint>().apply {
                addBlueprints(cConfigs, extraNpcConfigs, ::NpcBlueprint)
            }
            logger.info { "Loaded ${blueprints.size} npc blueprints" }
        }

        private fun <E : ExtraNpcConfig, B : NpcBlueprint> MutableMap<Int, NpcBlueprint>.addBlueprints(
            cacheConfigs: Map<Int, NpcConfig>,
            extraObjectConfigs: List<E>,
            construct: (NpcConfig, E) -> B
        ) {
            extraObjectConfigs.forEach { extraConfig ->
                extraConfig.ids.forEach inner@{ id ->
                    val cacheConfig = cacheConfigs[id] ?: kotlin.run {
                        logger.warn { "Could not find npc for id $id" }
                        return@inner
                    }
                    put(id, construct.invoke(cacheConfig, extraConfig))
                }
            }
        }
    }
}