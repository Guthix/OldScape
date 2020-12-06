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

import io.guthix.oldscape.server.ServerContext
import io.guthix.oldscape.server.net.game.out.NpcInfoSmallViewportPacket
import io.guthix.oldscape.server.template.NpcTemplate
import io.guthix.oldscape.server.world.entity.interest.NpcUpdateType
import io.guthix.oldscape.server.world.map.Tile
import io.guthix.oldscape.server.world.map.dim.TileUnit
import io.guthix.oldscape.server.world.map.dim.tiles

data class Npc(val id: Int, override val index: Int, override var pos: Tile) : Character(index) {
    val template: NpcTemplate by lazy { ServerContext.npcTemplates[id] }

    val name: String get() = template.name

    var isRemoved: Boolean = false

    override var spawnPos: Tile = pos.copy()

    override val updateFlags = sortedSetOf<NpcUpdateType>()

    override val size: TileUnit get() = template.size.tiles

    val contextMenu: Array<String?> get() = template.contextMenu

    override var orientation: Int = 0

    override fun toString(): String = "Npc(index=$index, id=$id, name=$name, pos=$pos)"

    override fun addOrientationFlag(): Boolean = updateFlags.add(NpcInfoSmallViewportPacket.orientation)

    override fun addTurnToLockFlag(): Boolean = updateFlags.add(NpcInfoSmallViewportPacket.turnLockTo)

    override fun addSequenceFlag(): Boolean = updateFlags.add(NpcInfoSmallViewportPacket.sequence)

    override fun checkSequenceFlag(): Boolean = updateFlags.contains(NpcInfoSmallViewportPacket.sequence)

    override fun addSpotAnimationFlag(): Boolean = updateFlags.add(NpcInfoSmallViewportPacket.spotAnimation)

    override fun addHitUpdateFlag(): Boolean = updateFlags.add(NpcInfoSmallViewportPacket.hit)

    override fun addShoutFlag(): Boolean = updateFlags.add(NpcInfoSmallViewportPacket.shout)
}