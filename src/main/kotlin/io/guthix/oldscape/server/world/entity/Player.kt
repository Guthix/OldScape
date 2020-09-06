/*
 * Copyright 2018-2020 Guthix
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.guthix.oldscape.server.world.entity

import io.guthix.oldscape.server.event.Event
import io.guthix.oldscape.server.event.EventHolder
import io.guthix.oldscape.server.event.GameEvent
import io.guthix.oldscape.server.event.PublicMessageEvent
import io.guthix.oldscape.server.net.game.out.*
import io.guthix.oldscape.server.plugin.EventHandler
import io.guthix.oldscape.server.task.Task
import io.guthix.oldscape.server.template.VarbitTemplate
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.interest.*
import io.guthix.oldscape.server.world.entity.intface.IfComponent
import io.guthix.oldscape.server.world.map.dim.TileUnit
import io.guthix.oldscape.server.world.map.dim.tiles
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelHandlerContext
import java.util.concurrent.ConcurrentLinkedQueue

class Player(
    var priority: Int,
    var ctx: ChannelHandlerContext,
    val username: String,
    val clientSettings: ClientSettings,
    private val playerManager: PlayerManager,
    internal val npcManager: NpcManager, //TODO stop exposing
    internal val mapManager: MapManager,
    private val contextMenuManager: ContextMenuManager,
    private val varpManager: VarpManager,
    val stats: StatManager,
    private val energyManager: EnergyManager
) : Character(playerManager.index), Comparable<Player>, EventHolder {
    override val updateFlags = sortedSetOf<PlayerUpdateType>()

    override val events: ConcurrentLinkedQueue<EventHandler<Event>> = ConcurrentLinkedQueue()

    var isLoggingOut: Boolean = false

    val contextMenu: Array<String> get() = contextMenuManager.contextMenu

    var topInterface: TopInterfaceManager = TopInterfaceManager(ctx, id = 165)
        private set

    var nameModifiers: Array<String> = arrayOf("", "", "")

    override var orientation: Int = 0

    val gender: PlayerManager.Gender = PlayerManager.Gender.MALE

    var isSkulled: Boolean = false

    val prayerIcon: Int = -1

    var rights: Int = 2

    val combatLevel: Int = 126

    val style: PlayerManager.Style = PlayerManager.Style(
        hair = 0,
        beard = 10,
        torso = 18,
        arms = 26,
        legs = 36,
        hands = 33,
        feet = 42
    )

    val colours: PlayerManager.Colours = PlayerManager.Colours(0, 0, 0, 0, 0)

    val equipmentSet: PlayerManager.EquipmentSet = PlayerManager.EquipmentSet(mutableMapOf())

    data class StanceSequences(
        var stand: Int,
        var turn: Int,
        var walk: Int,
        var turn180: Int,
        var turn90CW: Int,
        var turn90CCW: Int,
        var run: Int
    )

    val animations: StanceSequences = StanceSequences(
        stand = 808,
        turn = 823,
        walk = 819,
        turn180 = 820,
        turn90CW = 821,
        turn90CCW = 822,
        run = 824
    )

    override var inRunMode: Boolean = super.inRunMode
        set(value) {
            field = value
            updateFlags.add(PlayerInfoPacket.movementCached)
        }

    override val size: TileUnit = 1.tiles

    var weight: Int
        get() = energyManager.weight
        set(value) {
            energyManager.weight = value
        }

    var energy: Int
        get() = energyManager.energy
        set(value) {
            energyManager.energy = value
        }

    override fun processTasks() {
        while (true) {
            while (events.isNotEmpty()) events.poll().handle()
            val resumed = tasks.values.flatMap { routineList -> routineList.toList().map(Task::run) } // TODO optimize
            if (resumed.all { !it } && events.isEmpty()) break // TODO add live lock detection
        }
    }

    fun initialize(world: World) {
        playerManager.initialize(world, this)
        mapManager.initialize(world, this)
        val xteas = mapManager.getInterestedXteas(world.map)
        ctx.write(InterestInitPacket(world.players, this, xteas, pos.x.inZones, pos.y.inZones))
        updateFlags.add(PlayerInfoPacket.appearance)
        updateFlags.add(PlayerInfoPacket.orientation)
        updateFlags.add(PlayerInfoPacket.nameModifiers)
        npcManager.initialize(world, this)
        topInterface.initialize(world, this)
        contextMenuManager.initialize(world, this)
        varpManager.initialize(world, this)
        stats.initialize(world, this)
        energyManager.initialize(world, this)
    }

    fun synchronize(world: World): List<ChannelFuture> {
        val futures = mutableListOf<ChannelFuture>()
        futures.addAll(topInterface.synchronize(world, this))
        futures.addAll(contextMenuManager.synchronize(world, this))
        futures.addAll(varpManager.synchronize(world, this))
        futures.addAll(stats.synchronize(world, this))
        futures.addAll(energyManager.synchronize(world, this))
        futures.addAll(mapManager.synchronize(world, this))
        futures.addAll(npcManager.synchronize(world, this))
        futures.addAll(playerManager.synchronize(world, this))
        ctx.flush()
        return futures
    }

    override fun postProcess() {
        super.postProcess()
        topInterface.postProcess()
        contextMenuManager.postProcess()
        varpManager.postProcess()
        stats.postProcess()
        energyManager.postProcess()
        mapManager.postProcess()
        npcManager.postProcess()
        playerManager.postProcess()
    }

    fun openTopInterface(id: Int, modalSlot: Int? = null, moves: Map<Int, Int> = mutableMapOf()): TopInterfaceManager {
        val movedChildren = mutableMapOf<Int, IfComponent>()
        ctx.write(IfOpentopPacket(id))
        for ((fromSlot, toSlot) in moves) {
            movedChildren[toSlot] = topInterface.children[fromSlot] ?: continue
            ctx.write(IfMovesubPacket(topInterface.id, fromSlot, id, toSlot))
        }
        topInterface.modalSlot?.let { curModalSlot ->
            modalSlot?.let { newModalSlot ->
                if (topInterface.modalOpen && curModalSlot != newModalSlot) {
                    ctx.write(IfMovesubPacket(topInterface.id, curModalSlot, id, newModalSlot))
                }
            }
        }
        topInterface = TopInterfaceManager(ctx, id, topInterface.modalOpen, modalSlot, movedChildren)
        return topInterface
    }

    fun talk(message: PublicMessageEvent) {
        publicMessage = message
        shoutMessage = null
        updateFlags.add(PlayerInfoPacket.chat)
        cancelTasks(ChatTask)
        addTask(ChatTask) {
            wait(ticks = PlayerManager.MESSAGE_DURATION - 1)
            addPostTask { publicMessage = null }
        }
    }

    fun senGameMessage(message: String) {
        ctx.write(MessageGamePacket(0, false, message))
    }

    fun updateAppearance() {
        updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun updateVarp(id: Int, value: Int): Unit = varpManager.updateVarp(id, value)

    fun updateVarbit(template: VarbitTemplate, value: Int): Unit = varpManager.updateVarbit(template, value)

    fun runClientScript(id: Int, vararg args: Any) {
        ctx.write(RunclientscriptPacket(id, *args))
    }

    fun setMapFlag(x: TileUnit, y: TileUnit) {
        ctx.write(SetMapFlagPacket(x - mapManager.baseX.inTiles, y - mapManager.baseY.inTiles))
    }

    override fun addOrientationFlag(): Boolean = updateFlags.add(PlayerInfoPacket.orientation)

    override fun addTurnToLockFlag(): Boolean = updateFlags.add(PlayerInfoPacket.lockTurnTo)

    override fun addSequenceFlag(): Boolean = updateFlags.add(PlayerInfoPacket.sequence)

    override fun checkSequenceFlag(): Boolean = updateFlags.contains(PlayerInfoPacket.sequence)

    override fun addSpotAnimationFlag(): Boolean = updateFlags.add(PlayerInfoPacket.spotAnimation)

    override fun addHitUpdateFlag(): Boolean = updateFlags.add(PlayerInfoPacket.hit)

    override fun addShoutFlag(): Boolean = updateFlags.add(PlayerInfoPacket.shout)

    internal fun stageLogout() {
        isLoggingOut = true
        events.clear()
        tasks.clear()
        ctx.writeAndFlush(LogoutFullPacket())
    }

    override fun compareTo(other: Player): Int = when {
        priority < other.priority -> -1
        priority > other.priority -> 1
        else -> 0
    }

    fun clearMap(): Unit = mapManager.clear(this)
}