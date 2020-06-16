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
package io.guthix.oldscape.server.gameframe

import io.guthix.oldscape.server.api.Enums
import io.guthix.oldscape.server.api.readComponent
import io.guthix.oldscape.server.world.entity.Player

enum class GameFrame(val interfaceId: Int, val enumId: Int) {
    FIXED(interfaceId = 548, enumId = 1129),
    RESIZABLE_BOX(interfaceId = 161, enumId = 1130),
    RESIZABLE_LINE(interfaceId = 164, enumId = 1131),
    BLACK_SCREEN(interfaceId = 165, enumId = 1132);

    companion object {
        fun findByInterfaceId(id: Int): GameFrame = values().first { it.interfaceId == id }
    }
}

private const val modalKey: Int = (161 shl Short.SIZE_BITS) or 15

fun Player.changeGameFrame(gameFrame: GameFrame) {
    val fromEnum = Enums[GameFrame.findByInterfaceId(topInterface.id).enumId]
    val toEnum = Enums[gameFrame.enumId]
    val moves = mutableMapOf<Int, Int>()
    for ((key, value) in fromEnum.keyValuePairs) {
        val from = readComponent(value as Int)
        val to = readComponent(toEnum.keyValuePairs[key] as Int)
        if (from != null && to != null) {
            moves[from.slot] = to.slot
        }
    }
    val modalSlot = readComponent(toEnum.keyValuePairs[modalKey] as Int)?.slot
    openTopInterface(gameFrame.interfaceId, modalSlot, moves)
}