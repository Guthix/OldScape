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
package io.guthix.oldscape.server.net.state.service

import io.guthix.oldscape.server.net.IncPacket

enum class ServiceType(val opcode: Int) {
    GAME(14), JS5(15);
}

class GameConnectionRequest : IncPacket

class Js5ConnectionRequest(val revision: Int) : IncPacket