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
import io.guthix.oldscape.server.world.map.Tile
import io.guthix.oldscape.server.world.map.dim.TileUnit
import io.guthix.oldscape.server.world.map.dim.tiles

fun simplePathSearch(start: Tile, dest: Destination, moverSize: TileUnit, map: WorldMap): MutableList<Tile> {
    var curX = start.x
    var curY = start.y
    val path = mutableListOf<Tile>()
    while (!dest.reached(curX, curY, moverSize)) {
        val directions = getDirection(curX.value, curY.value, dest.x.value, dest.y.value)
        var finalDestinationFound = true
        directions@ for (dir in directions) {
            when (dir) {
                Direction.SOUTH -> if (map.canWalkSouth(start.floor, curX, curY, moverSize)) {
                    curY--
                    path.add(Tile(start.floor, curX, curY))
                    finalDestinationFound = false
                    break@directions
                }
                Direction.NORTH -> if (map.canWalkNorth(start.floor, curX, curY, moverSize)) {
                    curY++
                    path.add(Tile(start.floor, curX, curY))
                    finalDestinationFound = false
                    break@directions
                }
                Direction.WEST -> if (map.canWalkWest(start.floor, curX, curY, moverSize)) {
                    curX--
                    path.add(Tile(start.floor, curX, curY))
                    finalDestinationFound = false
                    break@directions
                }
                Direction.EAST -> if (map.canWalkEast(start.floor, curX, curY, moverSize)) {
                    curX++
                    path.add(Tile(start.floor, curX, curY))
                    finalDestinationFound = false
                    break@directions
                }
                Direction.SOUTH_WEST -> if (map.canWalkSouthWest(start.floor, curX, curY, moverSize)
                    && !(dest.x == curX - 1.tiles && dest.y == curY - 1.tiles)
                ) {
                    curX--
                    curY--
                    path.add(Tile(start.floor, curX, curY))
                    finalDestinationFound = false
                    break@directions
                }
                Direction.NORTH_WEST -> if (map.canWalkNorthWest(start.floor, curX, curY, moverSize)
                    && !(dest.x == curX - 1.tiles && dest.y == curY + 1.tiles)
                ) {
                    curX--
                    curY++
                    path.add(Tile(start.floor, curX, curY))
                    finalDestinationFound = false
                    break@directions
                }
                Direction.SOUTH_EAST -> if (map.canWalkSouthEast(start.floor, curX, curY, moverSize)
                    && !(dest.x == curX + 1.tiles && dest.y == curY - 1.tiles)
                ) {
                    curX++
                    curY--
                    path.add(Tile(start.floor, curX, curY))
                    finalDestinationFound = false
                    break@directions
                }
                Direction.NORTH_EAST -> if (map.canWalkNorthEast(start.floor, curX, curY, moverSize)
                    && !(dest.x == curX + 1.tiles && dest.y == curY + 1.tiles)
                ) {
                    curX++
                    curY++ // TODO mabe optimize this use Directions array?
                    path.add(Tile(start.floor, curX, curY))
                    finalDestinationFound = false
                    break@directions
                }
            }
        }
        if (finalDestinationFound) {
            break
        }
    }
    return path
}

private val directDirections = listOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)

private fun getDirection(startX: Int, startY: Int, destX: Int, destY: Int): List<Direction> {
    if (startX == destX) {
        if (startY > destY) {
            return listOf(Direction.SOUTH)
        } else if (startY < destY) {
            return listOf(Direction.NORTH)
        }
    } else if (startY == destY) {
        if (startX > destX) {
            return listOf(Direction.WEST)
        } else if (startX < destX) {
            return listOf(Direction.EAST)
        }
    } else {
        if (startX < destX && startY < destY) {
            return listOf(Direction.NORTH_EAST, Direction.EAST, Direction.NORTH)
        } else if (startX < destX && startY > destY) {
            return listOf(Direction.SOUTH_EAST, Direction.EAST, Direction.SOUTH)
        } else if (startX > destX && startY < destY) {
            return listOf(Direction.NORTH_WEST, Direction.WEST, Direction.NORTH)
        } else if (startX > destX && startY > destY) {
            return listOf(Direction.SOUTH_WEST, Direction.WEST, Direction.SOUTH)
        }
    }
    return directDirections.shuffled()
}