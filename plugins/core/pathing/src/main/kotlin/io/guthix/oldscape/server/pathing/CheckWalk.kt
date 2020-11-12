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

import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.map.Zone
import io.guthix.oldscape.server.world.map.dim.FloorUnit
import io.guthix.oldscape.server.world.map.dim.TileUnit
import io.guthix.oldscape.server.world.map.dim.tiles
import io.guthix.oldscape.server.world.map.dim.until

internal fun World.canWalkWest(floor: FloorUnit, x: TileUnit, y: TileUnit, moverSize: TileUnit) =
    (0.tiles until moverSize).none { getCollision(floor, x - 1.tiles, y + it) and Zone.BLOCK_E != 0 }

internal fun World.canWalkEast(floor: FloorUnit, x: TileUnit, y: TileUnit, moverSize: TileUnit) =
    (0.tiles until moverSize).none { getCollision(floor, x + moverSize, y + it) and Zone.BLOCK_W != 0 }

internal fun World.canWalkSouth(floor: FloorUnit, x: TileUnit, y: TileUnit, moverSize: TileUnit) =
    (0.tiles until moverSize).none { getCollision(floor, x + it, y - 1.tiles) and Zone.BLOCK_N != 0 }

internal fun World.canWalkNorth(floor: FloorUnit, x: TileUnit, y: TileUnit, moverSize: TileUnit) =
    (0.tiles until moverSize).none { getCollision(floor, x + it, y + moverSize) and Zone.BLOCK_S != 0 }


internal fun World.canWalkSouthWest(floor: FloorUnit, x: TileUnit, y: TileUnit, moverSize: TileUnit): Boolean {
    if (getCollision(floor, x + moverSize - 1.tiles, y - 1.tiles) and Zone.BLOCK_N != 0) return false
    if (getCollision(floor, x - 1.tiles, y + moverSize - 1.tiles) and Zone.BLOCK_E != 0) return false
    for (i in 0.tiles until moverSize) {
        if (getCollision(floor, x + i - 1.tiles, y - 1.tiles) and Zone.BLOCK_NE != 0) return false
        if (getCollision(floor, x - 1.tiles, y + i - 1.tiles) and Zone.BLOCK_NE != 0) return false
    }
    return true
}

internal fun World.canWalkSouthEast(floor: FloorUnit, x: TileUnit, y: TileUnit, moverSize: TileUnit): Boolean {
    if (getCollision(floor, x, y - 1.tiles) and Zone.BLOCK_N != 0) return false
    if (getCollision(floor, x + moverSize, y + moverSize - 1.tiles) and Zone.BLOCK_W != 0) return false
    for (i in 0.tiles until moverSize) {
        if (getCollision(floor, x + moverSize - i, y - 1.tiles) and Zone.BLOCK_NW != 0) return false
        if (getCollision(floor, x + moverSize, y + i - 1.tiles) and Zone.BLOCK_NW != 0) return false
    }
    return true
}

internal fun World.canWalkNorthWest(floor: FloorUnit, x: TileUnit, y: TileUnit, moverSize: TileUnit): Boolean {
    if (getCollision(floor, x + moverSize - 1.tiles, y + moverSize) and Zone.BLOCK_S != 0) return false
    if (getCollision(floor, x - 1.tiles, y) and Zone.BLOCK_E != 0) return false
    for (i in 0.tiles until moverSize) {
        if (getCollision(floor, x - 1.tiles, y + moverSize - i) and Zone.BLOCK_SE != 0) return false
        if (getCollision(floor, x + i - 1.tiles, y + moverSize) and Zone.BLOCK_SE != 0) return false
    }
    return true
}

internal fun World.canWalkNorthEast(floor: FloorUnit, x: TileUnit, y: TileUnit, moverSize: TileUnit): Boolean {
    if (getCollision(floor, x, y + moverSize) and Zone.BLOCK_S != 0) return false
    if (getCollision(floor, x + moverSize, y) and Zone.BLOCK_W != 0) return false
    for (i in 0.tiles until moverSize) {
        if (getCollision(floor, x + moverSize, y + moverSize - i) and Zone.BLOCK_SW != 0) return false
        if (getCollision(floor, x + moverSize - i, y + moverSize - i) and Zone.BLOCK_SW != 0) return false
    }
    return true
}