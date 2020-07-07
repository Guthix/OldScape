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
package io.guthix.oldscape.server.world

import io.guthix.oldscape.server.event.*
import io.guthix.oldscape.server.net.game.GameDecoder
import io.guthix.oldscape.server.net.game.GameEncoder
import io.guthix.oldscape.server.net.game.GameHandler
import io.guthix.oldscape.server.net.login.*
import io.guthix.oldscape.server.plugin.EventHandler
import io.guthix.oldscape.server.task.*
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.map.Tile
import io.netty.util.concurrent.DefaultPromise
import io.netty.util.concurrent.ImmediateEventExecutor
import io.netty.util.concurrent.PromiseCombiner
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.LinkedBlockingDeque
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted

class World : TimerTask(), TaskHolder, EventHolder {
    val map: WorldMap = WorldMap(mutableMapOf())

    override val events: LinkedBlockingDeque<EventHandler<GameEvent>> = LinkedBlockingDeque()

    override val tasks: MutableMap<TaskType, MutableList<Task>> = mutableMapOf()

    internal val loginQueue = ConcurrentLinkedQueue<LoginRequest>()

    internal val logoutQueue = ConcurrentLinkedQueue<Player>()

    val players: PlayerList = PlayerList(MAX_PLAYERS)

    val npcs: NpcList = NpcList(MAX_NPCS)

    val isFull: Boolean get() = players.size + loginQueue.size >= MAX_PLAYERS

    override fun run() {
        processInEvents()
        processLogins()
        processNpcTasks()
        proccessNpcMovement()
        processPlayerTasks()
        proccessPlayerMovement()
        synchronizeInterest()
        processLogouts()
        postProcess()
    }

    private fun processInEvents() {
        while (true) {
            while (events.isNotEmpty()) events.poll().handle()
            val resumed = tasks.values.flatMap { routineList -> routineList.toList().map(Task::run) } // TODO optimize
            if (resumed.all { !it } && events.isEmpty()) break // TODO add live lock detection
        }
    }

    private fun processLogins() {
        while (loginQueue.isNotEmpty()) {
            val request = loginQueue.poll()
            val player = players.create(request)
            request.ctx.writeAndFlush(LoginResponse(player.index, player.rights))
            request.ctx.pipeline().replace(LoginDecoder::class.qualifiedName, GameDecoder::class.qualifiedName,
                GameDecoder(request.isaacPair.decodeGen, player, this)
            )
            request.ctx.pipeline().replace(LoginHandler::class.qualifiedName, GameHandler::class.qualifiedName,
                GameHandler(player)
            )
            request.ctx.pipeline().replace(LoginEncoder::class.qualifiedName, GameEncoder::class.qualifiedName,
                GameEncoder(request.isaacPair.encodeGen)
            )
            EventBus.schedule(LoginEvent(player, this))
        }
    }

    fun addNpc(id: Int, tile: Tile) {
        val npc = npcs.create(id, tile)
        EventBus.schedule(NpcSpawnedEvent(npc, this))
    }

    fun stagePlayerLogout(player: Player) {
        player.stageLogout()
        logoutQueue.add(player)
    }

    private fun processLogouts() {
        while (logoutQueue.isNotEmpty()) {
            val player = logoutQueue.poll()
            players.remove(player)
        }
    }

    private fun processNpcTasks() {
        for (npc in npcs) npc.processTasks()
    }

    private fun processPlayerTasks() {
        for (player in players) player.processTasks()
    }

    private fun proccessNpcMovement() {
        for (npc in npcs) npc.move()
    }

    private fun proccessPlayerMovement() {
        for (player in players) player.move()
    }

    private fun synchronizeInterest() {
        val futures = PromiseCombiner(ImmediateEventExecutor.INSTANCE)
        players.forEach { it.synchronize(this).forEach(futures::add) }
        futures.finish(DefaultPromise<Void>(ImmediateEventExecutor.INSTANCE).addListener {
            for (player in players) player.postProcess()
            for (npc in npcs) npc.postProcess()
        })
    }

    companion object {
        const val MAX_PLAYERS: Int = 2048

        const val MAX_NPCS: Int = 32768
    }
}