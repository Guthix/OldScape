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

import io.guthix.oldscape.server.dimensions.*
import io.guthix.oldscape.server.net.game.ZoneOutGameEvent
import io.guthix.oldscape.server.net.game.out.*
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.WorldMap
import io.guthix.oldscape.server.world.entity.Loc
import io.guthix.oldscape.server.world.entity.Obj
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.entity.Projectile
import io.guthix.oldscape.server.world.map.Tile
import io.guthix.oldscape.server.world.map.Zone
import io.netty.channel.ChannelFuture

class MapManager : InterestManager {
    lateinit var middleZone: Zone

    val baseX: ZoneUnit get() = middleZone.x - RANGE

    val baseY: ZoneUnit get() = middleZone.y - RANGE

    val zones: Array<Array<Zone?>> = Array(SIZE.value) {
        arrayOfNulls(SIZE.value)
    }

    val changes: Array<Array<MutableList<ZoneOutGameEvent>>> = Array(SIZE.value) {
        Array(SIZE.value) {
            mutableListOf()
        }
    }

    fun reloadRequired(curZone: Zone): Boolean = abs(middleZone.x - curZone.x) > UPDATE_RANGE ||
        abs(middleZone.y - curZone.y) > UPDATE_RANGE

    fun checkReload(curZone: Zone, map: WorldMap, player: Player) {
        if (reloadRequired(curZone)) {
            val oldZone = middleZone
            middleZone = curZone
            val xteas = getInterestedXteas(map)
            player.ctx.write(RebuildNormalPacket(xteas, curZone.x, curZone.y))
            unsubscribeZones(player)
            subscribeToZones(oldZone, player, map)
        }
    }

    fun getInterestedXteas(map: WorldMap): List<IntArray> {
        val interestedXteas = mutableListOf<IntArray>()
        for (mSquareX in middleZone.x.startMapInterest..middleZone.x.endMapInterest) {
            for (mSquareY in middleZone.y.startMapInterest..middleZone.y.endMapInterest) {
                if (onTutorialIsland(mSquareX, mSquareY)) continue
                val id = (mSquareX.value shl 8) or mSquareY.value
                val xtea = map.mapsquares[id]?.xtea ?: error("Could not find XTEA for id $id.")
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
        val prevPacketCache = changes.copyOf()
        changes.forEach { it.forEach(MutableList<ZoneOutGameEvent>::clear) }
        ((middleZone.x - RANGE)..(middleZone.x + RANGE)).forEachIndexed { i, zoneX ->
            ((middleZone.y - RANGE)..(middleZone.y + RANGE)).forEachIndexed { j, zoneY ->
                val zone = map.getZone(middleZone.floor, zoneX, zoneY)
                zones[i][j] = zone
                zone?.let {
                    zone.players.add(player)
                    val prevLocalX = (zone.x - (oldZone.x - RANGE))
                    val prevLocalY = (zone.y - (oldZone.y - RANGE))
                    if (middleZone.floor == oldZone.floor && prevLocalX in REL_RANGE && prevLocalY in REL_RANGE) {
                        changes[i][j].addAll(prevPacketCache[prevLocalX.value][prevLocalY.value]) // move packet cache
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
        changes[(tile.x.inZones - baseX).value][(tile.y.inZones - baseY).value].add(
            ObjAddPacket(obj.id, obj.quantity, tile.x.relativeZone, tile.y.relativeZone)
        )
    }

    fun removeObject(tile: Tile, obj: Obj) {
        changes[(tile.x.inZones - baseX).value][(tile.y.inZones - baseY).value].add(
            ObjDelPacket(obj.id, tile.x.relativeZone, tile.y.relativeZone)
        )
    }

    fun addDynamicLoc(loc: Loc) {
        changes[(loc.pos.x.inZones - baseX).value][(loc.pos.y.inZones - baseY).value].add(
            LocAddChangePacket(
                loc.id, loc.type, loc.orientation, loc.pos.x.relativeZone, loc.pos.y.relativeZone
            )
        )
    }

    fun removeDynamicLoc(loc: Loc) {
        changes[(loc.pos.x.inZones - baseX).value][(loc.pos.y.inZones - baseY).value].add(
            LocDelPacket(loc.type, loc.orientation, loc.pos.x.relativeZone, loc.pos.y.relativeZone)
        )
    }

    fun addProjectile(projectile: Projectile) {
        changes[(projectile.start.x.inZones - baseX).value][(projectile.start.y.inZones - baseY).value].add(
            MapProjanimPacket(
                projectile.id,
                projectile.startHeight,
                projectile.targetHeight,
                if (projectile.target is Player) projectile.target.index + 32769 else projectile.target.index + 1,
                projectile.angle,
                projectile.steepness,
                projectile.delay,
                projectile.lifetime,
                projectile.target.pos.x - projectile.start.x,
                projectile.target.pos.y - projectile.start.y,
                projectile.start.x.relativeZone,
                projectile.start.y.relativeZone
            )
        )
    }

    fun clear(player: Player) {
        changes.forEachIndexed { x, yPacketList ->
            yPacketList.forEachIndexed { y, _ ->
                player.ctx.write(UpdateZoneFullFollowsPacket(x.zones.inTiles, y.zones.inTiles))
            }
        }
    }

    override fun initialize(world: World, player: Player) {
        middleZone = world.map.getZone(player.pos) ?: error("Could not find $player on the map.")
        ((middleZone.x - RANGE)..(middleZone.x + RANGE)).forEachIndexed { i, zoneX ->
            ((middleZone.y - RANGE)..(middleZone.y + RANGE)).forEachIndexed { j, zoneY ->
                val zone = world.map.getZone(middleZone.floor, zoneX, zoneY)
                zones[i][j] = zone
                zone?.let {
                    zone.players.add(player)
                    addInterestPackets(zone)
                }
            }
        }
    }

    override fun synchronize(world: World, player: Player): List<ChannelFuture> {
        val futures = mutableListOf<ChannelFuture>()
        val pZone = world.map.getZone(player.pos) ?: error("Could not find $player on the map.")
        checkReload(pZone, world.map, player)
        changes.forEachIndexed { x, yPacketList ->
            yPacketList.forEachIndexed { y, packetList ->
                if (packetList.size == 1) {
                    futures.add(player.ctx.write(UpdateZonePartialFollowsPacket(x.zones.inTiles, y.zones.inTiles)))
                    futures.add(player.ctx.write(packetList.first()))
                } else if (packetList.size > 1) {
                    futures.add(player.ctx.write(
                        UpdateZonePartialEnclosedPacket(x.zones.inTiles, y.zones.inTiles, packetList.toList())
                    ))
                }
            }
        }
        return futures
    }

    override fun postProcess(): Unit = changes.forEach { it.forEach(MutableList<ZoneOutGameEvent>::clear) }

    companion object {
        val SIZE: ZoneUnit = 13.zones

        val REL_RANGE: ZoneUnitRange = (0.zones until SIZE)

        val RANGE: ZoneUnit = SIZE / 2.zones

        val UPDATE_RANGE: ZoneUnit = RANGE - PlayerManager.RANGE.inZones

        private val ZoneUnit.startMapInterest get() = (this - RANGE).inMapsquares

        private val ZoneUnit.endMapInterest get() = (this + RANGE).inMapsquares

        private fun onTutorialIsland(x: MapsquareUnit, y: MapsquareUnit) =
            ((x.value == 48 || x.value == 49) && y.value == 48)
                || (x.value == 48 && x.value == 148)
    }
}



