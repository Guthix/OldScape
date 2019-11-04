/*
 * This file is part of Guthix OldScape.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Guthix OldScape. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.net.state.js5

import io.guthix.oldscape.server.net.IncPacket

enum class Js5Type(val opcode: Int) {
    NORMAL_CONTAINER_REQUEST(0),
    PRIORITY_CONTAINER_REQUEST(1),
    CLIENT_LOGGED_IN(2),
    CLIENT_LOGGED_OUT(3),
    ENCRYPTION_KEY_UPDATE(4);
}

class Js5FileRequest(val isPriority: Boolean, val indexFileId: Int, val containerId: Int) : IncPacket