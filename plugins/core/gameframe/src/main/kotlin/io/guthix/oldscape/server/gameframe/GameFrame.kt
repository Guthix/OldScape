/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.gameframe

import io.guthix.oldscape.server.api.Enums
import io.guthix.oldscape.server.api.readComponent
import io.guthix.oldscape.server.world.entity.EntityAttribute
import io.guthix.oldscape.server.world.entity.character.player.Player

var Player.gameframe by EntityAttribute<GameFrame>()

enum class GameFrame(val enumId: Int) {
    FIXED(1129), RESIZABLE_BOX(1130), RESIZABLE_LINE(1131), BLACK_SCREEN(1132)
}

fun Player.changeGameFrame(toGameFrame: GameFrame) {
    val fromEnum = Enums[gameframe.enumId]
    val toEnum = Enums[toGameFrame.enumId]
    val topInterface = toEnum.keyValuePairs.values.first() as Int shr Short.SIZE_BITS
    setTopInterface(topInterface)
    for((key, value) in fromEnum.keyValuePairs) {
        val from = readComponent(value as Int)
        val to = readComponent(toEnum.keyValuePairs[key] as Int)
        if(from != null && to != null) {
            moveSubInterface(from.interfaceId, from.componentId, to.interfaceId, to.componentId)
        }
    }
    gameframe = toGameFrame
}