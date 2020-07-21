/*
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
 * along with Guthix OldScape-Server. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.gameframe

import io.guthix.oldscape.cache.config.EnumConfig
import io.guthix.oldscape.server.id.Enums
import io.guthix.oldscape.server.world.entity.Player

enum class GameFrame(val interfaceId: Int, val enum: Map<EnumConfig.Component, EnumConfig.Component>) {
    FIXED(interfaceId = 548, enum = Enums.GAMEFRAME_FIXED),
    RESIZABLE_BOX(interfaceId = 161, enum = Enums.GAMEFRAME_RESIZABLE_BOX),
    RESIZABLE_LINE(interfaceId = 164, enum = Enums.GAMEFRAME_RESIZABLE_LINE),
    BLACK_SCREEN(interfaceId = 165, enum = Enums.GAMEFRAME_BLACK_SCREEN);

    companion object {
        fun findByInterfaceId(id: Int): GameFrame = values().first { it.interfaceId == id }
    }
}

private val modalKey: EnumConfig.Component = EnumConfig.Component(161, 15)

fun Player.changeGameFrame(gameFrame: GameFrame) {
    val fromEnum = GameFrame.findByInterfaceId(topInterface.id).enum
    val toEnum = gameFrame.enum
    val moves = mutableMapOf<Int, Int>()
    for ((fromKey, fromValue) in fromEnum) {
        val toValue = toEnum[fromKey]
        if (toValue != null) {
            moves[fromValue.slot] = toValue.slot
        }
    }
    openTopInterface(gameFrame.interfaceId, toEnum[modalKey]?.slot, moves)
}