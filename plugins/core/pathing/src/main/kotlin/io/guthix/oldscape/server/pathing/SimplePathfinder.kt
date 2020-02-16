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
package io.guthix.oldscape.server.pathing

import io.guthix.oldscape.server.world.WorldMap
import io.guthix.oldscape.server.world.mapsquare.zone.ZoneCollision
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import io.guthix.oldscape.server.world.mapsquare.zone.tile.TileUnit
import io.guthix.oldscape.server.world.mapsquare.zone.tile.tiles
import io.guthix.oldscape.server.world.mapsquare.zone.tile.until
import java.util.ArrayList

private class Point(val x: TileUnit, val y: TileUnit, val dir: Direction)

fun simplePathSearch(start: Tile, dest: Destination, moverSize: TileUnit, map: WorldMap): MutableList<Tile> {
    val points = ArrayList<Point>()
    var curX = start.x
    var curY = start.y
    while (curX != dest.x || curY != dest.y) {
        val directions = getDirection(curX.value, curY.value, dest.x.value, dest.y.value)
        if (dest.reached(curX, curY, moverSize)) { // Reached destination
            break
        }
        if (directions.size > 1) { // Ensure destination is not approached diagonally
            val dir = directions[0]
            if (curX.value + dir.stepX == dest.x.value && curY.value + dir.stepY == dest.y.value) {
                directions[0] = directions[directions.size - 1]
                directions[directions.size - 1] = dir
            }
        }
        var found = true
        for (dir in directions) {
            found = true
            when (dir) {
                Direction.NORTH -> run {
                    if (map.getCollisionMask(start.floor, curX, curY + moverSize) and ZoneCollision.BLOCK_SOUTH_EAST != 0
                        || map.getCollisionMask(start.floor, curX + (moverSize - 1.tiles), curY + moverSize) and ZoneCollision.BLOCK_SOUTH_WEST != 0
                    ) {
                        found = false
                        return@run
                    }
                    for (i in 1.tiles until moverSize - 1.tiles) {
                        if (map.getCollisionMask(start.floor, curX + i, curY + moverSize) and ZoneCollision.BLOCK_SOUTH_EW != 0) {
                            found = false
                            return@run
                        }
                    }
                    points.add(Point(curX, curY + 1.tiles, dir))
                    curY++
                }
                Direction.NORTH_EAST -> run {
                    if (map.getCollisionMask(start.floor, curX + 1.tiles, curY + moverSize) and ZoneCollision.BLOCK_SOUTH_EAST != 0
                        || map.getCollisionMask(start.floor, curX + moverSize, curY + moverSize) and ZoneCollision.BLOCK_SOUTH_WEST != 0
                        || map.getCollisionMask(start.floor, curX + moverSize, curY + 1.tiles) and ZoneCollision.BLOCK_NORTH_WEST != 0
                    ) {
                        found = false
                        return@run
                    }
                    for (i in 1.tiles until moverSize - 1.tiles) {
                        if (map.getCollisionMask(start.floor, curX + (i + 1.tiles), curY + moverSize) and ZoneCollision.BLOCK_SOUTH_EW != 0
                            || map.getCollisionMask(start.floor, curX + moverSize, curY + (i + 1.tiles)) and ZoneCollision.BLOCK_WEST_NS != 0
                        ) {
                            found = false
                            return@run
                        }
                    }
                    points.add(Point(curX + 1.tiles, curY + 1.tiles, dir))
                    curX++
                    curY++
                }
                Direction.EAST -> run {
                    if (map.getCollisionMask(start.floor, curX + moverSize, curY) and ZoneCollision.BLOCK_NORTH_WEST != 0
                        || map.getCollisionMask(start.floor, curX + moverSize, curY + (moverSize - 1.tiles)) and ZoneCollision.BLOCK_SOUTH_WEST != 0
                    ) {
                        found = false
                        return@run
                    }
                    for (i in 1 until moverSize.value - 1) {
                        if (map.getCollisionMask(start.floor, curX + moverSize, curY + i.tiles) and ZoneCollision.BLOCK_WEST_NS != 0) {
                            found = false
                            return@run
                        }
                    }
                    points.add(Point(curX + 1.tiles, curY, dir))
                    curX++
                }
                Direction.SOUTH_EAST -> run {
                    if (map.getCollisionMask(start.floor, curX + 1.tiles, curY - 1.tiles) and ZoneCollision.BLOCK_NORTH_EAST != 0
                        || map.getCollisionMask(start.floor, curX + moverSize, curY + (moverSize - 2.tiles)) and ZoneCollision.BLOCK_SOUTH_WEST != 0
                        || map.getCollisionMask(start.floor, curX + moverSize, curY - 1.tiles) and ZoneCollision.BLOCK_NORTH_WEST != 0
                    ) {
                        found = false
                        return@run
                    }
                    for (i in 1.tiles until moverSize - 1.tiles) {
                        if (map.getCollisionMask(start.floor, curX + moverSize, curY + (i - 1.tiles)) and ZoneCollision.BLOCK_WEST_NS != 0
                            || map.getCollisionMask(start.floor, curX + (i + 1.tiles), curY - 1.tiles) and ZoneCollision.BLOCK_NORTH_EW != 0
                        ) {
                            found = false
                            return@run
                        }
                    }
                    points.add(Point(curX + 1.tiles, curY - 1.tiles, dir))
                    curX++
                    curY--
                }
                Direction.SOUTH -> run {
                    if (map.getCollisionMask(start.floor, curX, curY - 1.tiles) and ZoneCollision.BLOCK_NORTH_EAST != 0
                        || map.getCollisionMask(start.floor, curX + (moverSize - 1.tiles), curY - 1.tiles) and ZoneCollision.BLOCK_NORTH_WEST != 0
                    ) {
                        found = false
                        return@run
                    }
                    for (i in 1.tiles until moverSize - 1.tiles) {
                        if (map.getCollisionMask(start.floor, curX + i, curY - 1.tiles) and ZoneCollision.BLOCK_NORTH_EW != 0) {
                            found = false
                            return@run
                        }
                    }
                    points.add(Point(curX, curY - 1.tiles, dir))
                    curY--
                }
                Direction.SOUTH_WEST -> run {
                    if (map.getCollisionMask(start.floor, curX - 1.tiles, curY + (moverSize - 2.tiles)) and ZoneCollision.BLOCK_SOUTH_EAST != 0
                        || map.getCollisionMask(start.floor, curX - 1.tiles, curY - 1.tiles) and ZoneCollision.BLOCK_NORTH_EAST != 0
                        || map.getCollisionMask(start.floor, curX + (moverSize - 2.tiles), curY - 1.tiles) and ZoneCollision.BLOCK_NORTH_WEST != 0
                    ) {
                        found = false
                        return@run
                    }
                    for (i in 1.tiles until moverSize - 1.tiles) {
                        if (map.getCollisionMask(start.floor, curX - 1.tiles, curY + (i - 1.tiles)) and ZoneCollision.BLOCK_EAST_NS != 0
                            || map.getCollisionMask(start.floor, curX + (i - 1.tiles), curY - 1.tiles) and ZoneCollision.BLOCK_NORTH_EW != 0
                        ) {
                            found = false
                            return@run
                        }
                    }
                    points.add(Point(curX - 1.tiles, curY - 1.tiles, dir))
                    curX--
                    curY--
                }
                Direction.WEST -> run {
                    if (map.getCollisionMask(start.floor, curX - 1.tiles, curY) and ZoneCollision.BLOCK_NORTH_EAST != 0
                        || map.getCollisionMask(start.floor, curX - 1.tiles, curY + (moverSize - 1.tiles)) and ZoneCollision.BLOCK_SOUTH_EAST != 0
                    ) {
                        found = false
                        return@run
                    }
                    for (i in 1.tiles until moverSize - 1.tiles) {
                        if (map.getCollisionMask(start.floor, curX - 1.tiles, curY + i) and ZoneCollision.BLOCK_EAST_NS != 0) {
                            found = false
                            return@run
                        }
                    }
                    points.add(Point(curX - 1.tiles, curY, dir))
                    curX--
                }
                Direction.NORTH_WEST -> run {
                    if (map.getCollisionMask(start.floor, curX - 1.tiles, curY + 1.tiles) and ZoneCollision.BLOCK_NORTH_EAST != 0
                        || map.getCollisionMask(start.floor, curX - 1.tiles, curY + moverSize) and ZoneCollision.BLOCK_SOUTH_EAST != 0
                        || map.getCollisionMask(start.floor, curX, curY + moverSize) and ZoneCollision.BLOCK_SOUTH_WEST != 0
                    ) {
                        found = false
                        return@run
                    }
                    for (i in 1.tiles until moverSize - 1.tiles) {
                        if (map.getCollisionMask(start.floor, curX - 1.tiles, curY + (i + 1.tiles)) and ZoneCollision.BLOCK_EAST_NS != 0
                            || map.getCollisionMask(start.floor, curX + (i - 1.tiles), curY + moverSize) and ZoneCollision.BLOCK_SOUTH_EW != 0
                        ) {
                            found = false
                            return@run
                        }
                    }
                    points.add(Point(curX - 1.tiles, curY + 1.tiles, dir))
                    curX--
                    curY++
                }
            }
            if (found) {
                break
            }
        }
        if (!found) {
            break
        }
    }
    val path = mutableListOf<Tile>()
    if (points.isNotEmpty()) {
        val last: Direction? = null
        for (i in 0 until points.size - 1) {
            val p = points[i]
            if (p.dir !== last) {
                path.add(Tile(dest.floor, p.x, p.y))
            }
        }
        val p = points[points.size - 1]
        path.add(Tile(dest.floor, p.x, p.y))
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