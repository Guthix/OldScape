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
package io.guthix.oldscape.server.world.entity.interest

import io.guthix.oldscape.server.PropertyHolder
import io.guthix.oldscape.server.world.map.dim.TileUnit
import io.guthix.oldscape.server.world.map.dim.tiles
import io.guthix.oldscape.server.net.game.out.PlayerInfoPacket
import io.guthix.oldscape.server.template.EquipmentType
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.*
import io.guthix.oldscape.server.world.map.Tile
import io.netty.channel.ChannelFuture
import kotlin.reflect.KProperty

class PlayerManager(val index: Int) : InterestManager {
    var localPlayerCount: Int = 0

    val localPlayers: Array<Player?> = arrayOfNulls(World.MAX_PLAYERS)

    val localPlayerIndexes: IntArray = IntArray(World.MAX_PLAYERS)

    var externalPlayerCount: Int = 0

    val externalPlayerIndexes: IntArray = IntArray(World.MAX_PLAYERS)

    val regionIds: IntArray = IntArray(World.MAX_PLAYERS)

    val skipFlags: ByteArray = ByteArray(World.MAX_PLAYERS)

    override fun initialize(world: World, player: Player) {
        localPlayers[index] = player
        localPlayerIndexes[localPlayerCount++] = index
        for (playerIndex in 1 until World.MAX_PLAYERS) {
            if (index != playerIndex) {
                val externalPlayer = world.players[playerIndex]
                regionIds[playerIndex] = externalPlayer?.pos?.regionId ?: 0
                externalPlayerIndexes[externalPlayerCount++] = playerIndex
            }
        }
    }

    override fun synchronize(world: World, player: Player): List<ChannelFuture> = listOf(
        player.ctx.write(PlayerInfoPacket(world.players, this, player))
    )

    override fun postProcess() {}

    class EquipmentSet(val equipment: MutableMap<Int, Obj>) : PropertyHolder {
        var head: Obj? = equipment[EquipmentType.HEAD.slot]
        var cape: Obj? = equipment[EquipmentType.CAPE.slot]
        var neck: Obj? = equipment[EquipmentType.NECK.slot]
        var weapon: Obj? = equipment[EquipmentType.ONE_HAND_WEAPON.slot]
        var body: Obj? = equipment[EquipmentType.BODY.slot]
        var shield: Obj? = equipment[EquipmentType.SHIELD.slot]
        var legs: Obj? = equipment[EquipmentType.LEGS.slot]
        var hands: Obj? = equipment[EquipmentType.HANDS.slot]
        var feet: Obj? = equipment[EquipmentType.FEET.slot]
        var ring: Obj? = equipment[EquipmentType.RING.slot]
        var ammunition: Obj? = equipment[EquipmentType.AMMUNITION.slot]

        fun equip(obj: Obj) {
            requireNotNull(obj.equipmentType) { "Obj ${obj.id} has no equipment type." }
            equipment[obj.equipmentType!!.slot] = obj
        }

        fun unequip(equipmentType: EquipmentType): Obj? = equipment.remove(equipmentType.slot)

        override val properties: MutableMap<KProperty<*>, Any?> = mutableMapOf()
    }

    data class Style(
        val hair: Int,
        val beard: Int,
        val torso: Int,
        val arms: Int,
        val legs: Int,
        val hands: Int,
        val feet: Int
    )

    data class Colours(
        var hair: Int,
        var torso: Int,
        var legs: Int,
        var feet: Int,
        var skin: Int
    )

    enum class Gender(val opcode: Int) { MALE(0), FEMALE(1) }

    companion object {
        val SIZE: TileUnit = 32.tiles

        val RANGE: TileUnit = SIZE / 2.tiles

        val REGION_SIZE: TileUnit = 8192.tiles

        const val MESSAGE_DURATION: Int = 4
    }
}

val Tile.regionId: Int
    get() = (floor.value shl 16) or ((x / PlayerManager.REGION_SIZE).value shl 8) or
        (y / PlayerManager.REGION_SIZE).value