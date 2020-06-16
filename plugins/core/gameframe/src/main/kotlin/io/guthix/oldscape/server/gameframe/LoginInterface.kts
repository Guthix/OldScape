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

import io.guthix.oldscape.server.event.PlayerInitialized
import io.guthix.oldscape.server.world.entity.intface.Interface

on(PlayerInitialized::class).then {
    val topInterface = player.openTopInterface(id = 165)
    topInterface.openSubInterface(slot = 1, subId = 162, type = Interface.Type.CLIENTINTERFACE)
    topInterface.openSubInterface(slot = 2, subId = 651, type = Interface.Type.CLIENTINTERFACE)
    topInterface.openSubInterface(slot = 25, subId = 163, type = Interface.Type.CLIENTINTERFACE)
    topInterface.openSubInterface(slot = 26, subId = 160, type = Interface.Type.CLIENTINTERFACE)
    topInterface.openSubInterface(slot = 29, subId = 378, type = Interface.Type.OVERLAYINTERFACE)
        .setText(73, "You have a Bank PIN!")
        .setText(7, "Welcome to OldScape Emulator!")
    topInterface.openSubInterface(slot = 11, subId = 320, type = Interface.Type.CLIENTINTERFACE)
    topInterface.openSubInterface(slot = 12, subId = 629, type = Interface.Type.CLIENTINTERFACE)
        .openSubInterface(33, subId = 399, type = Interface.Type.CLIENTINTERFACE)
    topInterface.openSubInterface(slot = 13, subId = 149, type = Interface.Type.CLIENTINTERFACE)
    topInterface.openSubInterface(slot = 14, subId = 387, type = Interface.Type.CLIENTINTERFACE)
    topInterface.openSubInterface(slot = 15, subId = 541, type = Interface.Type.CLIENTINTERFACE)
    topInterface.openSubInterface(slot = 16, subId = 218, type = Interface.Type.CLIENTINTERFACE)
    topInterface.openSubInterface(slot = 19, subId = 429, type = Interface.Type.CLIENTINTERFACE)
    topInterface.openSubInterface(slot = 18, subId = 109, type = Interface.Type.CLIENTINTERFACE)
    topInterface.openSubInterface(slot = 20, subId = 182, type = Interface.Type.CLIENTINTERFACE)
    topInterface.openSubInterface(slot = 21, subId = 261, type = Interface.Type.CLIENTINTERFACE)
    topInterface.openSubInterface(slot = 22, subId = 216, type = Interface.Type.CLIENTINTERFACE)
    topInterface.openSubInterface(slot = 23, subId = 239, type = Interface.Type.CLIENTINTERFACE)
    topInterface.openSubInterface(slot = 17, subId = 7, type = Interface.Type.CLIENTINTERFACE)
    topInterface.openSubInterface(slot = 10, subId = 593, type = Interface.Type.CLIENTINTERFACE)
    player.runClientScript(233, -1, 2100, 0, 1897, 330, -200, 5, 28238, 24772664) // cake
    player.runClientScript(233, 8446, 1200, 0, 177, 1999, 0, 0, 38593, 24772665) // npc head
}