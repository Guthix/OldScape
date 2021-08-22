/*
 * Copyright 2018-2021 Guthix
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

import io.guthix.oldscape.dim.TileUnit
import io.guthix.oldscape.dim.floors
import io.guthix.oldscape.dim.tiles
import io.guthix.oldscape.server.PersistentProperty
import io.guthix.oldscape.server.PersistentPropertyHolder
import io.guthix.oldscape.server.ServerContext
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.event.EventHolder
import io.guthix.oldscape.server.event.PlayerMovedEvent
import io.guthix.oldscape.server.event.PublicMessageEvent
import io.guthix.oldscape.server.net.game.out.*
import io.guthix.oldscape.cache.InventoryIds
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.interest.*
import io.guthix.oldscape.server.world.entity.intface.IfComponent
import io.guthix.oldscape.server.world.map.Tile
import io.guthix.oldscape.server.world.map.Zone
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelHandlerContext

class Player internal constructor(
    val uid: Int,
    var priority: Int,
    var ctx: ChannelHandlerContext,
    val username: String,
    val clientSettings: ClientSettings,
    override var zone: Zone,
    override val persistentProperties: MutableMap<String, Any>,
    private val playerManager: PlayerManager,
    private val npcManager: NpcManager,
    private val sceneManager: SceneManager,
    private val energyManager: EnergyManager,
    private val contextMenuManager: ContextMenuManager,
    private val varpManager: VarpManager,
    private val statManager: StatManager,
    private val interfaceManager: TopInterfaceManager,
) : Character(playerManager.index), Comparable<Player>, EventHolder, PersistentPropertyHolder {
    init {
        zone.players.add(this)
    }

    override var pos: Tile by PersistentProperty { Tile(0.floors, 3235.tiles, 3222.tiles) }

    override var spawnPos: Tile = defaultSpawn

    override var orientation: Int by PersistentProperty { 0 }

    val localNpcs: List<Npc> get() = npcManager.localNpcs

    val gender: Gender by PersistentProperty { Gender.MALE }

    val colours: Colours by PersistentProperty { Colours(0, 0, 0, 0, 0) }

    val style: Style by PersistentProperty {
        Style(
            hair = 0,
            beard = 10,
            torso = 18,
            arms = 26,
            legs = 36,
            hands = 33,
            feet = 42
        )
    }

    val itemBag: InventoryManager by PersistentProperty {
        InventoryManager(InventoryIds.ITEM_BAG_93, TopInterfaceManager.INVENTORY_IFID, 0)
    }

    val equipment: EquipmentManager by PersistentProperty {
        EquipmentManager()
    }

    var rights: Int by PersistentProperty { 2 }

    var isLoggingOut: Boolean = false

    val contextMenu: Array<String> get() = contextMenuManager.contextMenu

    val topInterface: TopInterfaceManager get() = interfaceManager

    internal val scene: SceneManager get() = sceneManager

    val stats: StatManager get() = statManager

    var nameModifiers: Array<String> = arrayOf("", "", "")

    var isSkulled: Boolean = false

    val prayerIcon: Int = -1

    val combatLevel: Int = 126

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

    override val updateFlags = sortedSetOf<PlayerUpdateType>()

    fun initialize(world: World) {
        playerManager.initialize(world, this)
        sceneManager.initialize(world, this)
        val xteas = sceneManager.getInterestedXteas(world.xteas)
        ctx.write(InterestInitPacket(world.players, this, xteas, pos.x.inZones, pos.y.inZones))
        updateFlags.add(PlayerInfoPacket.appearance)
        updateFlags.add(PlayerInfoPacket.orientation)
        updateFlags.add(PlayerInfoPacket.nameModifiers)
        itemBag.initialize()
        equipment.initialize()
        contextMenuManager.initialize(this)
        statManager.initialize(world, this)
        energyManager.initialize(this)
    }

    internal fun synchronize(world: World): List<ChannelFuture> {
        val futures = mutableListOf<ChannelFuture>()
        futures.addAll(itemBag.synchronize(this))
        futures.addAll(equipment.synchronize(this))
        futures.addAll(contextMenuManager.synchronize(this))
        futures.addAll(varpManager.synchronize(this))
        futures.addAll(statManager.synchronize(world, this))
        futures.addAll(energyManager.synchronize(this))
        futures.addAll(sceneManager.synchronize(world, world.xteas, this))
        futures.addAll(playerManager.synchronize(world, this))
        futures.addAll(npcManager.synchronize(world, this))
        ctx.flush()
        return futures
    }

    override fun postProcess() {
        super.postProcess()
        itemBag.postProcess()
        equipment.postProcess()
        contextMenuManager.postProcess()
        varpManager.postProcess()
        statManager.postProcess()
        energyManager.postProcess()
        sceneManager.postProcess()
    }

    override fun scheduleMovedEvent(world: World) {
        EventBus.schedule(PlayerMovedEvent(lastPos, this, world))
    }

    override fun moveZone(from: Zone, to: Zone) {
        from.players.remove(this)
        to.players.add(this)
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
        topInterface.id = id
        topInterface.modalSlot = modalSlot
        topInterface.children = movedChildren
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

    fun sendGameMessage(message: String) {
        ctx.write(MessageGamePacket(0, false, message))
    }

    fun updateAppearance() {
        updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun updateVarp(id: Int, value: Int): Unit = varpManager.updateVarp(id, value)

    fun updateVarbit(id: Int, value: Int): Unit = varpManager.updateVarbit(ServerContext.varbitTemplates[id], value)

    fun runClientScript(id: Int, vararg args: Any) {
        ctx.write(RunclientscriptPacket(id, *args))
    }

    fun setMapFlag(x: TileUnit, y: TileUnit) {
        ctx.write(SetMapFlagPacket(x - sceneManager.baseX.inTiles, y - sceneManager.baseY.inTiles))
    }

    fun playSong(id: Int) {
        ctx.write(MidiSongPacket(id))
    }

    override fun addTemporaryMovementFlag(): Boolean = updateFlags.add(PlayerInfoPacket.movementTemporary)

    override fun addOrientationFlag(): Boolean = updateFlags.add(PlayerInfoPacket.orientation)

    override fun addTurnToLockFlag(): Boolean = updateFlags.add(PlayerInfoPacket.lockTurnTo)

    override fun addSequenceFlag(): Boolean = updateFlags.add(PlayerInfoPacket.sequence)

    override fun checkSequenceFlag(): Boolean = updateFlags.contains(PlayerInfoPacket.sequence)

    override fun addSpotAnimationFlag(): Boolean = updateFlags.add(PlayerInfoPacket.spotAnimation)

    override fun addHitUpdateFlag(): Boolean = updateFlags.add(PlayerInfoPacket.hit)

    override fun addShoutFlag(): Boolean = updateFlags.add(PlayerInfoPacket.shout)

    internal fun stageLogout(forced: Boolean) {
        isLoggingOut = true
        events.clear()
        tasks.clear()
        postTasks.clear()
        if (!forced) ctx.writeAndFlush(LogoutFullPacket())
    }

    override fun compareTo(other: Player): Int = when {
        priority < other.priority -> -1
        priority > other.priority -> 1
        else -> 0
    }

    fun clearMap(): Unit = sceneManager.clear(this)

    override fun toString(): String = "Player(uid=$uid, index=$index, priority=$priority, name=$username)"

    companion object {
        internal val defaultSpawn: Tile = Tile(0.floors, 3235.tiles, 3222.tiles)
    }
}