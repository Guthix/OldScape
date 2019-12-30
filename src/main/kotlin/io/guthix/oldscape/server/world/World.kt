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
package io.guthix.oldscape.server.world

import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.event.imp.LoginEvent
import io.guthix.oldscape.server.net.state.game.GameDecoder
import io.guthix.oldscape.server.net.state.game.GameEncoder
import io.guthix.oldscape.server.net.state.game.GameHandler
import io.guthix.oldscape.server.net.state.login.*
import io.guthix.oldscape.server.world.entity.character.player.PlayerList
import java.util.*
import java.util.concurrent.*

class World : TimerTask() {
    internal val loginQueue = ConcurrentLinkedQueue<LoginRequest>()

    val players = PlayerList(MAX_PLAYERS)

    val isFull get(): Boolean = players.size + loginQueue.size >= MAX_PLAYERS

    override fun run() {
        processLogins()
        processPlayerEvents()
    }

    private fun processLogins() {
        while(loginQueue.isNotEmpty()) {
            val request = loginQueue.poll()
            val player= players.create(request)
            request.ctx.writeAndFlush(LoginResponse(player.index, player.rights))
            request.ctx.pipeline().replace(LoginDecoder::class.qualifiedName, GameDecoder::class.qualifiedName,
                GameDecoder(request.isaacPair.decodeGen)
            )
            request.ctx.pipeline().replace(LoginHandler::class.qualifiedName, GameHandler::class.qualifiedName,
                GameHandler(this, player)
            )
            request.ctx.pipeline().replace(LoginEncoder::class.qualifiedName, GameEncoder::class.qualifiedName,
                GameEncoder(request.isaacPair.encodeGen)
            )
            EventBus.schedule(LoginEvent(), this, player)
        }
    }

    private fun processPlayerEvents() {
        for(player in players) player.handleEvents()
    }

    companion object {
        const val MAX_PLAYERS = 2048
    }
}