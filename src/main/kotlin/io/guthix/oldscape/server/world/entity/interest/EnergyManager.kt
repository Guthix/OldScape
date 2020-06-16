/**
 * This file is part of Guthix OldScape-Server.
 *
 * Guthix OldScape-Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape-Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.world.entity.interest

import io.guthix.oldscape.server.net.game.out.UpdateRunenergyPacket
import io.guthix.oldscape.server.net.game.out.UpdateRunweightPacket
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Player
import io.netty.channel.ChannelFuture

class EnergyManager : InterestManager {
    private var weightChanged = false

    var weight: Int = 50
        set(value) {
            weightChanged = true
            field = value
        }

    private var energyChanged = false

    var energy: Int = 100
        set(value) {
            energyChanged = true
            field = value
        }

    override fun initialize(world: World, player: Player) {
        player.ctx.write(UpdateRunweightPacket(weight))
        player.ctx.write(UpdateRunenergyPacket(energy))
    }

    override fun synchronize(world: World, player: Player): List<ChannelFuture> {
        val futures = mutableListOf<ChannelFuture>()
        if (weightChanged) futures.add(player.ctx.write(UpdateRunweightPacket(weight)))
        if (energyChanged) futures.add(player.ctx.write(UpdateRunenergyPacket(energy)))
        return futures
    }

    override fun postProcess() {
        weightChanged = false
        energyChanged = false
    }
}