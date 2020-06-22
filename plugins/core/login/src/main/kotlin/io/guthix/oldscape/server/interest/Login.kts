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
package io.guthix.oldscape.server.interest

import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.event.LoginEvent
import io.guthix.oldscape.server.event.PlayerInitialized

on(LoginEvent::class).then {
    player.initialize(world)
    player.updateVarbit(id = 8119, value = 1) // enable chat
    player.updateVarp(id = 1306, value = 2) // set attack option for npcs (0, 1, 2, 3)
    player.updateVarp(id = 1107, value = 2) // set attack option for players (0, 1, 2, 3)
    player.senGameMessage("Welcome to OldScape Emulator!")
    EventBus.schedule(PlayerInitialized(player, world))
}