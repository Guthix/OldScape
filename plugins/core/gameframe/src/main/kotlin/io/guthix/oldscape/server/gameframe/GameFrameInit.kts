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
package io.guthix.oldscape.server.gameframe

import io.guthix.oldscape.server.event.imp.ButtonClickEvent
import io.guthix.oldscape.server.event.imp.WindowStatusEvent

on(WindowStatusEvent::class).then {
    player.clientSettings.width = event.width
    player.clientSettings.height = event.height
    if(player.clientSettings.resizable != event.isResized) {
        player.clientSettings.resizable = event.isResized
        if(player.clientSettings.resizable) {
            player.changeGameFrame(GameFrame.RESIZABLE_BOX)
        } else {
            player.changeGameFrame(GameFrame.FIXED)
        }
    }
}

on(ButtonClickEvent::class).where { event.interfaceId == 378 && event.buttonId == 81 }.then {
    player.closeSubInterface(165, 28)
    if(player.clientSettings.resizable) {
        player.changeGameFrame(GameFrame.RESIZABLE_BOX)
    } else {
        player.changeGameFrame(GameFrame.FIXED)
    }
}