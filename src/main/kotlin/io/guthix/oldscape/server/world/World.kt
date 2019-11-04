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
package io.guthix.oldscape.server.world

import io.guthix.oldscape.server.net.state.login.LoginRequest
import io.guthix.oldscape.server.world.entity.player.PlayerList
import java.util.*
import java.util.concurrent.*

class World : TimerTask() {
    internal val loginQueue = SynchronousQueue<LoginRequest>()

    internal val players = PlayerList(MAX_PLAYERS)

    override fun run() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun processLogins() {
        while(loginQueue.peek() != null) {
            val request = loginQueue.poll()
            players.add(request)
        }
    }

    companion object {
        const val MAX_PLAYERS = 2048
    }
}