/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.world.entity

import io.guthix.oldscape.server.api.NpcBlueprints
import io.guthix.oldscape.server.dimensions.tiles
import io.guthix.oldscape.server.net.game.out.NpcInfoSmallViewportPacket
import io.guthix.oldscape.server.world.entity.interest.NpcUpdateType
import io.guthix.oldscape.server.world.map.Tile

open class Npc(index: Int, id: Int, override var pos: Tile) : Character(index) {
    private val blueprint = NpcBlueprints[id]

    override val updateFlags = sortedSetOf<NpcUpdateType>()

    val id get() = blueprint.id

    override val size get() = blueprint.size.tiles

    val contextMenu get() = blueprint.contextMenu

    open fun postProcess() = updateFlags.clear()

    override fun addOrientationFlag() = updateFlags.add(NpcInfoSmallViewportPacket.orientation)

    override fun addTurnToLockFlag() = updateFlags.add(NpcInfoSmallViewportPacket.turnLockTo)
}