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
package io.guthix.oldscape.server.world.mapsquare.zone

import io.guthix.oldscape.server.world.entity.Location
import io.guthix.oldscape.server.world.mapsquare.zone.tile.TileUnit
import io.guthix.oldscape.server.world.mapsquare.zone.tile.tiles
import io.guthix.oldscape.server.world.mapsquare.zone.tile.until

class ZoneCollision(val zone: Zone) {
    private val masks = Array(ZoneUnit.SIZE_TILE.value) {
        IntArray(ZoneUnit.SIZE_TILE.value)
    }

    private fun addMask(localX: TileUnit, localY: TileUnit, mask: Int) {
        if (localX >= ZoneUnit.SIZE_TILE || localY >= ZoneUnit.SIZE_TILE || localX < 0.tiles || localY < 0.tiles) {
            val zone = zone.mapsquareFloor.mapsquare.world.getZone(
                zone.floor, zone.x.inTiles + localX, zone.y.inTiles + localY
            )
            zone?.collisions?.addMask(localX.relativeZone, localY.relativeZone, mask)
        } else {
            masks[localX.value][localY.value] = masks[localX.value][localY.value] or mask
        }
    }

    fun addLocation(loc: Location) {
        when (loc.type) {
            in 0..3 -> {
                if (loc.blueprint.clipType != 0) {
                    addWall(
                        loc.position.x.relativeZone, loc.position.y.relativeZone, loc.type, loc.orientation,
                        loc.blueprint.impenetrable
                    )
                }
            }
            in 9..21 -> {
                if (loc.blueprint.clipType != 0) {
                    var sizeX = loc.blueprint.width
                    var sizeY = loc.blueprint.length
                    if (loc.orientation == 1 || loc.orientation == 3) {
                        sizeX = loc.blueprint.length
                        sizeY = loc.blueprint.width
                    }
                    addObject(loc.position.x.relativeZone, loc.position.y.relativeZone, sizeX, sizeY,
                        loc.blueprint.impenetrable
                    )
                }
            }
            22 -> {
                if (loc.blueprint.clipType == 1) {
                    addDecoration(loc.position.x.relativeZone, loc.position.y.relativeZone)
                }
            }
        }
    }

    fun addObject(x: TileUnit, y: TileUnit, sizeX: TileUnit, sizeY: TileUnit, impenetrable: Boolean) {
        var mask = MASK_LOC
        if (impenetrable) {
            mask = mask or MASK_LOC_IMPENETRABLE
        }
        for(i in 0 until 3)
        for (tileX in x until (x + sizeX)) {
            for (tileY in y until y + sizeY) {
                addMask(tileX, tileY, mask)
            }
        }
    }

    fun addDecoration(x: TileUnit, y: TileUnit) {
        addMask(x, y, MASK_DECORATION)
    }

    fun addUnwalkableTile(x: TileUnit, y: TileUnit) {
        addMask(x, y, MASK_UNWALKABLE_TILE)
    }

