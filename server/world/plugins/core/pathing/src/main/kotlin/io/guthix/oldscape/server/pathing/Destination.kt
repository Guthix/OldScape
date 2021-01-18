/*
 * Copyright 2018-2021 Guthix
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

import io.guthix.oldscape.dim.*
import io.guthix.oldscape.server.world.Collision
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Character
import io.guthix.oldscape.server.world.entity.Loc
import io.guthix.oldscape.server.world.map.Tile

// TODO add caching the result?
abstract class Destination(val floor: FloorUnit, val x: TileUnit, val y: TileUnit) {
    abstract fun reached(moverX: TileUnit, moverY: TileUnit, moverSize: TileUnit): Boolean

    override fun toString(): String = "${Tile(floor, x, y)}"
}

class DestinationTile(floor: FloorUnit, x: TileUnit, y: TileUnit) : Destination(floor, x, y) {
    constructor(tile: Tile) : this(tile.floor, tile.x, tile.y)

    override fun reached(moverX: TileUnit, moverY: TileUnit, moverSize: TileUnit): Boolean = x == moverX && y == moverY
}

/**
 * Direct contact with target from either North/East/South/West (non diagonal)
 */
class DestinationRectangleDirect(
    floor: FloorUnit,
    x: TileUnit,
    y: TileUnit,
    private val sizeX: TileUnit,
    private val sizeY: TileUnit,
    private val world: World
) : Destination(floor, x, y) {
    constructor(char: Character, world: World) : this(
        char.pos.floor, char.pos.x, char.pos.y, char.sizeX, char.sizeY, world
    )

    override fun reached(moverX: TileUnit, moverY: TileUnit, moverSize: TileUnit): Boolean {
        val moverMaxX = moverX + moverSize
        val moverMaxY = moverY + moverSize
        for (curX in x until x + sizeX) {
            for (curY in y until y + sizeY) {
                val moverXRange = moverX until moverMaxX
                val moverYRange = moverY until moverMaxY
                if (curX == moverX - 1.tiles && curY in moverYRange // west side of target
                    && moverYRange.all { world.getCollision(floor, curX, it) and Collision.MASK_WALL_E == 0 }
                ) return true
                if (curX == moverMaxX && curY in moverYRange // east side of target
                    && moverYRange.all { world.getCollision(floor, curX, it) and Collision.MASK_WALL_W == 0 }
                ) return true
                if (curY == moverY - 1.tiles && curX in moverXRange // south side of target
                    && moverXRange.all { world.getCollision(floor, it, curY) and Collision.MASK_WALL_N == 0 }
                ) return true
                if (curY == moverMaxY && curX in moverXRange // north side of target
                    && moverXRange.all { world.getCollision(floor, it, curY) and Collision.MASK_WALL_S == 0 }
                ) return true
            }
        }
        return false
    }
}

class DestinationRange(
    private val char: Character,
    private val range: TileUnit,
    private val world: World
) : Destination(char.pos.floor, char.pos.x, char.pos.y) {
    override fun reached(moverX: TileUnit, moverY: TileUnit, moverSize: TileUnit): Boolean =
        max(abs(char.pos.x - moverX), abs(char.pos.y - moverY)) <= range && inLineOfSight(
            Tile(char.pos.floor, moverX, moverY),
            moverSize,
            moverSize,
            char.pos,
            char.sizeX,
            char.sizeY,
            world
        )
}

