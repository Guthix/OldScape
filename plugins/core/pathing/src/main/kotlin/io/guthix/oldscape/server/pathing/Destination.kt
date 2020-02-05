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
import io.guthix.oldscape.server.world.entity.EntityAttribute
import io.guthix.oldscape.server.world.entity.Loc
import io.guthix.oldscape.server.world.entity.character.player.Player
import io.guthix.oldscape.server.world.mapsquare.FloorUnit
import io.guthix.oldscape.server.world.mapsquare.zone.ZoneCollision
import io.guthix.oldscape.server.world.mapsquare.zone.tile.TileUnit
import io.guthix.oldscape.server.world.mapsquare.zone.tile.tiles

var Player.destination by EntityAttribute<Destination>()

abstract class Destination(val floor: FloorUnit, val x: TileUnit, val y: TileUnit) {
    abstract fun reached(moverX: TileUnit, moverY: TileUnit, moverSize: TileUnit): Boolean
}

class DestinationTile(floor: FloorUnit, x: TileUnit, y: TileUnit) : Destination(floor, x, y) {
    override fun reached(moverX: TileUnit, moverY: TileUnit, moverSize: TileUnit) = x == moverX && y == moverY
}

class DestinationNpc(floor: FloorUnit, x: TileUnit, y: TileUnit) : Destination(floor, x, y) {
    override fun reached(moverX: TileUnit, moverY: TileUnit, moverSize: TileUnit): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class DestinationLocation(
    private val loc: Loc,
    private val map: WorldMap
) : Destination(loc.position.floor, loc.position.x, loc.position.y) {
    override fun reached(moverX: TileUnit, moverY: TileUnit, moverSize: TileUnit): Boolean {
        return when (loc.type) {
            in 0..3, 9 -> reachedWall(moverX, moverY, moverSize)
            in 4..8 -> reachedDecoration(moverX, moverY, moverSize)
            else -> reachedObject(moverX, moverY, moverSize)
        }
    }

    private fun reachedWall(moverX: TileUnit, moverY: TileUnit, moverSize: TileUnit): Boolean {
        if (moverSize == 1.tiles) {
            if (x == moverX && y == moverY) {
                return true
            }
            when (loc.type) {
                0 -> {
                    when (loc.orientation) {
                        ORIENTATION_NORTH -> if(
                            (y == moverY && moverX == x - 1.tiles) ||
                            (x == moverX && moverY == y + 1.tiles &&
                                map.getCollisionMask(floor, moverX, moverY) and ZoneCollision.BLOCK_SOUTH == 0) ||
                            (x == moverX && moverY == y - 1.tiles &&
                                map.getCollisionMask(floor, moverX, moverY) and ZoneCollision.BLOCK_NORTH == 0)
                        ) return true
                        ORIENTATION_EAST -> if(
                            (x == moverX && moverY == y + 1.tiles) ||
                            (y == moverY && moverX == x - 1.tiles &&
                                map.getCollisionMask(floor, moverX, moverY) and ZoneCollision.BLOCK_EAST == 0) ||
                            (y == moverY && moverX == x + 1.tiles &&
                                map.getCollisionMask(floor, moverX, moverY) and ZoneCollision.BLOCK_WEST == 0)
                        ) return true
                        ORIENTATION_SOUTH -> if(
                            (y == moverY && moverX == x + 1.tiles) ||
                            (y == moverY && moverX == x + 1.tiles &&
                                map.getCollisionMask(floor, moverX, moverY) and ZoneCollision.BLOCK_SOUTH == 0) ||
                            (x == moverX && moverY == y - 1.tiles &&
                                map.getCollisionMask(floor, moverX, moverY) and ZoneCollision.BLOCK_NORTH == 0)
                        ) return true
                        ORIENTATION_WEST -> if(
                            (x == moverX && moverY == y - 1.tiles) ||
                            (y == moverY && moverX == x - 1.tiles &&
                                map.getCollisionMask(floor, moverX, moverY) and ZoneCollision.BLOCK_EAST == 0) ||
                            (y == moverY && moverX == x + 1.tiles &&
                                map.getCollisionMask(floor, moverX, moverY) and ZoneCollision.BLOCK_WEST == 0)
                        ) return true
                    }
                }
                2 -> {
                    when (loc.orientation) {
                        ORIENTATION_NORTH -> if(
                            (y == moverY && moverX == x - 1.tiles) ||
                            (x == moverX && moverY == y + 1.tiles) ||
                            (y == moverY && moverX == x + 1.tiles &&
                                map.getCollisionMask(floor, moverX, moverY) and ZoneCollision.BLOCK_WEST == 0) ||
                            (x == moverX && moverY == y - 1.tiles &&
                                map.getCollisionMask(floor, moverX, moverY) and ZoneCollision.BLOCK_NORTH == 0)
                        ) return true
                        ORIENTATION_EAST -> if(
                            (x == moverX && moverY == y + 1.tiles) ||
                            (y == moverY && moverX == x + 1.tiles) ||
                            (y == moverY && moverX == x - 1.tiles &&
                                map.getCollisionMask(floor, moverX, moverY) and ZoneCollision.BLOCK_EAST == 0) ||
                            (x == moverX && moverY == y - 1.tiles &&
                                map.getCollisionMask(floor, moverX, moverY) and ZoneCollision.BLOCK_NORTH == 0)
                        ) return true
                        ORIENTATION_SOUTH -> if(
                            (y == moverY && moverX == x + 1.tiles) ||
                            (x == moverX && moverY == y - 1.tiles) ||
                            (x == moverX && moverY == y + 1.tiles &&
                                map.getCollisionMask(floor, moverX, moverY) and ZoneCollision.BLOCK_SOUTH == 0) ||
                            (y == moverY && moverX == x - 1.tiles &&
                                map.getCollisionMask(floor, moverX, moverY) and ZoneCollision.BLOCK_EAST == 0)
                        ) return true
                        ORIENTATION_WEST -> if(
                            (y == moverY && moverX == x - 1.tiles) ||
                            (x == moverX && moverY == y - 1.tiles) ||
                            (x == moverX && moverY == y + 1.tiles &&
                                map.getCollisionMask(floor, moverX, moverY) and ZoneCollision.BLOCK_SOUTH == 0) ||
                            (y == moverY && moverX == x + 1.tiles &&
                                map.getCollisionMask(floor, moverX, moverY) and ZoneCollision.BLOCK_WEST == 0)
                        ) return true
                    }
                }
                9 -> if(
                    (x == moverX && moverY == y + 1.tiles &&
                        map.getCollisionMask(floor, moverX, moverY) and ZoneCollision.MASK_WALL_SOUTH == 0) ||
                    (x == moverX && moverY == y - 1.tiles &&
                        map.getCollisionMask(floor, moverX, moverY) and ZoneCollision.MASK_WALL_NORTH == 0) ||
                    (y == moverY && moverX == x - 1.tiles &&
                        map.getCollisionMask(floor, moverX, moverY) and ZoneCollision.MASK_WALL_EAST == 0) ||
                    (y == moverY && moverX == x + 1.tiles &&
                        map.getCollisionMask(floor, moverX, moverY) and ZoneCollision.MASK_WALL_WEST == 0)
                ) return true
            }
        } else {
            val actorMaxX = moverX + moverSize - 1.tiles
            val actorMaxY = moverY + moverSize - 1.tiles
            if (x in moverX..actorMaxX && y in moverY..actorMaxY) {
                return true
            }
            when (loc.type) {
                0 -> {
                    when (loc.orientation) {
                        ORIENTATION_NORTH -> if(
                            (y in moverY..actorMaxY && moverY == x - moverSize) ||
                            (x in moverX..actorMaxX && moverY == y + 1.tiles &&
                                map.getCollisionMask(floor, x, moverY) and ZoneCollision.BLOCK_SOUTH == 0) ||
                            (x in moverX..actorMaxX && moverY == y - moverSize &&
                                map.getCollisionMask(floor, x, actorMaxY) and ZoneCollision.BLOCK_NORTH == 0)
                        ) return true
                        ORIENTATION_EAST -> if(
                            (x in moverX..actorMaxX && moverY == y + 1.tiles) ||
                            (y in moverY..actorMaxY && moverX == x + 1.tiles &&
                                map.getCollisionMask(floor, moverX, y) and ZoneCollision.BLOCK_WEST == 0) ||
                            (y in moverY..actorMaxY && moverX == x - moverSize &&
                                map.getCollisionMask(floor, actorMaxX, y) and ZoneCollision.BLOCK_EAST == 0)
                        ) return true
                        ORIENTATION_SOUTH -> if(
                            (y in moverY..actorMaxY && moverX == x + 1.tiles) ||
                            (x in moverX..actorMaxX && moverY == y + 1.tiles &&
                                map.getCollisionMask(floor, x, moverY) and ZoneCollision.BLOCK_SOUTH == 0) ||
                            (x in moverX..actorMaxX && moverY == y - moverSize &&
                                map.getCollisionMask(floor, x, actorMaxY) and ZoneCollision.BLOCK_NORTH == 0)
                        ) return true
                        ORIENTATION_WEST -> if(
                            (x in moverX..actorMaxX && moverY == y - moverSize) ||
                            (y in moverY..actorMaxY && moverX == x + 1.tiles &&
                                map.getCollisionMask(floor, moverX, y) and ZoneCollision.BLOCK_WEST == 0) ||
                            (y in moverY..actorMaxY && moverX == x - moverSize &&
                                map.getCollisionMask(floor, actorMaxX, y) and ZoneCollision.BLOCK_EAST == 0)
                        ) return true
                    }
                }
                2 -> {
                    when (loc.orientation) {
                        ORIENTATION_NORTH -> if(
                            (y in moverY..actorMaxY && moverX == x - moverSize) ||
                            (x in moverX..actorMaxX && moverY == y + 1.tiles) ||
                            (y in moverY..actorMaxY && moverX == x + 1.tiles &&
                                map.getCollisionMask(floor, moverX, y) and ZoneCollision.BLOCK_WEST == 0) ||
                            (x in moverX..actorMaxX && moverY == y - moverSize &&
                                map.getCollisionMask(floor, x, actorMaxY) and ZoneCollision.BLOCK_NORTH == 0)
                        ) return true
                        ORIENTATION_EAST -> if(
                            (x in moverX..actorMaxX && moverY == y + 1.tiles) ||
                            (y in moverY..actorMaxY && moverX == x + 1.tiles) ||
                            (y in moverY..actorMaxY && moverX == x - moverSize &&
                                map.getCollisionMask(floor, actorMaxX, y) and ZoneCollision.BLOCK_EAST == 0) ||
                            (x in moverX..actorMaxX && moverY == y - moverSize
                                && map.getCollisionMask(floor, x, actorMaxY) and ZoneCollision.BLOCK_NORTH == 0)
                        ) return true
                        ORIENTATION_SOUTH -> if(
                            (y in moverY..actorMaxY && moverX == x + 1.tiles) ||
                            (x in moverX..actorMaxX && moverY == y - moverSize) ||
                            (x in moverX..actorMaxX && moverY == y + 1.tiles &&
                                map.getCollisionMask(floor, x, moverY) and ZoneCollision.BLOCK_SOUTH == 0) ||
                            (y in moverY..actorMaxY && moverX == x - moverSize &&
                                map.getCollisionMask(floor, actorMaxX, y) and ZoneCollision.BLOCK_EAST == 0)
                        ) return true
                        ORIENTATION_WEST -> if(
                            (y in moverY..actorMaxY && moverX == x - moverSize) ||
                            (x in moverX..actorMaxX && moverY == y - moverSize) ||
                            (x in moverX..actorMaxX && moverY == y + 1.tiles &&
                                map.getCollisionMask(floor, x, moverY) and ZoneCollision.BLOCK_SOUTH == 0) ||
                            (y in moverY..actorMaxY && moverX == x + 1.tiles &&
                                map.getCollisionMask(floor, moverX, y) and ZoneCollision.BLOCK_WEST == 0)
                        ) return true
                    }
                }
                9 -> if(
                    (x in moverX..actorMaxX && moverY == y + 1.tiles
                        && map.getCollisionMask(floor, x, moverY) and ZoneCollision.BLOCK_SOUTH == 0) ||
                    (y in moverY..actorMaxY && moverX == x + 1.tiles
                        && map.getCollisionMask(floor, moverX, y) and ZoneCollision.BLOCK_WEST == 0) ||
                    (x in moverX..actorMaxX && moverY == y - moverSize
                        && map.getCollisionMask(floor, x, actorMaxY) and ZoneCollision.BLOCK_NORTH == 0) ||
                    (y in moverY..actorMaxY && moverX == x - moverSize
                        && map.getCollisionMask(floor, actorMaxX, y) and ZoneCollision.BLOCK_EAST == 0)
                ) return true

            }
        }
        return false
    }

    private fun reachedDecoration(moverX: TileUnit, moverY: TileUnit, moverSize: TileUnit): Boolean {
        var orientation = loc.orientation
        if (moverSize == 1.tiles) {
            if (x == moverX && y == moverY) {
                return true
            }
            val mask = map.getCollisionMask(floor, moverX, moverY)
            when (loc.type) {
                6, 7 -> {
                    if (loc.type == 7) {
                        orientation = orientation + 2 and 0x3
                    }
                    when (orientation) {
                        ORIENTATION_NORTH -> if(
                            (y == moverY && moverX == x + 1.tiles && mask and ZoneCollision.MASK_WALL_WEST == 0) ||
                            (x == moverX && moverY == y - 1.tiles && mask and ZoneCollision.MASK_WALL_NORTH == 0)
                        ) return true
                        ORIENTATION_EAST -> if(
                            (y == moverY && moverX == x - 1.tiles && mask and ZoneCollision.MASK_WALL_EAST == 0) ||
                            (x == moverX && moverY == y - 1.tiles && mask and ZoneCollision.MASK_WALL_NORTH == 0)
                        ) return true
                        ORIENTATION_SOUTH -> if(
                            (y == moverY && moverX == x - 1.tiles && mask and ZoneCollision.MASK_WALL_EAST == 0) ||
                            (x == moverX && moverY == y + 1.tiles && mask and ZoneCollision.MASK_WALL_SOUTH == 0)
                        ) return true
                        ORIENTATION_WEST -> if(
                            (y == moverY && moverX == x + 1.tiles && mask and ZoneCollision.MASK_WALL_WEST == 0) ||
                            (x == moverX && moverY == y + 1.tiles && mask and ZoneCollision.MASK_WALL_SOUTH == 0)
                        ) return true
                    }
                }
                8 -> if(
                    (x == moverX && moverY == y + 1.tiles && mask and ZoneCollision.MASK_WALL_SOUTH == 0) ||
                    (x == moverX && moverY == y - 1.tiles && mask and ZoneCollision.MASK_WALL_NORTH == 0) ||
                    (y == moverY && moverX == x + 1.tiles && mask and ZoneCollision.MASK_WALL_WEST == 0) ||
                    (y == moverY && moverX == x - 1.tiles && mask and ZoneCollision.MASK_WALL_EAST == 0)
                ) return true
            }
        } else {
            val actorMaxX = moverX + moverSize - 1.tiles
            val actorMaxY = moverY + moverSize - 1.tiles
            if (x in moverX..actorMaxX && y in moverY..actorMaxY) {
                return true
            }
            when (loc.type) {
                6, 7 -> {
                    if (loc.type == 7) {
                        orientation = 0x3 and 2 + orientation
                    }
                    when (orientation) {
                        ORIENTATION_NORTH -> if(
                            (x in moverX..actorMaxX && moverY == y - moverSize &&
                                map.getCollisionMask(floor, x, actorMaxY) and ZoneCollision.MASK_WALL_NORTH == 0) ||
                            (y in moverY..actorMaxY && moverX == x + 1.tiles
                                && map.getCollisionMask(floor, moverX, y) and ZoneCollision.MASK_WALL_WEST == 0)
                        ) return true
                        ORIENTATION_EAST -> if(
                            (x in moverX..actorMaxX && moverY == y - moverSize &&
                                map.getCollisionMask(floor, x, actorMaxY) and ZoneCollision.MASK_WALL_NORTH == 0) ||
                            (y in moverY..actorMaxY && moverX == x - moverSize &&
                                map.getCollisionMask(floor, actorMaxX, y) and ZoneCollision.MASK_WALL_EAST == 0)
                        ) return true
                        ORIENTATION_SOUTH -> if(
                            (y in moverY..actorMaxY && moverX == x - moverSize &&
                                map.getCollisionMask(floor, actorMaxX, y) and ZoneCollision.MASK_WALL_EAST == 0) ||
                            (x in moverX..actorMaxX && moverY == y + 1.tiles &&
                                map.getCollisionMask(floor, x, moverY) and ZoneCollision.MASK_WALL_SOUTH == 0)
                        ) return true
                        ORIENTATION_WEST -> if(
                            (x in moverX..actorMaxX && moverY == y + 1.tiles &&
                                map.getCollisionMask(floor, x, moverY) and ZoneCollision.MASK_WALL_SOUTH == 0) ||
                            (y in moverY..actorMaxY && moverX == x + 1.tiles &&
                                map.getCollisionMask(floor, moverX, y) and ZoneCollision.MASK_WALL_WEST == 0)
                        ) return true
                    }
                }
                8 -> if(
                    (x in moverX..actorMaxX && moverY == y - moverSize &&
                        map.getCollisionMask(floor, x, actorMaxY) and ZoneCollision.MASK_WALL_NORTH == 0) ||
                    (y in moverY..actorMaxY && moverX == x - moverSize
                        && map.getCollisionMask(floor, actorMaxX, y) and ZoneCollision.MASK_WALL_EAST == 0) ||
                    (x in moverX..actorMaxX && moverY == y + 1.tiles
                        && map.getCollisionMask(floor, x, moverY) and ZoneCollision.MASK_WALL_SOUTH == 0) ||
                    (y in moverY..actorMaxY && moverX == x + 1.tiles
                        && map.getCollisionMask(floor, moverX, y) and ZoneCollision.MASK_WALL_WEST == 0)
                ) return true
            }
        }
        return false
    }

    private fun reachedObject(moverX: TileUnit, moverY: TileUnit, moverSize: TileUnit): Boolean {
        val srcEndX = moverX + moverSize
        val srcEndY = moverY + moverSize
        val destEndX = x + loc.sizeX
        val destEndY = y + loc.sizeY
        if (moverY == destEndY && loc.accessBlockFlags and MASK_ENTRANCE_NORTH == 0) {
            var maxX = if (moverX > x) moverX else x
            val maxXSize = if (srcEndX < destEndX) srcEndX else destEndX
            while (maxX < maxXSize) {
                if (map.getCollisionMask(floor, maxX, destEndY - 1.tiles) and ZoneCollision.MASK_WALL_NORTH == 0) {
                    return true
                }
                maxX++
            }
        } else if (destEndX == moverX && loc.accessBlockFlags and MASK_ENTRANCE_EAST == 0) {
            var maxY = if (moverY > y) moverY else y
            val maxYSize = if (srcEndY < destEndY) srcEndY else destEndY
            while (maxY < maxYSize) {
                if (map.getCollisionMask(floor, destEndX - 1.tiles, maxY) and ZoneCollision.MASK_WALL_EAST == 0) {
                    return true
                }
                maxY++
            }
        } else if (y == srcEndY && loc.accessBlockFlags and MASK_ENTRANCE_SOUTH == 0) {
            var maxX = if (moverX > x) moverX else x
            val maxXSize = if (srcEndX < destEndX) srcEndX else destEndX
            while (maxX < maxXSize) {
                if (map.getCollisionMask(floor, maxX, y) and ZoneCollision.MASK_WALL_SOUTH == 0) {
                    return true
                }
                maxX++
            }
        } else if (srcEndX == x && loc.accessBlockFlags and MASK_ENTRANCE_WEST == 0) {
            var maxY = if (moverY > y) moverY else y
            val maxYSize = if (srcEndY < destEndY) srcEndY else destEndY
            while (maxY < maxYSize) {
                if (map.getCollisionMask(floor, x, maxY) and ZoneCollision.MASK_WALL_WEST == 0) {
                    return true
                }
                maxY++
            }
        }
        return false
    }

    companion object {
        const val ORIENTATION_NORTH = 0
        const val ORIENTATION_EAST = 1
        const val ORIENTATION_SOUTH = 2
        const val ORIENTATION_WEST = 3
        const val MASK_ENTRANCE_NORTH = 0x1
        const val MASK_ENTRANCE_EAST = 0x2
        const val MASK_ENTRANCE_SOUTH = 0x4
        const val MASK_ENTRANCE_WEST = 0x8
    }
}