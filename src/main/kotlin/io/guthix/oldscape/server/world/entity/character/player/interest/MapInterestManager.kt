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
package io.guthix.oldscape.server.world.entity.character.player.interest

import io.guthix.oldscape.server.net.state.game.ZoneOutGameEvent
import io.guthix.oldscape.server.net.state.game.outp.zone.LocAddChangePacket
import io.guthix.oldscape.server.net.state.game.outp.zone.LocDelPacket
import io.guthix.oldscape.server.net.state.game.outp.zone.ObjAddPacket
import io.guthix.oldscape.server.net.state.game.outp.zone.ObjDelPacket
import io.guthix.oldscape.server.world.WorldMap
import io.guthix.oldscape.server.world.entity.Loc
import io.guthix.oldscape.server.world.entity.Obj
import io.guthix.oldscape.server.world.entity.character.player.Player
import io.guthix.oldscape.server.world.mapsquare.MapsquareUnit
import io.guthix.oldscape.server.world.mapsquare.zone.*
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile

class MapInterestManager(val player: Player) {
    lateinit var middleZone: Zone

    val baseX get() = middleZone.x - RANGE

    val baseY get() = middleZone.y - RANGE

    val zones = Array(SIZE.value) {
        arrayOfNulls<Zone>(SIZE.value)
    }

    val packetCache = Array(SIZE.value) {
        Array(SIZE.value) {
            mutableListOf<ZoneOutGameEvent>()
        }
    }

    fun initialize(mZone: Zone, map: WorldMap) {
        middleZone = mZone
        ((middleZone.x - RANGE)..(middleZone.x + RANGE)).forEachIndexed { i, zoneX ->
            ((middleZone.y - RANGE)..(middleZone.y + RANGE)).forEachIndexed { j, zoneY ->
                val zone = map.getZone(middleZone.floor, zoneX, zoneY)
                zones[i][j] = zone
                zone?.let {
                    zone.players.add(player)
                    addInterestPackets(zone)
                }
            }
        }
    }

    fun reloadRequired(curZone: Zone) = abs(middleZone.x - curZone.x) > UPDATE_RANGE ||
        abs(middleZone.y - curZone.y) > UPDATE_RANGE

    fun checkReload(curZone: Zone, map: WorldMap) {
        if(reloadRequired(curZone)) {
            val oldZone = middleZone
            middleZone = curZone
            val xteas = getInterestedXteas(map)
            player.updateMap(curZone, xteas)
            unsubscribeZones(player)
            subscribeToZones(oldZone, player, map)
        }
    }

    fun getInterestedXteas(map: WorldMap): List<IntArray> {
        val interestedXteas = mutableListOf<IntArray>()
        for(mSquareX in middleZone.x.startMapInterest..middleZone.x.endMapInterest) {
            for(mSquareY in middleZone.y.startMapInterest..middleZone.y.endMapInterest) {
                if(onTutorialIsland(mSquareX, mSquareY)) continue
                val id = (mSquareX.value shl 8) or mSquareY.value
                val xtea = map.mapsquares[id]?.xtea ?: error(
                    "Could not find XTEA for id $id."
                )
                interestedXteas.add(xtea)
            }
        }
        return interestedXteas
    }

    fun unsubscribeZones(player: Player) {
        zones.forEachIndexed { x, arrayOfZones ->
            arrayOfZones.forEachIndexed { y, zone ->
                zone?.players?.remove(player)
            }
        }
    }

    fun subscribeToZones(oldZone: Zone, player: Player, map: WorldMap) {
        val prevPacketCache = packetCache.copyOf()
        packetCache.forEach { it.forEach { pCache -> pCache.clear() } }
        ((middleZone.x - RANGE)..(middleZone.x + RANGE)).forEachIndexed { i, zoneX ->
            ((middleZone.y - RANGE)..(middleZone.y + RANGE)).forEachIndexed { j, zoneY ->
                val zone = map.getZone(middleZone.floor, zoneX, zoneY)
                zones[i][j] = zone
                zone?.let {
                    zone.players.add(player)
                    val prevLocalX = (zone.x - (oldZone.x - RANGE))
                    val prevLocalY = (zone.y - (oldZone.y - RANGE))
                    if(middleZone.floor == oldZone.floor && prevLocalX in REL_RANGE && prevLocalY in REL_RANGE) {
                        packetCache[i][j].addAll(prevPacketCache[prevLocalX.value][prevLocalY.value]) // move packet cache
                    } else {
                        addInterestPackets(zone)
                    }
                }
            }
        }
    }

    private fun addInterestPackets(zone: Zone) {
        zone.groundObjects.forEach { (tile, objMap) ->
            objMap.values.forEach { objList ->
                objList.forEach { obj ->
                    addObject(tile, obj)
                }
            }
        }
    }

    fun addObject(tile: Tile, obj: Obj) {
        packetCache[(tile.x.inZones - baseX).value][(tile.y.inZones - baseY).value].add(
            ObjAddPacket(obj.blueprint.id, obj.quantity, tile.x.relativeZone, tile.y.relativeZone)
        )
    }

    fun removeObject(tile: Tile, obj: Obj) {
        packetCache[(tile.x.inZones - baseX).value][(tile.y.inZones - baseY).value].add(
            ObjDelPacket(obj.blueprint.id, tile.x.relativeZone, tile.y.relativeZone)
        )
    }

    fun addDynamicLoc(loc: Loc) {
        packetCache[(loc.position.x.inZones - baseX).value][(loc.position.y.inZones - baseY).value].add(
            LocAddChangePacket(
                loc.blueprint.id, loc.type, loc.orientation, loc.position.x.relativeZone, loc.position.y.relativeZone
            )
        )
    }

    fun removeDynamicLoc(loc: Loc) {
        packetCache[(loc.position.x.inZones - baseX).value][(loc.position.y.inZones - baseY).value].add(
            LocDelPacket(loc.type, loc.orientation, loc.position.x.relativeZone, loc.position.y.relativeZone)
        )
    }

    companion object {
        val SIZE = 13.zones

        val REL_RANGE = (0.zones until SIZE)

        val RANGE = SIZE / 2.zones

        val UPDATE_RANGE = RANGE - PlayerInterestManager.RANGE.inZones

        private val ZoneUnit.startMapInterest get() = (this - RANGE).inMapsquares

        private val ZoneUnit.endMapInterest get() = (this + RANGE).inMapsquares

        private fun onTutorialIsland(mSquareX: MapsquareUnit, mSquareY: MapsquareUnit) =
            ((mSquareX.value == 48 || mSquareX.value == 49) && mSquareY.value == 48)
                || (mSquareX.value == 48 && mSquareX.value == 148)
    }
}



