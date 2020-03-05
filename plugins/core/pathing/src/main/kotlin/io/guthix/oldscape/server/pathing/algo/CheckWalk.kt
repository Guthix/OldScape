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
package io.guthix.oldscape.server.pathing.algo

import io.guthix.oldscape.server.world.WorldMap
import io.guthix.oldscape.server.world.mapsquare.FloorUnit
import io.guthix.oldscape.server.world.mapsquare.zone.ZoneCollision
import io.guthix.oldscape.server.world.mapsquare.zone.tile.TileUnit
import io.guthix.oldscape.server.world.mapsquare.zone.tile.tiles
import io.guthix.oldscape.server.world.mapsquare.zone.tile.until

internal fun WorldMap.canWalkWest(floor: FloorUnit, x: TileUnit, y: TileUnit, moverSize: TileUnit): Boolean {
    for(i in 0.tiles until moverSize) {
        if(getCollisionMask(floor, x - 1.tiles, y + i) and ZoneCollision.BLOCK_E != 0) return false
    }
    return true
}


internal fun WorldMap.canWalkEast(floor: FloorUnit, x: TileUnit, y: TileUnit, moverSize: TileUnit): Boolean {
    for (i in 0.tiles until moverSize) {
        if(getCollisionMask(floor, x + moverSize, y + i) and ZoneCollision.BLOCK_W != 0) return false
    }
    return true
}


internal fun WorldMap.canWalkSouth(floor: FloorUnit, x: TileUnit, y: TileUnit, moverSize: TileUnit): Boolean {
    for (i in 0.tiles until moverSize) {
        if(getCollisionMask(floor, x + i, y - 1.tiles) and ZoneCollision.BLOCK_N != 0) return false
    }
    return true
}

internal fun WorldMap.canWalkNorth(floor: FloorUnit, x: TileUnit, y: TileUnit, moverSize: TileUnit): Boolean {
    for (i in 0.tiles until moverSize) {
        if(getCollisionMask(floor, x + i, y + moverSize) and ZoneCollision.BLOCK_S != 0) return false
    }
    return true
}

internal fun WorldMap.canWalkSouthWest(floor: FloorUnit, x: TileUnit, y: TileUnit, moverSize: TileUnit): Boolean {
    if(getCollisionMask(floor, x + moverSize - 1.tiles, y - 1.tiles) and ZoneCollision.BLOCK_N != 0) return false
    if(getCollisionMask(floor, x - 1.tiles, y + moverSize- 1.tiles) and ZoneCollision.BLOCK_E != 0) return false
    for(i in 0.tiles until moverSize) {
        if(getCollisionMask(floor, x + i - 1.tiles, y - 1.tiles) and ZoneCollision.BLOCK_NE != 0) return false
        if(getCollisionMask(floor, x - 1.tiles, y + i - 1.tiles) and ZoneCollision.BLOCK_NE != 0) return false
    }
    return true
}

internal fun WorldMap.canWalkSouthEast(floor: FloorUnit, x: TileUnit, y: TileUnit, moverSize: TileUnit): Boolean {
    if(getCollisionMask(floor, x, y - 1.tiles) and ZoneCollision.BLOCK_N != 0) return false
    if(getCollisionMask(floor, x + moverSize, y + moverSize - 1.tiles)  and ZoneCollision.BLOCK_W != 0) return false
    for(i in 0.tiles until moverSize) {
        if(getCollisionMask(floor, x + moverSize - i, y - 1.tiles) and ZoneCollision.BLOCK_NW != 0) return false
        if(getCollisionMask(floor, x + moverSize, y + i - 1.tiles) and ZoneCollision.BLOCK_NW != 0) return false
    }
    return true
}

internal fun WorldMap.canWalkNorthWest(floor: FloorUnit, x: TileUnit, y: TileUnit, moverSize: TileUnit): Boolean {
    if(getCollisionMask(floor, x + moverSize - 1.tiles, y + moverSize) and ZoneCollision.BLOCK_S != 0) return false
    if(getCollisionMask(floor, x - 1.tiles, y) and ZoneCollision.BLOCK_E != 0) return false
    for(i in 0.tiles until moverSize) {
        if(getCollisionMask(floor, x - 1.tiles, y + moverSize - i) and ZoneCollision.BLOCK_SE != 0) return false
        if(getCollisionMask(floor, x + i - 1.tiles, y + moverSize) and ZoneCollision.BLOCK_SE != 0) return false
    }
    return true
}

internal fun WorldMap.canWalkNorthEast(floor: FloorUnit, x: TileUnit, y: TileUnit, moverSize: TileUnit): Boolean {
    if(getCollisionMask(floor, x, y + moverSize) and ZoneCollision.BLOCK_S != 0) return false
    if(getCollisionMask(floor, x + moverSize, y) and ZoneCollision.BLOCK_W != 0) return false
    for(i in 0.tiles until moverSize) {
        if(getCollisionMask(floor, x + moverSize, y + moverSize - i) and ZoneCollision.BLOCK_SW != 0) return false
        if(getCollisionMask(floor, x + moverSize - i, y + moverSize - i) and ZoneCollision.BLOCK_SW != 0) return false
    }
    return true
}