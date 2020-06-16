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

import io.guthix.oldscape.server.net.game.out.UpdateStatPacket
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Player
import io.netty.channel.ChannelFuture
import kotlin.math.pow

class Stat(val id: Int, status: Int, xp: Int, private val changes: MutableList<Stat>) {
    var level: Int = experienceTable.indexOfFirst { xp > it } + 1
        private set

    private var xpToPrevLevel = experienceTable[level]

    private var xpForNextLevel = experienceTable[level]

    var xp: Int = xp
        private set

    var status: Int = status
        set(value) {
            field = value
            changes.add(this)
        }

    init {
        changes.add(this)
    }

    // TODO add level/xp cap
    fun addXp(amount: Int): Boolean {
        xp += amount
        changes.add(this)
        return when {
            xp > xpForNextLevel -> {
                level = experienceTable.indexOfFirst { xp > it } + 1
                xpForNextLevel = experienceTable[level]
                true
            }
            xp < xpToPrevLevel -> {
                level = experienceTable.indexOfFirst { xp > it } + 1
                xpToPrevLevel = experienceTable[level]
                true
            }
            else -> false
        }
    }

    companion object {
        val experienceTable: IntArray = let {
            var xp = 0
            val table = mutableListOf<Int>()
            table.add(xp)
            (2..99).forEach { level ->
                xp += ((level - 1) + 300 * 2.0.pow((level - 1) / 7)).toInt() / 4
                table.add(xp)
            }
            table.toIntArray()
        }
    }
}

class StatManager : InterestManager {
    internal val changes: MutableList<Stat> = mutableListOf()

    val attack: Stat = Stat(id = 0, status = 99, xp = 13_034_431, changes)
    val defence: Stat = Stat(id = 1, status = 99, xp = 13_034_431, changes)
    val strength: Stat = Stat(id = 2, status = 99, xp = 13_034_431, changes)
    val hitpoints: Stat = Stat(id = 3, status = 99, xp = 13_034_431, changes)
    val ranged: Stat = Stat(id = 4, status = 99, xp = 13_034_431, changes)
    val prayer: Stat = Stat(id = 5, status = 99, xp = 13_034_431, changes)
    val magic: Stat = Stat(id = 6, status = 99, xp = 13_034_431, changes)
    val cooking: Stat = Stat(id = 7, status = 99, xp = 13_034_431, changes)
    val woodcutting: Stat = Stat(id = 8, status = 99, xp = 13_034_431, changes)
    val fletching: Stat = Stat(id = 9, status = 99, xp = 13_034_431, changes)
    val fishing: Stat = Stat(id = 10, status = 99, xp = 13_034_431, changes)
    val firemaking: Stat = Stat(id = 11, status = 99, xp = 13_034_431, changes)
    val crafting: Stat = Stat(id = 12, status = 99, xp = 13_034_431, changes)
    val smithing: Stat = Stat(id = 13, status = 99, xp = 13_034_431, changes)
    val mining: Stat = Stat(id = 14, status = 99, xp = 13_034_431, changes)
    val herblore: Stat = Stat(id = 15, status = 99, xp = 13_034_431, changes)
    val agility: Stat = Stat(id = 16, status = 99, xp = 13_034_431, changes)
    val thieving: Stat = Stat(id = 17, status = 99, xp = 13_034_431, changes)
    val slayer: Stat = Stat(id = 18, status = 99, xp = 13_034_431, changes)
    val farming: Stat = Stat(id = 19, status = 99, xp = 13_034_431, changes)
    val runecrafting: Stat = Stat(id = 20, status = 99, xp = 13_034_431, changes)
    val hunter: Stat = Stat(id = 21, status = 99, xp = 13_034_431, changes)
    val construction: Stat = Stat(id = 22, status = 99, xp = 13_034_431, changes)

    override fun initialize(world: World, player: Player) {}

    override fun synchronize(world: World, player: Player): List<ChannelFuture> = changes.map { stat ->
        player.ctx.write(UpdateStatPacket(stat.id, stat.xp, stat.status))
    }

    override fun postProcess(): Unit = changes.clear()
}