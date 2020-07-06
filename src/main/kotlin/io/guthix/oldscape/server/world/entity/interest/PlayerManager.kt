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
package io.guthix.oldscape.server.world.entity.interest

import io.guthix.oldscape.server.blueprints.CombatBonus
import io.guthix.oldscape.server.blueprints.StyleBonus
import io.guthix.oldscape.server.dimensions.TileUnit
import io.guthix.oldscape.server.dimensions.tiles
import io.guthix.oldscape.server.net.game.out.PlayerInfoPacket
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.*
import io.guthix.oldscape.server.world.map.Tile
import io.netty.channel.ChannelFuture
import kotlin.properties.Delegates

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

    class EquipmentSet(
        head: HeadEquipment?,
        cape: CapeEquipment?,
        neck: NeckEquipment?,
        ammunition: AmmunitionEquipment?,
        weapon: WeaponEquipment?,
        body: BodyEquipment?,
        shield: ShieldEquipment?,
        legs: LegEquipment?,
        hands: HandEquipment?,
        feet: FeetEquipment?,
        ring: RingEquipment?
    ) {
        var attackBonus: StyleBonus = StyleBonus(0, 0, 0, 0, 0) + head?.attackBonus +
            cape?.attackBonus + neck?.attackBonus + ammunition?.attackBonus + weapon?.attackBonus + body?.attackBonus +
            shield?.attackBonus + legs?.attackBonus + hands?.attackBonus + feet?.attackBonus + ring?.attackBonus

        var defenceBonus: StyleBonus = StyleBonus(0, 0, 0, 0, 0) + head?.defenceBonus +
            cape?.defenceBonus + neck?.defenceBonus + ammunition?.defenceBonus + weapon?.defenceBonus +
            body?.defenceBonus + shield?.defenceBonus + legs?.defenceBonus + hands?.defenceBonus + feet?.defenceBonus +
            ring?.defenceBonus

        var strengtBonus: CombatBonus = CombatBonus(0, 0, 0) + head?.strengthBonus +
            cape?.strengthBonus + neck?.strengthBonus + ammunition?.strengthBonus + weapon?.strengthBonus +
            body?.strengthBonus + shield?.strengthBonus + legs?.strengthBonus + hands?.strengthBonus +
            feet?.strengthBonus + ring?.strengthBonus

        var prayerBonus: Int = (head?.prayerBonus ?: 0) + (cape?.prayerBonus ?: 0) + (neck?.prayerBonus ?: 0) +
            (ammunition?.prayerBonus ?: 0) + (weapon?.prayerBonus ?: 0) + (body?.prayerBonus ?: 0) +
            (shield?.prayerBonus ?: 0) + (legs?.prayerBonus ?: 0) + (hands?.prayerBonus ?: 0) +
            (feet?.prayerBonus ?: 0) + (ring?.prayerBonus ?: 0)

        var head: HeadEquipment? by Delegates.observable(head) { _, old, new -> updateBonuses(old, new) }

        var cape: CapeEquipment? by Delegates.observable(cape) { _, old, new -> updateBonuses(old, new) }
            internal set

        var neck: NeckEquipment? by Delegates.observable(neck) { _, old, new -> updateBonuses(old, new) }
            internal set

        var ammunition: AmmunitionEquipment? by Delegates.observable(ammunition) { _, old, new ->
            updateBonuses(old, new)
        }
            internal set

        var weapon: WeaponEquipment? by Delegates.observable(weapon) { _, old, new -> updateBonuses(old, new) }
            internal set

        var body: BodyEquipment? by Delegates.observable(body) { _, old, new -> updateBonuses(old, new) }
            internal set

        var shield: ShieldEquipment? by Delegates.observable(shield) { _, old, new -> updateBonuses(old, new) }
            internal set

        var legs: LegEquipment? by Delegates.observable(legs) { _, old, new -> updateBonuses(old, new) }
            internal set

        var hands: HandEquipment? by Delegates.observable(hands) { _, old, new -> updateBonuses(old, new) }
            internal set

        var feet: FeetEquipment? by Delegates.observable(feet) { _, old, new -> updateBonuses(old, new) }
            internal set

        var ring: RingEquipment? by Delegates.observable(ring) { _, old, new -> updateBonuses(old, new) }
            internal set

        fun updateBonuses(old: Equipment?, new: Equipment?) {
            attackBonus -= old?.attackBonus
            defenceBonus -= old?.defenceBonus
            strengtBonus -= old?.strengthBonus
            prayerBonus -= old?.prayerBonus ?: 0
            attackBonus += new?.attackBonus
            defenceBonus += new?.defenceBonus
            strengtBonus += new?.strengthBonus
            prayerBonus += new?.prayerBonus ?: 0
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