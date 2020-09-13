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
package io.guthix.oldscape.server.pathing

import io.guthix.oldscape.server.world.WorldMap
import io.guthix.oldscape.server.world.map.ZoneCollision
import io.guthix.oldscape.server.world.map.dim.FloorUnit
import io.guthix.oldscape.server.world.map.dim.TileUnit
import io.guthix.oldscape.server.world.map.dim.tiles
import io.guthix.oldscape.server.world.map.dim.until

internal fun WorldMap.canWalkWest(floor: FloorUnit, x: TileUnit, y: TileUnit, moverSize: TileUnit) =
    (0.tiles until moverSize).none { getCollisionMask(floor, x - 1.tiles, y + it) and ZoneCollision.BLOCK_E != 0 }

internal fun WorldMap.canWalkEast(floor: FloorUnit, x: TileUnit, y: TileUnit, moverSize: TileUnit) =
    (0.tiles until moverSize).none { getCollisionMask(floor, x + moverSize, y + it) and ZoneCollision.BLOCK_W != 0 }

internal fun WorldMap.canWalkSouth(floor: FloorUnit, x: TileUnit, y: TileUnit, moverSize: TileUnit) =
    (0.tiles until moverSize).none { getCollisionMask(floor, x + it, y - 1.tiles) and ZoneCollision.BLOCK_N != 0 }

internal fun WorldMap.canWalkNorth(floor: FloorUnit, x: TileUnit, y: TileUnit, moverSize: TileUnit) =
    (0.tiles until moverSize).none { getCollisionMask(floor, x + it, y + moverSize) and ZoneCollision.BLOCK_S != 0 }


internal fun WorldMap.canWalkSouthWest(floor: FloorUnit, x: TileUnit, y: TileUnit, moverSize: TileUnit): Boolean {
    if (getCollisionMask(floor, x + moverSize - 1.tiles, y - 1.tiles) and ZoneCollision.BLOCK_N != 0) return false
    if (getCollisionMask(floor, x - 1.tiles, y + moverSize - 1.tiles) and ZoneCollision.BLOCK_E != 0) return false
    for (i in 0.tiles until moverSize) {
        if (getCollisionMask(floor, x + i - 1.tiles, y - 1.tiles) and ZoneCollision.BLOCK_NE != 0) return false
        if (getCollisionMask(floor, x - 1.tiles, y + i - 1.tiles) and ZoneCollision.BLOCK_NE != 0) return false
    }
    return true
}

internal fun WorldMap.canWalkSouthEast(floor: FloorUnit, x: TileUnit, y: TileUnit, moverSize: TileUnit): Boolean {
    if (getCollisionMask(floor, x, y - 1.tiles) and ZoneCollision.BLOCK_N != 0) return false
    if (getCollisionMask(floor, x + moverSize, y + moverSize - 1.tiles) and ZoneCollision.BLOCK_W != 0) return false
    for (i in 0.tiles until moverSize) {
        if (getCollisionMask(floor, x + moverSize - i, y - 1.tiles) and ZoneCollision.BLOCK_NW != 0) return false
        if (getCollisionMask(floor, x + moverSize, y + i - 1.tiles) and ZoneCollision.BLOCK_NW != 0) return false
    }
    return true
}

internal fun WorldMap.canWalkNorthWest(floor: FloorUnit, x: TileUnit, y: TileUnit, moverSize: TileUnit): Boolean {
    if (getCollisionMask(floor, x + moverSize - 1.tiles, y + moverSize) and ZoneCollision.BLOCK_S != 0) return false
    if (getCollisionMask(floor, x - 1.tiles, y) and ZoneCollision.BLOCK_E != 0) return false
    for (i in 0.tiles until moverSize) {
        if (getCollisionMask(floor, x - 1.tiles, y + moverSize - i) and ZoneCollision.BLOCK_SE != 0) return false
        if (getCollisionMask(floor, x + i - 1.tiles, y + moverSize) and ZoneCollision.BLOCK_SE != 0) return false
    }
    return true
}

internal fun WorldMap.canWalkNorthEast(floor: FloorUnit, x: TileUnit, y: TileUnit, moverSize: TileUnit): Boolean {
    if (getCollisionMask(floor, x, y + moverSize) and ZoneCollision.BLOCK_S != 0) return false
    if (getCollisionMask(floor, x + moverSize, y) and ZoneCollision.BLOCK_W != 0) return false
    for (i in 0.tiles until moverSize) {
        if (getCollisionMask(floor, x + moverSize, y + moverSize - i) and ZoneCollision.BLOCK_SW != 0) return false
        if (getCollisionMask(floor, x + moverSize - i, y + moverSize - i) and ZoneCollision.BLOCK_SW != 0) return false
    }
    return true
}