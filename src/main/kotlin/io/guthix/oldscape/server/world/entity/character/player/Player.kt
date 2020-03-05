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
package io.guthix.oldscape.server.world.entity.character.player

import io.guthix.oldscape.server.api.Varbits
import io.guthix.oldscape.server.event.PublicMessageEvent
import io.guthix.oldscape.server.routine.Routine
import io.guthix.oldscape.server.routine.ConditionalContinuation
import io.guthix.oldscape.server.routine.InitialCondition
import io.guthix.oldscape.server.net.state.game.outp.*
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.WorldMap
import io.guthix.oldscape.server.world.entity.Entity
import io.guthix.oldscape.server.world.entity.Obj
import io.guthix.oldscape.server.world.entity.character.Character
import io.guthix.oldscape.server.world.entity.character.SpotAnimation
import io.guthix.oldscape.server.world.entity.character.player.interest.MapInterest
import io.guthix.oldscape.server.world.entity.character.player.interest.PlayerInterest
import io.guthix.oldscape.server.world.entity.character.player.intface.IfComponent
import io.guthix.oldscape.server.world.entity.character.player.intface.Interface
import io.guthix.oldscape.server.world.entity.character.player.intface.TopInterface
import io.guthix.oldscape.server.world.entity.character.player.intface.component.SubInterface
import io.guthix.oldscape.server.world.mapsquare.zone.Zone
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import io.guthix.oldscape.server.world.mapsquare.zone.tile.TileUnit
import io.guthix.oldscape.server.world.mapsquare.zone.tile.tiles
import io.guthix.oldscape.server.world.mapsquare.zone.zones
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelHandlerContext
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

