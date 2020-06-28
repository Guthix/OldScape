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

import io.guthix.oldscape.server.api.Varbits
import io.guthix.oldscape.server.net.game.out.VarpLargePacket
import io.guthix.oldscape.server.net.game.out.VarpSmallPacket
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Player
import io.netty.channel.ChannelFuture
import kotlin.math.pow

class VarpManager : InterestManager {
    val varps: MutableMap<Int, Int> = mutableMapOf()

    private val changes = mutableMapOf<Int, Int>()

    fun updateVarp(id: Int, value: Int) {
        varps[id] = value
        changes[id] = value
    }

    fun updateVarbit(id: Int, value: Int) {
        val config = Varbits[id]
        val bitSize = (config.msb.toInt() - config.lsb.toInt()) + 1
        if (value > 2.0.pow(bitSize) - 1) throw IllegalArgumentException("Value $value to big for this varbit.")
        var curVarp = varps[config.varpId] ?: 0
        curVarp = curVarp.clearBits(config.msb.toInt(), config.lsb.toInt())
        curVarp = curVarp or value shl config.lsb.toInt()
        varps[config.varpId] = curVarp
        changes[config.varpId] = curVarp
    }

    private fun Int.setBits(msb: Int, lsb: Int): Int = this xor ((1 shl (msb + 1)) - 1) xor ((1 shl lsb) - 1)

    @Suppress("INTEGER_OVERFLOW")
    private fun Int.clearBits(msb: Int, lsb: Int) = ((1 shl 4 * 8 - 1) - 1).setBits(msb, lsb) and this

    override fun initialize(world: World, player: Player) {}

    override fun synchronize(world: World, player: Player): List<ChannelFuture> = changes.map { (id, value) ->
        if (value <= Byte.MIN_VALUE || value >= Byte.MAX_VALUE) {
            player.ctx.write(VarpLargePacket(id, value))
        } else {
            player.ctx.write(VarpSmallPacket(id, value))
        }
    }

    override fun postProcess(): Unit = changes.clear()
}