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
package io.guthix.oldscape.server.world.entity.interest

import io.guthix.oldscape.server.net.game.out.SetPlayerOpPacket
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Player

class ContextMenuManager : InterestManager {
    var contextMenu = arrayOf("Follow", "Trade with", "Report")

    var changes = mutableMapOf<Int, String>()

    override fun initialize(world: World, player: Player) {
        contextMenu.forEachIndexed { i, text ->
            player.ctx.write(SetPlayerOpPacket(false, i + 1, text))
        }
    }

    override fun synchronize(world: World, player: Player) = changes.map { (slot, text) ->
        player.ctx.write(SetPlayerOpPacket(false, slot + 1, text))
    }

    override fun postProcess() = changes.clear()
}