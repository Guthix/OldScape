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

import io.guthix.oldscape.cache.map.MapDefinition
import io.guthix.oldscape.cache.map.MapLocDefinition
import io.guthix.oldscape.cache.map.MapSquareDefinition
import io.guthix.oldscape.server.dimensions.*
import io.guthix.oldscape.server.world.WorldMap
import io.guthix.oldscape.server.world.entity.Loc
import io.guthix.oldscape.server.world.entity.Obj
import io.guthix.oldscape.server.world.entity.Projectile

class Mapsquare(val x: MapsquareUnit, val y: MapsquareUnit, val xtea: IntArray, val world: WorldMap) {
    val id: Int get() = id(x, y)

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
            locations.forEach { (id1, floor, localX, localY, type, orientation) ->
                get(floor).addStaticLocation(
                    Loc(
                        id1,
                        type,
                        Tile(floor.floors,
                            def.x.mapsquares.inTiles + localX.tiles,
                            def.y.mapsquares.inTiles + localY.tiles
                        ),
                        orientation
                    )
                )
            }
        }
        floors.apply {
            loadUnwalkableTiles(def.mapDefinition.renderRules)
            loadStaticLocations(def.locationDefinitions)
        }
    }

    fun getZone(floor: FloorUnit, localX: TileUnit, localY: TileUnit): Zone = floors[floor.value]
        .getZone(localX, localY)

    fun getZone(floor: FloorUnit, localX: ZoneUnit, localY: ZoneUnit): Zone = floors[floor.value]
        .getZone(localX, localY)

    fun getCollisionMask(floor: FloorUnit, localX: TileUnit, localY: TileUnit): Int = floors[floor.value]
        .getCollisionMask(localX, localY)

    fun getLoc(id: Int, floor: FloorUnit, localX: TileUnit, localY: TileUnit): Loc? = floors[floor.value]
        .getLoc(id, localX, localY)

    fun addUnwalkableTile(floor: FloorUnit, localX: TileUnit, localY: TileUnit): Unit = floors[floor.value]
        .addUnwalkableTile(localX.relativeZone, localY.relativeZone)

    fun addObject(tile: Tile, obj: Obj): Unit = floors[tile.floor.value].addObject(tile, obj)

    fun removeObject(tile: Tile, id: Int): Obj = floors[tile.floor.value].removeObject(tile, id)

    fun addDynamicLoc(loc: Loc): Unit = floors[loc.pos.floor.value].addDynamicLoc(loc)

    fun removeDynamicLoc(loc: Loc): Unit = floors[loc.pos.floor.value].removeDynamicLoc(loc)

    fun addProjectile(proj: Projectile): Unit? = floors[proj.start.floor.value].addProjectile(proj)

    companion object {
        const val FLOOR_COUNT: Int = 4

        fun id(x: MapsquareUnit, y: MapsquareUnit): Int = (x.value shl 8) or y.value
    }
}