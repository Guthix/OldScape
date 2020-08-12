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

import io.guthix.oldscape.server.world.map.dim.TileUnit
import io.guthix.oldscape.server.world.map.dim.abs
import io.guthix.oldscape.server.world.map.dim.tiles
import io.guthix.oldscape.server.world.WorldMap
import io.guthix.oldscape.server.world.map.Tile
import io.guthix.oldscape.server.world.map.ZoneCollision

private const val SCALE = 16

private val HALF_TILE = scaleUp(1.tiles) / 2.tiles

private fun scaleUp(tiles: TileUnit) = (tiles.value shl SCALE).tiles

private fun scaleDown(tiles: TileUnit) = (tiles.value ushr SCALE).tiles

fun inLineOfSight(
    start: Tile,
    width: TileUnit,
    height: TileUnit,
    end: Tile,
    destWidth: TileUnit,
    destHeight: TileUnit,
    map: WorldMap
): Boolean {
    val startX = coordinate(start.x, end.x, width)
    val startY = coordinate(start.y, end.y, height)
    val endX = coordinate(end.x, start.x, destWidth)
    val endY = coordinate(end.y, start.y, destHeight)
    val deltaX = endX - startX
    val deltaY = endY - startY
    val travelEast = deltaX >= 0.tiles
    val travelNorth = deltaY >= 0.tiles
    val xFlags = if (travelEast) ZoneCollision.BLOCK_HIGH_W else ZoneCollision.BLOCK_HIGH_E
    val yFlags = if (travelNorth) ZoneCollision.BLOCK_HIGH_S else ZoneCollision.BLOCK_HIGH_N
    if (abs(deltaX) > abs(deltaY)) {
        val offsetX = if (travelEast) 1.tiles else (-1).tiles
        val offsetY = if (travelNorth) 0.tiles else (-1).tiles
        var scaledY = scaleUp(startY) + HALF_TILE + offsetY
        val tangent = scaleUp(deltaY) / abs(deltaX)
        var currX = startX
        while (currX != endX) {
            currX += offsetX
            val currY = scaleDown(scaledY)
            if (map.getCollisionMask(start.floor, currX, currY) and xFlags != 0) {
                return false
            }
            scaledY += tangent
            val nextY = scaleDown(scaledY)
            if (nextY != currY && map.getCollisionMask(start.floor, currX, currY) and yFlags != 0) {
                return false
            }
        }
    } else {
        val offsetX = if (travelEast) 0.tiles else (-1).tiles
        val offsetY = if (travelNorth) 1.tiles else (-1).tiles
        var scaledX = scaleUp(startX) + HALF_TILE + offsetX
        val tangent = scaleUp(deltaX) / abs(deltaY)
        var currY = startY
        while (currY != endY) {
            currY += offsetY
            val currX = scaleDown(scaledX)
            if (map.getCollisionMask(start.floor, currX, currY) and yFlags != 0) {
                return false
            }
            scaledX += tangent
            val nextX = scaleDown(scaledX)
            if (nextX != currX && map.getCollisionMask(start.floor, currX, currY) and xFlags != 0) {
                return false
            }
        }
    }

    return true
}

private fun coordinate(a: TileUnit, b: TileUnit, size: TileUnit): TileUnit {
    return when {
        a >= b -> a
        a + size - 1.tiles <= b -> a + size - 1.tiles
        else -> b
    }
}