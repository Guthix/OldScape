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
package io.guthix.oldscape.server.world.mapsquare

import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Location
import io.guthix.oldscape.server.world.mapsquare.zone.Zone
import io.guthix.oldscape.server.world.mapsquare.zone.ZoneUnit
import io.guthix.oldscape.server.world.mapsquare.zone.tile.TileUnit
import io.guthix.oldscape.server.world.mapsquare.zone.zones

class MapsquareFloor(
    val floor: FloorUnit,
    val x: MapsquareUnit,
    val y: MapsquareUnit,
    val mapsquare: Mapsquare
) {
    lateinit var world: World

    val zones = Array(MapsquareUnit.SIZE_ZONE.value) { zoneX ->
        Array(MapsquareUnit.SIZE_ZONE.value) { zoneY ->
            Zone(floor, x.inZones + zoneX.zones, y.inZones + zoneY.zones, this)
        }
    }

    fun getZone(localX: TileUnit, localY: TileUnit) = zones[localX.inZones.value][localY.inZones.value]

    fun getZone(localX: ZoneUnit, localY: ZoneUnit) = zones[localX.value][localY.value]

    fun addUnwalkableTile(localX: TileUnit, localY: TileUnit) = zones[localX.inZones.value][localY.inZones.value]
        .addUnwalkableTile(localX.relativeZone, localY.relativeZone)

    fun getCollisionMask(localX: TileUnit, localY: TileUnit) = zones[localX.inZones.value][localY.inZones.value]
        .getCollisionMask(localX.relativeZone, localY.relativeZone)

    fun getLocation(id: Int, localX: TileUnit, localY: TileUnit) = zones[localX.inZones.value][localY.inZones.value]
        .getLocation(id, localX.relativeZone, localY.relativeZone)

    fun addStaticLocation(location: Location) {
        val zoneX = location.position.x.inZones.relativeMapSquare
        val zoneY = location.position.y.inZones.relativeMapSquare
        zones[zoneX.value][zoneY.value].addStaticLocation(location)
    }
}