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
package io.guthix.oldscape.server.world.map

import io.guthix.oldscape.server.dimensions.*
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Loc
import io.guthix.oldscape.server.world.entity.Obj
import io.guthix.oldscape.server.world.entity.Projectile

class MapsquareFloor(
    val floor: FloorUnit,
    val x: MapsquareUnit,
    val y: MapsquareUnit,
    val mapsquare: Mapsquare
) {
    lateinit var world: World

    val zones: Array<Array<Zone>> = Array(MapsquareUnit.SIZE_ZONE.value) { zoneX ->
        Array(MapsquareUnit.SIZE_ZONE.value) { zoneY ->
            Zone(floor, x.inZones + zoneX.zones, y.inZones + zoneY.zones, this)
        }
    }

    fun getZone(localX: TileUnit, localY: TileUnit): Zone = zones[localX.inZones.value][localY.inZones.value]

    fun getZone(localX: ZoneUnit, localY: ZoneUnit): Zone = zones[localX.value][localY.value]

    fun getCollisionMask(localX: TileUnit, localY: TileUnit): Int = zones[localX.inZones.value][localY.inZones.value]
        .getCollisionMask(localX.relativeZone, localY.relativeZone)

    fun getLoc(id: Int, localX: TileUnit, localY: TileUnit): Loc? = zones[localX.inZones.value][localY.inZones.value]
        .getLoc(id, localX.relativeZone, localY.relativeZone)

    fun addStaticLocation(loc: Loc) {
        val zoneX = loc.pos.x.inZones.relativeMapSquare
        val zoneY = loc.pos.y.inZones.relativeMapSquare
        zones[zoneX.value][zoneY.value].addStaticLoc(loc)
    }

    fun addUnwalkableTile(localX: TileUnit, localY: TileUnit): Unit = zones[localX.inZones.value][localY.inZones.value]
        .addUnwalkableTile(localX.relativeZone, localY.relativeZone)

    fun addObject(tile: Tile, obj: Obj) {
        val zoneX = tile.x.inZones.relativeMapSquare
        val zoneY = tile.y.inZones.relativeMapSquare
        zones[zoneX.value][zoneY.value].addObject(tile, obj)
    }

    fun removeObject(tile: Tile, id: Int): Obj {
        val zoneX = tile.x.inZones.relativeMapSquare
        val zoneY = tile.y.inZones.relativeMapSquare
        return zones[zoneX.value][zoneY.value].removeObject(tile, id)
    }

    fun addDynamicLoc(loc: Loc) {
        val zoneX = loc.pos.x.inZones.relativeMapSquare
        val zoneY = loc.pos.y.inZones.relativeMapSquare
        zones[zoneX.value][zoneY.value].addDynamicLoc(loc)
    }

    fun removeDynamicLoc(loc: Loc) {
        val zoneX = loc.pos.x.inZones.relativeMapSquare
        val zoneY = loc.pos.y.inZones.relativeMapSquare
        zones[zoneX.value][zoneY.value].removeDynamicLoc(loc)
    }

    fun addProjectile(proj: Projectile) {
        val zoneX = proj.start.x.inZones.relativeMapSquare
        val zoneY = proj.start.y.inZones.relativeMapSquare
        zones[zoneX.value][zoneY.value].addProjectile(proj)
    }
}