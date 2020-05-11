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
package io.guthix.oldscape.server.world

import io.guthix.oldscape.server.event.LoginEvent
import io.guthix.oldscape.server.event.script.EventBus
import io.guthix.oldscape.server.net.game.GameDecoder
import io.guthix.oldscape.server.net.game.GameEncoder
import io.guthix.oldscape.server.net.game.GameHandler
import io.guthix.oldscape.server.net.login.*
import io.netty.util.concurrent.*
import java.util.concurrent.*
import java.util.*

class World : TimerTask() {
    val map = WorldMap(mutableMapOf())

    internal val loginQueue = ConcurrentLinkedQueue<LoginRequest>()

    val players = PlayerList(MAX_PLAYERS)

    val isFull get(): Boolean = players.size + loginQueue.size >= MAX_PLAYERS

    override fun run() {
        processLogins()
        processPlayerEvents()
        proccessMovment()
        synchronizeInterest()
    }

    private fun processLogins() {
        while (loginQueue.isNotEmpty()) {
            val request = loginQueue.poll()
            val player = players.create(request)
            player.clientSettings = request.clientSettings
            request.ctx.writeAndFlush(LoginResponse(player.index, player.visualInterestManager.rights))
            request.ctx.pipeline().replace(LoginDecoder::class.qualifiedName, GameDecoder::class.qualifiedName,
                GameDecoder(request.isaacPair.decodeGen)
            )
            request.ctx.pipeline().replace(LoginHandler::class.qualifiedName, GameHandler::class.qualifiedName,
                GameHandler(this, player)
            )
            request.ctx.pipeline().replace(LoginEncoder::class.qualifiedName, GameEncoder::class.qualifiedName,
                GameEncoder(request.isaacPair.encodeGen)
            )
            EventBus.schedule(LoginEvent(), player, this)
        }
    }

    private fun processPlayerEvents() {
        for (player in players) player.processInEvents()
    }

    private fun proccessMovment() {
        for (player in players) player.visualInterestManager.move()
    }

    private fun synchronizeInterest() {
        val futures = PromiseCombiner(ImmediateEventExecutor.INSTANCE)
        players.forEach { it.synchronize(this).forEach { futures.add(it) } }
        futures.finish(DefaultPromise<Void>(ImmediateEventExecutor.INSTANCE).addListener {
            for (player in players) player.postProcess()
        })
    }

    companion object {
        const val MAX_PLAYERS = 2048
    }
}