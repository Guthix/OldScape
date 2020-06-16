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
package io.guthix.oldscape.server.pathing

import io.guthix.oldscape.server.dimensions.TileUnit
import io.guthix.oldscape.server.dimensions.tiles
import io.guthix.oldscape.server.world.WorldMap
import io.guthix.oldscape.server.world.map.Tile
import kotlin.math.abs

private const val MAX_QUEUE_LENGTH = 4096
private val SEARCH_SIZE = 104.tiles
private val ALTERNATIVE_ROUTE_RANGE = 10.tiles
private const val MAX_ALTERNATIVE_PATH = 99

fun breadthFirstSearch(
    start: Tile,
    dest: Destination,
    moverSize: TileUnit,
    findAlternative: Boolean,
    map: WorldMap
): MutableList<Tile> {
    val pathBaseX = start.x - (SEARCH_SIZE / 2.tiles)
    val pathBaseY = start.y - (SEARCH_SIZE / 2.tiles)
    var endX = dest.x
    var endY = dest.y
    if (dest.reached(start.x, start.y, moverSize)) { //Already at location
        return emptyList<Tile>().toMutableList()
    }
    val directions = Array(SEARCH_SIZE.value) { IntArray(SEARCH_SIZE.value) }
    val distances = Array(SEARCH_SIZE.value) { IntArray(SEARCH_SIZE.value) { Int.MAX_VALUE } }

    fun canFindPath(start: Tile, dest: Destination, moverSize: TileUnit): Boolean {
        val bufferX = IntArray(MAX_QUEUE_LENGTH)
        val bufferY = IntArray(MAX_QUEUE_LENGTH)
        var currentIndex = 0
        var nextIndex = 0
        bufferX[nextIndex] = start.x.value
        bufferY[nextIndex] = start.y.value
        nextIndex++
        var curGraphX: TileUnit
        var curGraphY: TileUnit
        while (currentIndex != nextIndex) { // While path is not found
            val curX = bufferX[currentIndex].tiles
            val curY = bufferY[currentIndex].tiles
            currentIndex = (currentIndex + 1) and 0xFFF
            curGraphX = curX - pathBaseX
            curGraphY = curY - pathBaseY
            if (dest.reached(curX, curY, moverSize)) {
                endX = curX
                endY = curY
                return true
            }
            val nextDistance = distances[curGraphX.value][curGraphY.value] + 1
            if (curGraphX > 0.tiles && directions[curGraphX.value - 1][curGraphY.value] == 0
                && map.canWalkWest(start.floor, curX, curY, moverSize)
            ) {
                bufferX[nextIndex] = curX.value - 1
                bufferY[nextIndex] = curY.value
                nextIndex = (nextIndex + 1) and 0xFFF
                directions[curGraphX.value - 1][curGraphY.value] = Direction.EAST.mask
                distances[curGraphX.value - 1][curGraphY.value] = nextDistance
            }
            if (curGraphX < SEARCH_SIZE - 1.tiles && directions[curGraphX.value + 1][curGraphY.value] == 0
                && map.canWalkEast(start.floor, curX, curY, moverSize)
            ) {
                bufferX[nextIndex] = curX.value + 1
                bufferY[nextIndex] = curY.value
                nextIndex = (nextIndex + 1) and 0xFFF
                directions[curGraphX.value + 1][curGraphY.value] = Direction.WEST.mask
                distances[curGraphX.value + 1][curGraphY.value] = nextDistance
            }
            if (curGraphY > 0.tiles && directions[curGraphX.value][curGraphY.value - 1] == 0 &&
                map.canWalkSouth(start.floor, curX, curY, moverSize)
            ) {
                bufferX[nextIndex] = curX.value
                bufferY[nextIndex] = curY.value - 1
                nextIndex = (nextIndex + 1) and 0xFFF
                directions[curGraphX.value][curGraphY.value - 1] = Direction.NORTH.mask
                distances[curGraphX.value][curGraphY.value - 1] = nextDistance
            }
            if (curGraphY < SEARCH_SIZE - 1.tiles && directions[curGraphX.value][curGraphY.value + 1] == 0
                && map.canWalkNorth(start.floor, curX, curY, moverSize)
            ) {
                bufferX[nextIndex] = curX.value
                bufferY[nextIndex] = curY.value + 1
                nextIndex = (nextIndex + 1) and 0xFFF
                directions[curGraphX.value][curGraphY.value + 1] = Direction.SOUTH.mask
                distances[curGraphX.value][curGraphY.value + 1] = nextDistance
            }
            if (curGraphX > 0.tiles && curGraphY > 0.tiles && directions[curGraphX.value - 1][curGraphY.value - 1] == 0
                && map.canWalkSouthWest(start.floor, curX, curY, moverSize)
            ) {
                bufferX[nextIndex] = curX.value - 1
                bufferY[nextIndex] = curY.value - 1
                nextIndex = (nextIndex + 1) and 0xFFF
                directions[curGraphX.value - 1][curGraphY.value - 1] = Direction.NORTH.mask or Direction.EAST.mask
                distances[curGraphX.value - 1][curGraphY.value - 1] = nextDistance
            }
            if (curGraphX < SEARCH_SIZE - 1.tiles && curGraphY > 0.tiles
                && directions[curGraphX.value + 1][curGraphY.value - 1] == 0
                && map.canWalkSouthEast(start.floor, curX, curY, moverSize)
            ) {
                bufferX[nextIndex] = curX.value + 1
                bufferY[nextIndex] = curY.value - 1
                nextIndex = (nextIndex + 1) and 0xFFF
                directions[curGraphX.value + 1][curGraphY.value - 1] = Direction.NORTH.mask or Direction.WEST.mask
                distances[curGraphX.value + 1][curGraphY.value - 1] = nextDistance
            }
            if (curGraphX > 0.tiles && curGraphY < SEARCH_SIZE - 1.tiles
                && directions[curGraphX.value - 1][curGraphY.value + 1] == 0
                && map.canWalkNorthWest(start.floor, curX, curY, moverSize)
            ) {
                bufferX[nextIndex] = curX.value - 1
                bufferY[nextIndex] = curY.value + 1
                nextIndex = (nextIndex + 1) and 0xFFF
                directions[curGraphX.value - 1][curGraphY.value + 1] = Direction.SOUTH.mask or Direction.EAST.mask
                distances[curGraphX.value - 1][curGraphY.value + 1] = nextDistance
            }
            if (curGraphX < SEARCH_SIZE - 1.tiles && curGraphY < SEARCH_SIZE - 1.tiles
                && directions[curGraphX.value + 1][curGraphY.value + 1] == 0
                && map.canWalkNorthEast(start.floor, curX, curY, moverSize)
            ) {
                bufferX[nextIndex] = curX.value + 1
                bufferY[nextIndex] = curY.value + 1
                nextIndex = (nextIndex + 1) and 0xFFF
                directions[curGraphX.value + 1][curGraphY.value + 1] = Direction.SOUTH.mask or Direction.WEST.mask
                distances[curGraphX.value + 1][curGraphY.value + 1] = nextDistance
            }
        }
        return false
    }

    fun findAlternativeDestination(dest: Destination) {
        var lowCost = Integer.MAX_VALUE
        var lowDist = Integer.MAX_VALUE
        for (x in dest.x - ALTERNATIVE_ROUTE_RANGE..dest.x + ALTERNATIVE_ROUTE_RANGE) {
            for (y in dest.y - ALTERNATIVE_ROUTE_RANGE..dest.y + ALTERNATIVE_ROUTE_RANGE) {
                val localX = x - pathBaseX
                val localY = y - pathBaseY
                if (localX >= SEARCH_SIZE || localY >= SEARCH_SIZE || localX < 0.tiles || localY < 0.tiles
                    || distances[localX.value][localY.value] >= MAX_ALTERNATIVE_PATH
                ) {
                    continue
                }
                val dx = abs((dest.x - x).value)
                val dy = abs((dest.y - y).value)
                val cost = dx * dx + dy * dy
                if (cost < lowCost || (cost == lowCost && distances[localX.value][localY.value] < lowDist)) {
                    lowCost = cost
                    lowDist = distances[localX.value][localY.value]
                    endY = y
                    endX = x
                }
            }
        }
    }

    val foundDestination = canFindPath(start, dest, moverSize)
    if (!foundDestination && findAlternative) {
        findAlternativeDestination(dest)
    }
    if (start.x == endX && start.y == endY) { //Alternative destination is the same as current location
        return emptyList<Tile>().toMutableList()
    }

    // Trace back the path from destination to the start using the direction masks
    var traceX = endX
    var traceY = endY
    var direction: Int
    val path = mutableListOf<Tile>()
    while (traceX != start.x || traceY != start.y) {
        direction = directions[(traceX - pathBaseX).value][(traceY - pathBaseY).value]
        path.add(Tile(dest.floor, traceX, traceY))
        if (direction and Direction.EAST.mask != 0) {
            traceX++
        } else if (direction and Direction.WEST.mask != 0) {
            traceX--
        }
        if (direction and Direction.NORTH.mask != 0) {
            traceY++
        } else if (direction and Direction.SOUTH.mask != 0) {
            traceY--
        }
    }
    return path.asReversed()
}