    fun addWall(x: TileUnit, y: TileUnit, type: Int, orientation: Int, impenetrable: Boolean) {
        when (type) {
            0 -> {
                when (orientation) {
                    0 -> {
                        addMask(x, y, MASK_WALL_WEST)
                        addMask(x - 1.tiles, y, MASK_WALL_EAST)
                    }
                    1 -> {
                        addMask(x, y, MASK_WALL_NORTH)
                        addMask(x, y + 1.tiles, MASK_WALL_SOUTH)
                    }
                    2 -> {
                        addMask(x, y, MASK_WALL_EAST)
                        addMask(x + 1.tiles, y, MASK_WALL_WEST)
                    }
                    3 -> {
                        addMask(x, y, MASK_WALL_SOUTH)
                        addMask(x, y - 1.tiles, MASK_WALL_NORTH)
                    }
                }
            }
            1, 3 -> {
                when (orientation) {
                    0 -> {
                        addMask(x, y, MASK_WALL_NORTHWEST)
                        addMask(x - 1.tiles, y + 1.tiles, MASK_WALL_SOUTHEAST)
                    }
                    1 -> {
                        addMask(x, y, MASK_WALL_NORTHEAST)
                        addMask(x + 1.tiles, y + 1.tiles, MASK_WALL_SOUTHWEST)
                    }
                    2 -> {
                        addMask(x, y, MASK_WALL_SOUTHEAST)
                        addMask(x + 1.tiles, y - 1.tiles, MASK_WALL_NORTHWEST)
                    }
                    3 -> {
                        addMask(x, y, MASK_WALL_SOUTHWEST)
                        addMask(x - 1.tiles, y - 1.tiles, MASK_WALL_NORTHEAST)
                    }
                }
            }
            2 -> {
                when (orientation) {
                    0 -> {
                        addMask(x, y, MASK_WALL_WEST or MASK_WALL_NORTH)
                        addMask(x - 1.tiles, y, MASK_WALL_EAST)
                        addMask(x, y + 1.tiles, MASK_WALL_SOUTH)
                    }
                    1 -> {
                        addMask(x, y, MASK_WALL_NORTH or MASK_WALL_EAST)
                        addMask(x, y + 1.tiles, MASK_WALL_SOUTH)
                        addMask(x + 1.tiles, y, MASK_WALL_WEST)
                    }
                    2 -> {
                        addMask(x, y, MASK_WALL_EAST or MASK_WALL_SOUTH)
                        addMask(x + 1.tiles, y, MASK_WALL_WEST)
                        addMask(x, y - 1.tiles, MASK_WALL_NORTH)
                    }
                    3 -> {
                        addMask(x, y, MASK_WALL_SOUTH or MASK_WALL_WEST)
                        addMask(x, y - 1.tiles, MASK_WALL_NORTH)
                        addMask(x - 1.tiles, y, MASK_WALL_EAST)
                    }
                }
            }
        }
        if (impenetrable) {
            when (type) {
                0 -> {
                    when (orientation) {
                        0 -> {
                            addMask(x, y, MASK_WALL_WEST_IMPENETRABLE)
                            addMask(x - 1.tiles, y, MASK_WALL_EAST_IMPENETRABLE)
                        }
                        1 -> {
                            addMask(x, y, MASK_WALL_NORTH_IMPENETRABLE)
                            addMask(x, y + 1.tiles, MASK_WALL_SOUTH_IMPENETRABLE)
                        }
                        2 -> {
                            addMask(x, y, MASK_WALL_EAST_IMPENETRABLE)
                            addMask(x + 1.tiles, y, MASK_WALL_WEST_IMPENETRABLE)
                        }
                        3 -> {
                            addMask(x, y, MASK_WALL_SOUTH_IMPENETRABLE)
                            addMask(x, y - 1.tiles, MASK_WALL_NORTH_IMPENETRABLE)
                        }
                    }

                }
                1, 3 -> {
                    when (orientation) {
                        0 -> {
                            addMask(x, y, MASK_WALL_NORTHWEST_IMPENETRABLE)
                            addMask(x - 1.tiles, y + 1.tiles, MASK_WALL_SOUTHEAST_IMPENETRABLE)
                        }
                        1 -> {
                            addMask(x, y, MASK_WALL_NORTHEAST_IMPENETRABLE)
                            addMask(x + 1.tiles, y + 1.tiles, MASK_WALL_SOUTHWEST_IMPENETRABLE)
                        }
                        2 -> {
                            addMask(x, y, MASK_WALL_SOUTHEAST_IMPENETRABLE)
                            addMask(x + 1.tiles, y - 1.tiles, MASK_WALL_NORTHWEST_IMPENETRABLE)
                        }
                        3 -> {
                            addMask(x, y, MASK_WALL_SOUTHWEST_IMPENETRABLE)
                            addMask(x - 1.tiles, y - 1.tiles, MASK_WALL_NORTHEAST_IMPENETRABLE)
                        }
                    }
                }
                2 -> {
                    when (orientation) {
                        0 -> {
                            addMask(x, y, MASK_WALL_WEST_IMPENETRABLE or MASK_WALL_NORTH_IMPENETRABLE)
                            addMask(x - 1.tiles, y, MASK_WALL_EAST_IMPENETRABLE)
                            addMask(x, y + 1.tiles, MASK_WALL_SOUTH_IMPENETRABLE)
                        }
                        1 -> {
                            addMask(x, y, MASK_WALL_NORTH_IMPENETRABLE or MASK_WALL_EAST_IMPENETRABLE)
                            addMask(x, y + 1.tiles, MASK_WALL_SOUTH_IMPENETRABLE)
                            addMask(x + 1.tiles, y, MASK_WALL_WEST_IMPENETRABLE)
                        }
                        2 -> {
                            addMask(x, y, MASK_WALL_EAST_IMPENETRABLE or MASK_WALL_SOUTH_IMPENETRABLE)
                            addMask(x + 1.tiles, y, MASK_WALL_WEST_IMPENETRABLE)
                            addMask(x, y - 1.tiles, MASK_WALL_NORTH_IMPENETRABLE)
                        }
                        3 -> {
                            addMask(x, y, MASK_WALL_SOUTH_IMPENETRABLE or MASK_WALL_WEST_IMPENETRABLE)
                            addMask(x, y - 1.tiles, MASK_WALL_NORTH_IMPENETRABLE)
                            addMask(x - 1.tiles, y, MASK_WALL_EAST_IMPENETRABLE)
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val MASK_WALL_NORTHWEST = 0x1
        const val MASK_WALL_NORTH = 0x2
        const val MASK_WALL_NORTHEAST = 0x4
        const val MASK_WALL_EAST = 0x8
        const val MASK_WALL_SOUTHEAST = 0x10
        const val MASK_WALL_SOUTH = 0x20
        const val MASK_WALL_SOUTHWEST = 0x40
        const val MASK_WALL_WEST = 0x80
        const val MASK_LOC = 0x100
        const val MASK_WALL_NORTHWEST_IMPENETRABLE = 0x200
        const val MASK_WALL_NORTH_IMPENETRABLE = 0x400
        const val MASK_WALL_NORTHEAST_IMPENETRABLE = 0x800
        const val MASK_WALL_EAST_IMPENETRABLE = 0x1000
        const val MASK_WALL_SOUTHEAST_IMPENETRABLE = 0x2000
        const val MASK_WALL_SOUTH_IMPENETRABLE = 0x4000
        const val MASK_WALL_SOUTHWEST_IMPENETRABLE = 0x8000
        const val MASK_WALL_WEST_IMPENETRABLE = 0x10000
        const val MASK_LOC_IMPENETRABLE = 0x20000
        const val MASK_DECORATION = 0x40000
        const val MASK_UNWALKABLE_TILE = 0x200000
        const val BLOCK_TILE = MASK_LOC or MASK_UNWALKABLE_TILE or MASK_DECORATION
        const val BLOCK_NORTH_WEST = MASK_WALL_NORTH or MASK_WALL_NORTHWEST or MASK_WALL_WEST or BLOCK_TILE
        const val BLOCK_NORTH = MASK_WALL_NORTH or BLOCK_TILE
        const val BLOCK_NORTH_EAST = MASK_WALL_NORTH or MASK_WALL_NORTHEAST or MASK_WALL_EAST or BLOCK_TILE
        const val BLOCK_EAST = MASK_WALL_EAST or BLOCK_TILE
        const val BLOCK_SOUTH_EAST = MASK_WALL_SOUTH or MASK_WALL_SOUTHEAST or MASK_WALL_EAST or BLOCK_TILE
        const val BLOCK_SOUTH = MASK_WALL_SOUTH or BLOCK_TILE
        const val BLOCK_SOUTH_WEST = MASK_WALL_SOUTH or MASK_WALL_SOUTHWEST or MASK_WALL_WEST or BLOCK_TILE
        const val BLOCK_WEST = MASK_WALL_WEST or BLOCK_TILE
    }
}