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

import io.guthix.oldscape.server.event.ButtonClickEvent
import io.guthix.oldscape.server.event.WindowStatusEvent

on(WindowStatusEvent::class).then {
    player.clientSettings.width = event.width
    player.clientSettings.height = event.height
    if (player.clientSettings.resizable != event.isResized) {
        player.clientSettings.resizable = event.isResized
        if (player.clientSettings.resizable) {
            player.changeGameFrame(GameFrame.RESIZABLE_BOX)
        } else {
            player.changeGameFrame(GameFrame.FIXED)
        }
    }
}

on(ButtonClickEvent::class).where { event.interfaceId == 378 && event.buttonId == 78 }.then {
    player.topInterface.closeComponent(28)
    if (player.clientSettings.resizable) {
        player.changeGameFrame(GameFrame.RESIZABLE_BOX)
    } else {
        player.changeGameFrame(GameFrame.FIXED)
    }
}