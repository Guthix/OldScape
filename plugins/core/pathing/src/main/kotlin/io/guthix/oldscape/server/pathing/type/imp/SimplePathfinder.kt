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
package io.guthix.oldscape.server.pathing.type.imp

import io.guthix.oldscape.server.pathing.type.*
import io.guthix.oldscape.server.pathing.type.canWalkNorth
import io.guthix.oldscape.server.pathing.type.canWalkSouth
import io.guthix.oldscape.server.world.WorldMap
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import io.guthix.oldscape.server.world.mapsquare.zone.tile.TileUnit

fun simplePathSearch(start: Tile, dest: Destination, moverSize: TileUnit, map: WorldMap): MutableList<Tile> {
    var curX = start.x
    var curY = start.y
    val path = mutableListOf<Tile>()
    while (!dest.reached(curX, curY, moverSize)) {
        val directions = getDirection(curX.value, curY.value, dest.x.value, dest.y.value)
        var finalDestinationFound = true
        directions@ for (dir in directions) {
            when (dir) {
                Direction.SOUTH -> if(map.canWalkSouth(start.floor, curX, curY, moverSize)) {
                    curY--
                    path.add(Tile(start.floor, curX, curY))
                    finalDestinationFound = false
                    break@directions
                }
                Direction.NORTH -> if(map.canWalkNorth(start.floor, curX, curY, moverSize)) {
                    curY++
                    path.add(Tile(start.floor, curX, curY))
                    finalDestinationFound = false
                    break@directions
                }
                Direction.WEST -> if(map.canWalkWest(start.floor, curX, curY, moverSize)) {
                    curX--
                    path.add(Tile(start.floor, curX, curY))
                    finalDestinationFound = false
                    break@directions
                }
                Direction.EAST -> if(map.canWalkEast(start.floor, curX, curY, moverSize)) {
                    curX++
                    path.add(Tile(start.floor, curX, curY))
                    finalDestinationFound = false
                    break@directions
                }
                Direction.SOUTH_WEST -> if(map.canWalkSouthWest(start.floor, curX, curY, moverSize)) {
                    curX--
                    curY--
                    path.add(Tile(start.floor, curX, curY))
                    finalDestinationFound = false
                    break@directions
                }
                Direction.NORTH_WEST -> if(map.canWalkNorthWest(start.floor, curX, curY, moverSize)) {
                    curX--
                    curY++
                    path.add(Tile(start.floor, curX, curY))
                    finalDestinationFound = false
                    break@directions
                }
                Direction.SOUTH_EAST -> if(map.canWalkSouthEast(start.floor, curX, curY, moverSize)) {
                    curX++
                    curY--
                    path.add(Tile(start.floor, curX, curY))
                    finalDestinationFound = false
                    break@directions
                }
                Direction.NORTH_EAST -> if(map.canWalkNorthEast(start.floor, curX, curY, moverSize)) {
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

private fun getDirection(startX: Int, startY: Int, destX: Int, destY: Int): Array<Direction> {
    if (startX == destX) {
        if (startY > destY) {
            return arrayOf(Direction.SOUTH)
        } else if (startY < destY) {
            return arrayOf(Direction.NORTH)
        }
    } else if (startY == destY) {
        return if (startX > destX) {
            arrayOf(Direction.WEST)
        } else arrayOf(Direction.EAST)
    } else {
        if (startX < destX && startY < destY) {
            return arrayOf(Direction.NORTH_EAST, Direction.EAST, Direction.NORTH)
        } else if (startX < destX && startY > destY) {
            return arrayOf(Direction.SOUTH_EAST, Direction.EAST, Direction.SOUTH)
        } else if (startX > destX && startY < destY) {
            return arrayOf(Direction.NORTH_WEST, Direction.WEST, Direction.NORTH)
        } else if (startX > destX && startY > destY) {
            return arrayOf(Direction.SOUTH_WEST, Direction.WEST, Direction.SOUTH)
        }
    }
    return arrayOf()
}