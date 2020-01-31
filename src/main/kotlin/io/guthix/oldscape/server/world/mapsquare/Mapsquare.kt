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

import io.guthix.oldscape.cache.map.MapDefinition
import io.guthix.oldscape.cache.map.MapLocDefinition
import io.guthix.oldscape.cache.map.MapSquareDefinition
import io.guthix.oldscape.server.api.blueprint.LocationBlueprints
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Location
import io.guthix.oldscape.server.world.mapsquare.zone.Zone
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import io.guthix.oldscape.server.world.mapsquare.zone.tile.TileUnit
import io.guthix.oldscape.server.world.mapsquare.zone.tile.tiles

class Mapsquare(val x: MapsquareUnit, val y: MapsquareUnit, val xtea: IntArray, val world: World) {
    val id get() = id(x, y)

    val floors: Array<MapsquareFloor> = Array(FLOOR_COUNT) {
        MapsquareFloor(it.floors, x, y, this)
    }

    fun initialize(def: MapSquareDefinition) {
        fun Array<MapsquareFloor>.loadUnwalkableTiles(renderRules: Array<Array<ShortArray>>) {
            renderRules.forEachIndexed { z, floorRenderRules ->
                floorRenderRules.forEachIndexed { x, verticalRenderRules ->
                    verticalRenderRules.forEachIndexed { y, currentRule ->
                        var zReal = z
                        if (currentRule.toInt() and MapDefinition.BLOCKED_TILE_MASK.toInt() == 1) {
                            if (renderRules[1][x][y].toInt() and MapDefinition.BRIDGE_TILE_MASK.toInt() == 2) {
                                zReal--
                            }
                            if (zReal >= 0) {
                                get(zReal).addUnwalkableTile(x.tiles, y.tiles)
                            }
                        }
                    }
                }
            }
        }
        fun Array<MapsquareFloor>.loadStaticLocations(locations: List<MapLocDefinition>) {
            locations.forEach { loc ->
                get(loc.floor).addStaticLocation(
                    Location(
                        Tile(
                            loc.floor.floors,
                            def.x.mapsquares.inTiles + loc.localX.tiles,
                            def.y.mapsquares.inTiles + loc.localY.tiles
                        ),
                        LocationBlueprints[loc.id],
                        loc.type,
                        loc.orientation
                    )
                )
            }
        }
       floors.apply {
            loadUnwalkableTiles(def.mapDefinition.renderRules)
            loadStaticLocations(def.locationDefinitions)
        }
    }

    fun getZone(floor: FloorUnit, localX: TileUnit, localY: TileUnit): Zone {
        return floors[floor.value].zones[localX.inZones.value][localY.inZones.value]
    }

    fun addUnwalkableTile(floor: FloorUnit, localX: TileUnit, localY: TileUnit) {
        floors[floor.value].zones[localX.inZones.value][localY.inZones.value]
            .addUnwalkableTile(localX.relativeZone, localY.relativeZone)
    }

    fun getCollisionMask(floor: FloorUnit, localX: TileUnit, localY: TileUnit): Int {
        return floors[floor.value].zones[localX.inZones.value][localY.inZones.value]
            .getCollisionMask(localX.relativeZone, localY.relativeZone)
    }

    companion object {
        const val FLOOR_COUNT = 4

        fun id(x: MapsquareUnit, y: MapsquareUnit) = (x.value shl 8) or y.value
    }
}