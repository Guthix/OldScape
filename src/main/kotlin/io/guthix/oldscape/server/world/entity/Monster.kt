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

import io.guthix.oldscape.server.net.game.out.NpcInfoSmallViewportPacket
import io.guthix.oldscape.server.world.map.Tile

class Monster(index: Int, id: Int, pos: Tile, override val visual: MonsterVisual) : Npc(index, id, pos, visual) {
    fun hit(colour: HitMark.Colour, damage: Int, delay: Int) {
        visual.updateFlags.add(NpcInfoSmallViewportPacket.hit)
        visual.hitMarkQueue.add(HitMark(colour, damage, delay))
        visual.healthBarQueue.add(HealthBar(2, 0, 0, 100))
    }

    override fun postProcess() {
        super.postProcess()
        visual.hitMarkQueue.clear()
        visual.healthBarQueue.clear()
    }
}

class MonsterVisual : NpcVisual() {
    var health = 100

    val hitMarkQueue = mutableListOf<HitMark>()

    val healthBarQueue = mutableListOf<HealthBar>()
}