class DestinationLocation(
    private val loc: Loc,
    private val world: World
) : Destination(loc.pos.floor, loc.pos.x, loc.pos.y) {
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
                        ORIENTATION_NORTH -> if (
                            (y == moverY && moverX == x - 1.tiles) ||
                            (x == moverX && moverY == y + 1.tiles &&
                                world.getCollision(floor, moverX, moverY) and Collision.BLOCK_S == 0) ||
                            (x == moverX && moverY == y - 1.tiles &&
                                world.getCollision(floor, moverX, moverY) and Collision.BLOCK_N == 0)
                        ) return true
                        ORIENTATION_EAST -> if (
                            (x == moverX && moverY == y + 1.tiles) ||
                            (y == moverY && moverX == x - 1.tiles &&
                                world.getCollision(floor, moverX, moverY) and Collision.BLOCK_E == 0) ||
                            (y == moverY && moverX == x + 1.tiles &&
                                world.getCollision(floor, moverX, moverY) and Collision.BLOCK_W == 0)
                        ) return true
                        ORIENTATION_SOUTH -> if (
                            (y == moverY && moverX == x + 1.tiles) ||
                            (y == moverY && moverX == x + 1.tiles &&
                                world.getCollision(floor, moverX, moverY) and Collision.BLOCK_S == 0) ||
                            (x == moverX && moverY == y - 1.tiles &&
                                world.getCollision(floor, moverX, moverY) and Collision.BLOCK_N == 0)
                        ) return true
                        ORIENTATION_WEST -> if (
                            (x == moverX && moverY == y - 1.tiles) ||
                            (y == moverY && moverX == x - 1.tiles &&
                                world.getCollision(floor, moverX, moverY) and Collision.BLOCK_E == 0) ||
                            (y == moverY && moverX == x + 1.tiles &&
                                world.getCollision(floor, moverX, moverY) and Collision.BLOCK_W == 0)
                        ) return true
                    }
                }
                2 -> {
                    when (loc.orientation) {
                        ORIENTATION_NORTH -> if (
                            (y == moverY && moverX == x - 1.tiles) ||
                            (x == moverX && moverY == y + 1.tiles) ||
                            (y == moverY && moverX == x + 1.tiles &&
                                world.getCollision(floor, moverX, moverY) and Collision.BLOCK_W == 0) ||
                            (x == moverX && moverY == y - 1.tiles &&
                                world.getCollision(floor, moverX, moverY) and Collision.BLOCK_N == 0)
                        ) return true
                        ORIENTATION_EAST -> if (
                            (x == moverX && moverY == y + 1.tiles) ||
                            (y == moverY && moverX == x + 1.tiles) ||
                            (y == moverY && moverX == x - 1.tiles &&
                                world.getCollision(floor, moverX, moverY) and Collision.BLOCK_E == 0) ||
                            (x == moverX && moverY == y - 1.tiles &&
                                world.getCollision(floor, moverX, moverY) and Collision.BLOCK_N == 0)
                        ) return true
                        ORIENTATION_SOUTH -> if (
                            (y == moverY && moverX == x + 1.tiles) ||
                            (x == moverX && moverY == y - 1.tiles) ||
                            (x == moverX && moverY == y + 1.tiles &&
                                world.getCollision(floor, moverX, moverY) and Collision.BLOCK_S == 0) ||
                            (y == moverY && moverX == x - 1.tiles &&
                                world.getCollision(floor, moverX, moverY) and Collision.BLOCK_E == 0)
                        ) return true
                        ORIENTATION_WEST -> if (
                            (y == moverY && moverX == x - 1.tiles) ||
                            (x == moverX && moverY == y - 1.tiles) ||
                            (x == moverX && moverY == y + 1.tiles &&
                                world.getCollision(floor, moverX, moverY) and Collision.BLOCK_S == 0) ||
                            (y == moverY && moverX == x + 1.tiles &&
                                world.getCollision(floor, moverX, moverY) and Collision.BLOCK_W == 0)
                        ) return true
                    }
                }
                9 -> if (
                    (x == moverX && moverY == y + 1.tiles &&
                        world.getCollision(floor, moverX, moverY) and Collision.MASK_WALL_S == 0) ||
                    (x == moverX && moverY == y - 1.tiles &&
                        world.getCollision(floor, moverX, moverY) and Collision.MASK_WALL_N == 0) ||
                    (y == moverY && moverX == x - 1.tiles &&
                        world.getCollision(floor, moverX, moverY) and Collision.MASK_WALL_E == 0) ||
                    (y == moverY && moverX == x + 1.tiles &&
                        world.getCollision(floor, moverX, moverY) and Collision.MASK_WALL_W == 0)
                ) return true
            }
        } else {
            val moverMaxX = moverX + moverSize - 1.tiles
            val moverMaxY = moverY + moverSize - 1.tiles
            if (x in moverX..moverMaxX && y in moverY..moverMaxY) {
                return true
            }
            when (loc.type) {
                0 -> {
                    when (loc.orientation) {
                        ORIENTATION_NORTH -> if (
                            (y in moverY..moverMaxY && moverY == x - moverSize) ||
                            (x in moverX..moverMaxX && moverY == y + 1.tiles &&
                                world.getCollision(floor, x, moverY) and Collision.BLOCK_S == 0) ||
                            (x in moverX..moverMaxX && moverY == y - moverSize &&
                                world.getCollision(floor, x, moverMaxY) and Collision.BLOCK_N == 0)
                        ) return true
                        ORIENTATION_EAST -> if (
                            (x in moverX..moverMaxX && moverY == y + 1.tiles) ||
                            (y in moverY..moverMaxY && moverX == x + 1.tiles &&
                                world.getCollision(floor, moverX, y) and Collision.BLOCK_W == 0) ||
                            (y in moverY..moverMaxY && moverX == x - moverSize &&
                                world.getCollision(floor, moverMaxX, y) and Collision.BLOCK_E == 0)
                        ) return true
                        ORIENTATION_SOUTH -> if (
                            (y in moverY..moverMaxY && moverX == x + 1.tiles) ||
                            (x in moverX..moverMaxX && moverY == y + 1.tiles &&
                                world.getCollision(floor, x, moverY) and Collision.BLOCK_S == 0) ||
                            (x in moverX..moverMaxX && moverY == y - moverSize &&
                                world.getCollision(floor, x, moverMaxY) and Collision.BLOCK_N == 0)
                        ) return true
                        ORIENTATION_WEST -> if (
                            (x in moverX..moverMaxX && moverY == y - moverSize) ||
                            (y in moverY..moverMaxY && moverX == x + 1.tiles &&
                                world.getCollision(floor, moverX, y) and Collision.BLOCK_W == 0) ||
                            (y in moverY..moverMaxY && moverX == x - moverSize &&
                                world.getCollision(floor, moverMaxX, y) and Collision.BLOCK_E == 0)
                        ) return true
                    }
                }
                2 -> {
                    when (loc.orientation) {
                        ORIENTATION_NORTH -> if (
                            (y in moverY..moverMaxY && moverX == x - moverSize) ||
                            (x in moverX..moverMaxX && moverY == y + 1.tiles) ||
                            (y in moverY..moverMaxY && moverX == x + 1.tiles &&
                                world.getCollision(floor, moverX, y) and Collision.BLOCK_W == 0) ||
                            (x in moverX..moverMaxX && moverY == y - moverSize &&
                                world.getCollision(floor, x, moverMaxY) and Collision.BLOCK_N == 0)
                        ) return true
                        ORIENTATION_EAST -> if (
                            (x in moverX..moverMaxX && moverY == y + 1.tiles) ||
                            (y in moverY..moverMaxY && moverX == x + 1.tiles) ||
                            (y in moverY..moverMaxY && moverX == x - moverSize &&
                                world.getCollision(floor, moverMaxX, y) and Collision.BLOCK_E == 0) ||
                            (x in moverX..moverMaxX && moverY == y - moverSize
                                && world.getCollision(floor, x, moverMaxY) and Collision.BLOCK_N == 0)
                        ) return true
                        ORIENTATION_SOUTH -> if (
                            (y in moverY..moverMaxY && moverX == x + 1.tiles) ||
                            (x in moverX..moverMaxX && moverY == y - moverSize) ||
                            (x in moverX..moverMaxX && moverY == y + 1.tiles &&
                                world.getCollision(floor, x, moverY) and Collision.BLOCK_S == 0) ||
                            (y in moverY..moverMaxY && moverX == x - moverSize &&
                                world.getCollision(floor, moverMaxX, y) and Collision.BLOCK_E == 0)
                        ) return true
                        ORIENTATION_WEST -> if (
                            (y in moverY..moverMaxY && moverX == x - moverSize) ||
                            (x in moverX..moverMaxX && moverY == y - moverSize) ||
                            (x in moverX..moverMaxX && moverY == y + 1.tiles &&
                                world.getCollision(floor, x, moverY) and Collision.BLOCK_S == 0) ||
                            (y in moverY..moverMaxY && moverX == x + 1.tiles &&
                                world.getCollision(floor, moverX, y) and Collision.BLOCK_W == 0)
                        ) return true
                    }
                }
                9 -> if (
                    (x in moverX..moverMaxX && moverY == y + 1.tiles
                        && world.getCollision(floor, x, moverY) and Collision.BLOCK_S == 0) ||
                    (y in moverY..moverMaxY && moverX == x + 1.tiles
                        && world.getCollision(floor, moverX, y) and Collision.BLOCK_W == 0) ||
                    (x in moverX..moverMaxX && moverY == y - moverSize
                        && world.getCollision(floor, x, moverMaxY) and Collision.BLOCK_N == 0) ||
                    (y in moverY..moverMaxY && moverX == x - moverSize
                        && world.getCollision(floor, moverMaxX, y) and Collision.BLOCK_E == 0)
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
            val mask = world.getCollision(floor, moverX, moverY)
            when (loc.type) {
                6, 7 -> {
                    if (loc.type == 7) {
                        orientation = orientation + 2 and 0x3
                    }
                    when (orientation) {
                        ORIENTATION_NORTH -> if (
                            (y == moverY && moverX == x + 1.tiles && mask and Collision.MASK_WALL_W == 0) ||
                            (x == moverX && moverY == y - 1.tiles && mask and Collision.MASK_WALL_N == 0)
                        ) return true
                        ORIENTATION_EAST -> if (
                            (y == moverY && moverX == x - 1.tiles && mask and Collision.MASK_WALL_E == 0) ||
                            (x == moverX && moverY == y - 1.tiles && mask and Collision.MASK_WALL_N == 0)
                        ) return true
                        ORIENTATION_SOUTH -> if (
                            (y == moverY && moverX == x - 1.tiles && mask and Collision.MASK_WALL_E == 0) ||
                            (x == moverX && moverY == y + 1.tiles && mask and Collision.MASK_WALL_S == 0)
                        ) return true
                        ORIENTATION_WEST -> if (
                            (y == moverY && moverX == x + 1.tiles && mask and Collision.MASK_WALL_W == 0) ||
                            (x == moverX && moverY == y + 1.tiles && mask and Collision.MASK_WALL_S == 0)
                        ) return true
                    }
                }
                8 -> if (
                    (x == moverX && moverY == y + 1.tiles && mask and Collision.MASK_WALL_S == 0) ||
                    (x == moverX && moverY == y - 1.tiles && mask and Collision.MASK_WALL_N == 0) ||
                    (y == moverY && moverX == x + 1.tiles && mask and Collision.MASK_WALL_W == 0) ||
                    (y == moverY && moverX == x - 1.tiles && mask and Collision.MASK_WALL_E == 0)
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
                        ORIENTATION_NORTH -> if (
                            (x in moverX..actorMaxX && moverY == y - moverSize &&
                                world.getCollision(floor, x, actorMaxY) and Collision.MASK_WALL_N == 0) ||
                            (y in moverY..actorMaxY && moverX == x + 1.tiles
                                && world.getCollision(floor, moverX, y) and Collision.MASK_WALL_W == 0)
                        ) return true
                        ORIENTATION_EAST -> if (
                            (x in moverX..actorMaxX && moverY == y - moverSize &&
                                world.getCollision(floor, x, actorMaxY) and Collision.MASK_WALL_N == 0) ||
                            (y in moverY..actorMaxY && moverX == x - moverSize &&
                                world.getCollision(floor, actorMaxX, y) and Collision.MASK_WALL_E == 0)
                        ) return true
                        ORIENTATION_SOUTH -> if (
                            (y in moverY..actorMaxY && moverX == x - moverSize &&
                                world.getCollision(floor, actorMaxX, y) and Collision.MASK_WALL_E == 0) ||
                            (x in moverX..actorMaxX && moverY == y + 1.tiles &&
                                world.getCollision(floor, x, moverY) and Collision.MASK_WALL_S == 0)
                        ) return true
                        ORIENTATION_WEST -> if (
                            (x in moverX..actorMaxX && moverY == y + 1.tiles &&
                                world.getCollision(floor, x, moverY) and Collision.MASK_WALL_S == 0) ||
                            (y in moverY..actorMaxY && moverX == x + 1.tiles &&
                                world.getCollision(floor, moverX, y) and Collision.MASK_WALL_W == 0)
                        ) return true
                    }
                }
                8 -> if (
                    (x in moverX..actorMaxX && moverY == y - moverSize &&
                        world.getCollision(floor, x, actorMaxY) and Collision.MASK_WALL_N == 0) ||
                    (y in moverY..actorMaxY && moverX == x - moverSize
                        && world.getCollision(floor, actorMaxX, y) and Collision.MASK_WALL_E == 0) ||
                    (x in moverX..actorMaxX && moverY == y + 1.tiles
                        && world.getCollision(floor, x, moverY) and Collision.MASK_WALL_S == 0) ||
                    (y in moverY..actorMaxY && moverX == x + 1.tiles
                        && world.getCollision(floor, moverX, y) and Collision.MASK_WALL_W == 0)
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
                if (world.getCollision(floor, maxX, destEndY - 1.tiles) and Collision.MASK_WALL_N == 0) {
                    return true
                }
                maxX++
            }
        } else if (destEndX == moverX && loc.accessBlockFlags and MASK_ENTRANCE_EAST == 0) {
            var maxY = if (moverY > y) moverY else y
            val maxYSize = if (srcEndY < destEndY) srcEndY else destEndY
            while (maxY < maxYSize) {
                if (world.getCollision(floor, destEndX - 1.tiles, maxY) and Collision.MASK_WALL_E == 0) {
                    return true
                }
                maxY++
            }
        } else if (y == srcEndY && loc.accessBlockFlags and MASK_ENTRANCE_SOUTH == 0) {
            var maxX = if (moverX > x) moverX else x
            val maxXSize = if (srcEndX < destEndX) srcEndX else destEndX
            while (maxX < maxXSize) {
                if (world.getCollision(floor, maxX, y) and Collision.MASK_WALL_S == 0) {
                    return true
                }
                maxX++
            }
        } else if (srcEndX == x && loc.accessBlockFlags and MASK_ENTRANCE_WEST == 0) {
            var maxY = if (moverY > y) moverY else y
            val maxYSize = if (srcEndY < destEndY) srcEndY else destEndY
            while (maxY < maxYSize) {
                if (world.getCollision(floor, x, maxY) and Collision.MASK_WALL_W == 0) {
                    return true
                }
                maxY++
            }
        }
        return false
    }

    companion object {
        const val ORIENTATION_NORTH: Int = 0
        const val ORIENTATION_EAST: Int = 1
        const val ORIENTATION_SOUTH: Int = 2
        const val ORIENTATION_WEST: Int = 3
        const val MASK_ENTRANCE_NORTH: Int = 0x1
        const val MASK_ENTRANCE_EAST: Int = 0x2
        const val MASK_ENTRANCE_SOUTH: Int = 0x4
        const val MASK_ENTRANCE_WEST: Int = 0x8
    }
}