data class Player(
    override val index: Int,
    var priority: Int,
    override var position: Tile,
    val username: String,
    var ctx: ChannelHandlerContext,
    override val attributes: MutableMap<KProperty<*>, Any?> = mutableMapOf()
) : Character(index, position, attributes), Comparable<Player> {
    val inEvents = ConcurrentLinkedQueue<() -> Unit>()

    var topInterface: TopInterface = TopInterface(ctx, id = 165)

    fun openTopInterface(id: Int, modalSlot: Int? = null, moves: Map<Int, Int> = mutableMapOf()): TopInterface {
        val movedChildren = mutableMapOf<Int, IfComponent>()
        ctx.write(IfOpentopPacket(id))
        for((fromSlot, toSlot) in moves) {
            movedChildren[toSlot] = topInterface.children[fromSlot] ?: continue
            ctx.write(IfMovesubPacket(topInterface.id, fromSlot, id, toSlot))
        }
        topInterface.modalSlot?.let { curModalSlot ->
            modalSlot?.let { newModalSlot ->
                if(topInterface.modalOpen && curModalSlot != newModalSlot) {
                    ctx.write(IfMovesubPacket(topInterface.id, curModalSlot, id, newModalSlot))
                }
            }
        }
        topInterface = TopInterface(ctx, id, topInterface.modalOpen, modalSlot, movedChildren)
        return topInterface
    }

    lateinit var clientSettings: ClientSettings

    internal val routines = TreeMap<Routine.Type, Routine>()

    var followPosition = lastPostion.copy()

    var inRunMode = false

    var isTeleporting = false

    var path = mutableListOf<Tile>()

    var rights = 2

    var combatLevel = 126

    val appearance = Appearance(
        gender = Appearance.Gender.MALE,
        isSkulled = false,
        overheadIcon = -1,
        apparel = Appearance.Apparel(
            skinColor = 26,
            head = 0,
            chest = 18,
            hands = 33,
            legs = 36,
            feet = 42,
            weapon = 0,
            shield = 0
        ),
        animations = Appearance.Animations(
            stand = 808,
            turn = 823,
            walk = 819,
            turn180 = 820,
            turn90CW = 821,
            turn90CCW = 822,
            run = 824
        )
    )

    var nameModifiers = arrayOf("", "", "")

    var contextMenu = arrayOf("Follow", "Trade with", "Report")

    var sequenceId: Int? by Delegates.observable<Int?>(null) { _, _, _ ->
        updateFlags.add(PlayerInfoPacket.sequence)
    }

    var spotAnimation: SpotAnimation? by Delegates.observable<SpotAnimation?>(null) { _, _, _ ->
        updateFlags.add(PlayerInfoPacket.spotAnim)
    }

    var publicMessage: PublicMessageEvent by Delegates.observable(PublicMessageEvent(0,0, "")) {
        _, _, _ -> updateFlags.add(PlayerInfoPacket.chat)
    }

    var shoutMessage: String by Delegates.observable("") { _, _, _ ->
        updateFlags.add(PlayerInfoPacket.shout)
    }

    var interacting: Character? by Delegates.observable<Character?>(null) { _, _, _ ->
        updateFlags.add(PlayerInfoPacket.lockTurnToCharacter)
    }

    val playerInterest = PlayerInterest()

    val mapInterest = MapInterest(this)

    val inventory = Inventory(this, 149, 0, 3, arrayOfNulls(28))

    val varps = mutableMapOf<Int, Int>()

    override val updateFlags = sortedSetOf<PlayerInfoPacket.UpdateType>()

    init {
        updateFlags.add(PlayerInfoPacket.appearance)
        updateFlags.add(PlayerInfoPacket.orientation)
        updateFlags.add(PlayerInfoPacket.nameModifiers)
    }

    fun addRoutine(type: Routine.Type, routine: suspend Routine.() -> Unit): Routine {
        val cont = Routine(type, this)
        cont.next = ConditionalContinuation(InitialCondition, routine.createCoroutineUnintercepted(cont, cont))
        routines[type]?.cancel()
        routines[type] = cont
        cont.resumeIfPossible() // resume until first waiting point
        return cont
    }

    fun cancelRoutine(type: Routine.Type) {
        routines[type]?.cancel()
        routines.remove(type)
    }

    fun initializeInterest(worldMap: WorldMap, worldPlayers: PlayerList, pZone: Zone) {
        playerInterest.initialize(this, worldPlayers)
        mapInterest.initialize(pZone, worldMap)
        val xteas = mapInterest.getInterestedXteas(worldMap)
        ctx.write(InterestInitPacket(this, worldPlayers, xteas, position.x.inZones, position.y.inZones))
    }


    fun updateMap(zone: Zone, xteas: List<IntArray>) {
        ctx.write(RebuildNormalPacket(xteas, zone.x, zone.y))
    }

    fun updateStat(id: Int, xp: Int, status: Int) {
        ctx.write(UpdateStatPacket(id, xp, status))
    }

    fun updateWeight(amount: Int) {
        ctx.write(UpdateRunweightPacket(amount))
    }

    fun updateRunEnergy(energy: Int) {
        ctx.write(UpdateRunenergyPacket(energy))
    }

    fun synchronizeContextMenu() {
        contextMenu.forEachIndexed { i, text ->
            ctx.write(SetPlayerOpPacket(false, i + 1, text))
        }
    }

    fun runClientScript(id: Int, vararg args: Any) {
        ctx.write(RunclientscriptPacket(id, *args))
    }

    fun setMapFlag(x: TileUnit, y: TileUnit) {
        ctx.write(SetMapFlagPacket(x - mapInterest.baseX.inTiles, y - mapInterest.baseY.inTiles))
    }

    fun updateVarbit(varbitId: Int, value: Int) {
        fun Int.setBits(msb: Int, lsb: Int): Int = this xor ((1 shl (msb + 1)) - 1) xor ((1 shl lsb) - 1)
        @Suppress("INTEGER_OVERFLOW")
        fun Int.clearBits(msb: Int, lsb: Int) =  ((1 shl 4 * 8 - 1) - 1).setBits(msb, lsb) and this

        val config = Varbits[varbitId]
        val bitSize = (config.msb.toInt() - config.lsb.toInt()) + 1
        if(value > 2.0.pow(bitSize) - 1) throw IllegalArgumentException("Value $value to big for this varbit.")
        var curVarp = varps[config.varpId] ?: 0
        curVarp.clearBits(config.msb.toInt(), config.lsb.toInt())
        curVarp = curVarp or value shl config.lsb.toInt()
        varps[config.varpId] = curVarp
        updateVarp(config.varpId, curVarp)
    }



    fun updateVarp(id: Int, value: Int) {
        if (value <= Byte.MIN_VALUE || value >= Byte.MAX_VALUE) {
            ctx.write(VarpLargePacket(id, value))
        } else {
            ctx.write(VarpSmallPacket(id, value))
        }
    }

    fun turnTo(entity: Entity) {
        setOrientation(entity)
        updateFlags.add(PlayerInfoPacket.orientation)
    }

    fun turnToLock(char: Character?) {
        interacting = char
        char?.let { setOrientation(char) }
    }

    private fun setOrientation(entity: Entity) {
        val dx = (position.x.value + (sizeX.value.toDouble() / 2)) - (entity.position.x.value + (entity.sizeX.value.toDouble() / 2))
        val dy = (position.y.value + (sizeY.value.toDouble() / 2)) - (entity.position.y.value + (entity.sizeY.value.toDouble() / 2))
        if (dx.toInt() != 0 || dy.toInt() != 0) {
            orientation = (atan2(dx, dy) * 325.949).toInt() and 0x7FF
        }
    }

    fun addFullInventory(interfaceId: Int, interfacePosition: Int,containerId: Int, objs: List<Obj?>) {
        ctx.write(UpdateInvFullPacket(interfaceId, interfacePosition, containerId, objs))
    }

    fun addPartialInventory(interfaceId: Int, interfacePosition: Int,containerId: Int, objs: Map<Int, Obj?>) {
        ctx.write(UpdateInvPartialPacket(interfaceId, interfacePosition, containerId, objs))
    }

    fun releaseInvMemory(containerId: Int) {
        ctx.write(UpdateInvStopTransmitPacket(containerId))
    }

    fun clearInv(interfaceId: Int, interfacePosition: Int) {
        ctx.write(UpdateInvClearPacket(interfaceId, interfacePosition))
    }

    fun startSequence(seqId: Int) {
        sequenceId = seqId
    }

    fun stopSequence() {
        sequenceId = null
    }

    fun startSpotAnimation(spotAnim: SpotAnimation) {
        spotAnimation = spotAnim
    }

    fun stopSpotAnimation() {
        spotAnimation = null
    }

    fun shout(message: String) {
        shoutMessage = message
    }

    fun chat(message: PublicMessageEvent) {
        publicMessage = message
    }

    fun move() = if(path.isEmpty()) {
        movementType = MovementUpdateType.STAY
    } else {
        takeStep()
    }

    fun senGameMessage(message: String) {
        ctx.write(MessageGamePacket(0, false, message))
    }

    private fun takeStep() {
        lastPostion = position
        position = when {
            inRunMode -> when {
                path.size == 1 -> {
                    movementType = MovementUpdateType.WALK
                    updateFlags.add(PlayerInfoPacket.movementTemporary)
                    followPosition = position
                    path.removeAt(0)
                }
                path.size > 1 && position.withInDistanceOf(path[1], 1.tiles) -> { // running corners
                    movementType = MovementUpdateType.WALK
                    followPosition = path.removeAt(0)
                    path.removeAt(0)
                }
                else -> {
                    movementType = MovementUpdateType.RUN
                    followPosition = path.removeAt(0)
                    path.removeAt(0)
                }
            }
            else -> {
                movementType = MovementUpdateType.WALK
                followPosition = position
                path.removeAt(0)
            }
        }
        orientation = getOrientation(followPosition, position)
    }

    override fun compareTo(other: Player) = when {
        priority < other.priority -> -1
        priority > other.priority -> 1
        else -> 0
    }

    fun handleEvents() {
        while(inEvents.isNotEmpty()) {
            inEvents.poll().invoke()
        }
    }

    fun syncMapInterest(pZone: Zone, worldMap: WorldMap) {
        mapInterest.checkReload(pZone, worldMap)
        mapInterest.packetCache.forEachIndexed { x, yPacketList ->
            yPacketList.forEachIndexed { y, packetList ->
                if(packetList.size == 1) {
                    ctx.write(UpdateZonePartialFollowsPacket(x.zones.inTiles, y.zones.inTiles))
                    ctx.write(packetList.first())
                    packetList.clear()
                } else if(packetList.size > 1) {
                    ctx.write(UpdateZonePartialEnclosedPacket(x.zones.inTiles, y.zones.inTiles, packetList.toList()))
                    packetList.clear()
                }
            }
        }
    }

    fun clearMap() {
        mapInterest.packetCache.forEachIndexed { x, yPacketList ->
            yPacketList.forEachIndexed { y, _ ->
                ctx.write(UpdateZoneFullFollowsPacket(x.zones.inTiles, y.zones.inTiles))
            }
        }
    }

    fun interestSynchronize(world: World): ChannelFuture {
        inventory.update()
        val pZone = world.map.getZone(position) ?: throw IllegalStateException("Player is outside of the map.")
        syncMapInterest(pZone, world.map)
        return ctx.writeAndFlush(PlayerInfoPacket(this, world.players))
    }

    fun postProcess() {
        updateFlags.clear()
    }
}