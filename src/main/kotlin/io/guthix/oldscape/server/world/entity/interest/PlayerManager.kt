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

import io.guthix.oldscape.server.blueprints.CombatBonus
import io.guthix.oldscape.server.blueprints.EquipmentType
import io.guthix.oldscape.server.blueprints.StyleBonus
import io.guthix.oldscape.server.dimensions.TileUnit
import io.guthix.oldscape.server.dimensions.tiles
import io.guthix.oldscape.server.net.game.out.PlayerInfoPacket
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



    class EquipmentSet(internal val equipment: MutableMap<Int, Obj>) {
        var attackBonus: StyleBonus = StyleBonus(
            equipment.values.sumBy { it.attackBonus?.stab ?: 0 },
            equipment.values.sumBy { it.attackBonus?.slash ?: 0 },
            equipment.values.sumBy { it.attackBonus?.crush ?: 0 },
            equipment.values.sumBy { it.attackBonus?.range ?: 0 },
            equipment.values.sumBy { it.attackBonus?.magic ?: 0 }
        )

        var defenceBonus: StyleBonus = StyleBonus(
            equipment.values.sumBy { it.defenceBonus?.stab ?: 0 },
            equipment.values.sumBy { it.defenceBonus?.slash ?: 0 },
            equipment.values.sumBy { it.defenceBonus?.crush ?: 0 },
            equipment.values.sumBy { it.defenceBonus?.range ?: 0 },
            equipment.values.sumBy { it.defenceBonus?.magic ?: 0 }
        )

        var strengtBonus: CombatBonus = CombatBonus(
            equipment.values.sumBy { it.strengthBonus?.melee ?: 0 },
            equipment.values.sumBy { it.strengthBonus?.range ?: 0 },
            equipment.values.sumBy { it.strengthBonus?.magic ?: 0 }
        )

        var prayerBonus: Int = equipment.values.sumBy { it.prayerBonus ?: 0 }

        var head: Obj? by EquipmentProperty(EquipmentType.HEAD)
        var cape: Obj? by EquipmentProperty(EquipmentType.CAPE)
        var neck: Obj? by EquipmentProperty(EquipmentType.NECK)
        var weapon: Obj? by EquipmentProperty(EquipmentType.ONE_HAND_WEAPON)
        var body: Obj? by EquipmentProperty(EquipmentType.BODY)
        var shield: Obj? by EquipmentProperty(EquipmentType.SHIELD)
        var legs: Obj? by EquipmentProperty(EquipmentType.LEGS)
        var hands: Obj? by EquipmentProperty(EquipmentType.HANDS)
        var feet: Obj? by EquipmentProperty(EquipmentType.FEET)
        var ring: Obj? by EquipmentProperty(EquipmentType.RING)
        var ammunition: Obj? by EquipmentProperty(EquipmentType.AMMUNITION)

        fun equip(obj: Obj) {
            requireNotNull(obj.equipmentType) { "Obj ${obj.id} has no equipment type." }
            val old = equipment[obj.equipmentType!!.slot]
            equipment[obj.equipmentType!!.slot] = obj
            updateBonuses(old, obj)
        }

        fun unequip(equipmentType: EquipmentType) {
            val old = equipment.remove(equipmentType.slot)
            removeBonuses(old)
        }

        fun updateBonuses(old: Obj?, new: Obj?) {
            removeBonuses(old)
            addBonuses(new)
        }

        private fun addBonuses(obj: Obj?) {
            attackBonus += obj?.attackBonus
            defenceBonus += obj?.defenceBonus
            strengtBonus += obj?.strengthBonus
            prayerBonus += obj?.prayerBonus ?: 0
        }

        private fun removeBonuses(obj: Obj?) {
            attackBonus -= obj?.attackBonus
            defenceBonus -= obj?.defenceBonus
            strengtBonus -= obj?.strengthBonus
            prayerBonus -= obj?.prayerBonus ?: 0
        }

        class EquipmentProperty(private val type: EquipmentType) {
            operator fun getValue(thisRef: EquipmentSet, property: KProperty<*>): Obj? = thisRef.equipment[type.slot]

            operator fun setValue(thisRef: EquipmentSet, property: KProperty<*>, value: Obj?) = if(value == null)
                thisRef.unequip(type) else thisRef.equip(value)
        }
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