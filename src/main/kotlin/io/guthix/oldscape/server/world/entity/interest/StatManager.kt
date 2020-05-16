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

import io.guthix.oldscape.server.net.game.out.UpdateStatPacket
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Player
import io.netty.channel.ChannelFuture

class Stat(val statusLevel: Int, val level: Int, val xp: Int)

class StatManager: InterestManager {
    val skills = arrayOf(
        Stat(statusLevel = 99, level = 99, xp = 13_034_431),
        Stat(statusLevel = 99, level = 99, xp = 13_034_431),
        Stat(statusLevel = 99, level = 99, xp = 13_034_431),
        Stat(statusLevel = 99, level = 99, xp = 13_034_431),
        Stat(statusLevel = 99, level = 99, xp = 13_034_431),
        Stat(statusLevel = 99, level = 99, xp = 13_034_431),
        Stat(statusLevel = 99, level = 99, xp = 13_034_431),
        Stat(statusLevel = 99, level = 99, xp = 13_034_431),
        Stat(statusLevel = 99, level = 99, xp = 13_034_431),
        Stat(statusLevel = 99, level = 99, xp = 13_034_431),
        Stat(statusLevel = 99, level = 99, xp = 13_034_431),
        Stat(statusLevel = 99, level = 99, xp = 13_034_431),
        Stat(statusLevel = 99, level = 99, xp = 13_034_431),
        Stat(statusLevel = 99, level = 99, xp = 13_034_431),
        Stat(statusLevel = 99, level = 99, xp = 13_034_431),
        Stat(statusLevel = 99, level = 99, xp = 13_034_431),
        Stat(statusLevel = 99, level = 99, xp = 13_034_431),
        Stat(statusLevel = 99, level = 99, xp = 13_034_431),
        Stat(statusLevel = 99, level = 99, xp = 13_034_431),
        Stat(statusLevel = 99, level = 99, xp = 13_034_431),
        Stat(statusLevel = 99, level = 99, xp = 13_034_431),
        Stat(statusLevel = 99, level = 99, xp = 13_034_431),
        Stat(statusLevel = 99, level = 99, xp = 13_034_431)
    )

    val changes = mutableMapOf<Int, Stat>()

    override fun initialize(world: World, player: Player) {
        skills.forEachIndexed { id, stat -> player.ctx.write(UpdateStatPacket(id, stat.xp, stat.statusLevel)) }
    }

    override fun synchronize(world: World, player: Player) = changes.map { (id, stat) ->
        player.ctx.write(UpdateStatPacket(id, stat.xp, stat.statusLevel))
    }

    override fun postProcess() = changes.clear()
}