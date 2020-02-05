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
package io.guthix.oldscape.server.world.entity.character.player.interest

import io.guthix.oldscape.server.net.state.game.OutGameEvent
import io.guthix.oldscape.server.net.state.game.ZoneOutGameEvent
import io.guthix.oldscape.server.net.state.game.outp.zone.ObjAddPacket
import io.guthix.oldscape.server.world.WorldMap
import io.guthix.oldscape.server.world.entity.Obj
import io.guthix.oldscape.server.world.entity.character.player.Player
import io.guthix.oldscape.server.world.mapsquare.MapsquareUnit
import io.guthix.oldscape.server.world.mapsquare.zone.Zone
import io.guthix.oldscape.server.world.mapsquare.zone.ZoneUnit
import io.guthix.oldscape.server.world.mapsquare.zone.abs
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import io.guthix.oldscape.server.world.mapsquare.zone.zones

class MapInterest(val player: Player) {
    lateinit var lastLoadedZone: Zone

    val baseX get() = lastLoadedZone.x - RANGE

    val baseY get() = lastLoadedZone.y - RANGE


    val zones = Array(SIZE.value) {
        arrayOfNulls<Zone>(SIZE.value)
    }

    val packetCache = Array(SIZE.value) {
        Array(SIZE.value) {
            mutableListOf<ZoneOutGameEvent>()
        }
    }

    fun reloadRequired(curZone: Zone) = abs(lastLoadedZone.x - curZone.x) >= UPDATE_RANGE ||
        abs(lastLoadedZone.y - curZone.y) >= UPDATE_RANGE

    fun checkReload(currentZone: Zone, map: WorldMap) {
        if(reloadRequired(currentZone)) {
            val xteas = getInterestedXteas(currentZone, map)
            player.updateMap(currentZone, xteas)
            lastLoadedZone = currentZone
            unsubscribeZones(player)
            subscribeToZones(player, map)
        }
    }

    fun subscribeToZones(player: Player, map: WorldMap) {
        ((lastLoadedZone.x - RANGE)..(lastLoadedZone.x + RANGE)).forEachIndexed { i, zoneX ->
            ((lastLoadedZone.y - RANGE)..(lastLoadedZone.y + RANGE)).forEachIndexed { j, zoneY ->
                val zone = map.getZone(lastLoadedZone.floor, zoneX, zoneY)
                zones[i][j] = zone
                zone?.players?.add(player)
            }
        }
    }

    fun unsubscribeZones(player: Player) {
        zones.forEach { it.forEach { zone -> zone?.players?.remove(player) } }
    }

    fun addGroundObject(obj: Obj) {
        println("Add drop ${obj.blueprint.id}")
        packetCache[(obj.position.x.inZones - baseX).value][(obj.position.y.inZones - baseY).value].add(
            ObjAddPacket(obj.blueprint.id, 1, obj.position.x.relativeZone, obj.position.y.relativeZone)
        )
    }

    companion object {
        val SIZE = 13.zones

        val RANGE = SIZE / 2.zones

        val UPDATE_RANGE = RANGE - PlayerInterest.RANGE.inZones

        private val ZoneUnit.startMapInterest get() = (this - RANGE).inMapsquares

        private val ZoneUnit.endMapInterest get() = (this + RANGE).inMapsquares

        fun getInterestedXteas(zone: Zone, map: WorldMap): List<IntArray> {
            val interestedXteas = mutableListOf<IntArray>()
            for(mSquareX in zone.x.startMapInterest..zone.x.endMapInterest) {
                for(mSquareY in zone.y.startMapInterest..zone.y.endMapInterest) {
                    if(onTutorialIsland(mSquareX, mSquareY)) continue
                    val id = (mSquareX.value shl 8) or mSquareY.value
                    val xtea = map.mapsquares[id]?.xtea ?: throw IllegalStateException(
                        "Could not find XTEA for id $id."
                    )
                    interestedXteas.add(xtea)
                }
            }
            return interestedXteas
        }

        private fun onTutorialIsland(mSquareX: MapsquareUnit, mSquareY: MapsquareUnit) =
            ((mSquareX.value == 48 || mSquareX.value == 49) && mSquareY.value == 48)
                || (mSquareX.value == 48 && mSquareX.value == 148)
    }
}



