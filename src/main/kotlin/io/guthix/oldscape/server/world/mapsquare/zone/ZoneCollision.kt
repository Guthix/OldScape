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
package io.guthix.oldscape.server.world.mapsquare.zone

import io.guthix.oldscape.server.dimensions.*
import io.guthix.oldscape.server.world.entity.Loc

class ZoneCollision(val zone: Zone) {
    val masks = Array(ZoneUnit.SIZE_TILE.value) {
        IntArray(ZoneUnit.SIZE_TILE.value)
    }

    private fun addMask(localX: TileUnit, localY: TileUnit, mask: Int) {
        if(zone.x.inTiles + localX == 3226.tiles && zone.y.inTiles + localY == 3218.tiles && zone.floor == 0.floors) {
            throw Exception("Exception!")
        }
        if (localX >= ZoneUnit.SIZE_TILE || localY >= ZoneUnit.SIZE_TILE || localX < 0.tiles || localY < 0.tiles) {
            val zone = zone.mapsquareFloor.mapsquare.world.getZone(
                zone.floor, zone.x.inTiles + localX, zone.y.inTiles + localY
            )
            zone?.collisions?.addMask(localX.relativeZone, localY.relativeZone, mask)
        } else {
            masks[localX.value][localY.value] = masks[localX.value][localY.value] or mask
        }
    }

    fun addLocation(loc: Loc) {
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
            mask = mask or MASK_LOC_HIGH
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
        addMask(x, y, MASK_TERRAIN_BLOCK)
    }

    fun addWall(x: TileUnit, y: TileUnit, type: Int, orientation: Int, impenetrable: Boolean) {
        when (type) {
            0 -> {
                when (orientation) {
                    0 -> {
                        addMask(x, y, MASK_WALL_W)
                        addMask(x - 1.tiles, y, MASK_WALL_E)
                    }
                    1 -> {
                        addMask(x, y, MASK_WALL_N)
                        addMask(x, y + 1.tiles, MASK_WALL_S)
                    }
                    2 -> {
                        addMask(x, y, MASK_WALL_E)
                        addMask(x + 1.tiles, y, MASK_WALL_W)
                    }
                    3 -> {
                        addMask(x, y, MASK_WALL_S)
                        addMask(x, y - 1.tiles, MASK_WALL_N)
                    }
                }
            }
            1, 3 -> {
                when (orientation) {
                    0 -> {
                        addMask(x, y, MASK_PILLAR_NW)
                        addMask(x - 1.tiles, y + 1.tiles, MASK_PILLAR_SE)
                    }
                    1 -> {
                        addMask(x, y, MASK_PILLAR_NE)
                        addMask(x + 1.tiles, y + 1.tiles, MASK_PILLAR_SW)
                    }
                    2 -> {
                        addMask(x, y, MASK_PILLAR_SE)
                        addMask(x + 1.tiles, y - 1.tiles, MASK_PILLAR_NW)
                    }
                    3 -> {
                        addMask(x, y, MASK_PILLAR_SW)
                        addMask(x - 1.tiles, y - 1.tiles, MASK_PILLAR_NE)
                    }
                }
            }
            2 -> {
                when (orientation) {
                    0 -> {
                        addMask(x, y, MASK_WALL_W or MASK_WALL_N)
                        addMask(x - 1.tiles, y, MASK_WALL_E)
                        addMask(x, y + 1.tiles, MASK_WALL_S)
                    }
                    1 -> {
                        addMask(x, y, MASK_WALL_N or MASK_WALL_E)
                        addMask(x, y + 1.tiles, MASK_WALL_S)
                        addMask(x + 1.tiles, y, MASK_WALL_W)
                    }
                    2 -> {
                        addMask(x, y, MASK_WALL_E or MASK_WALL_S)
                        addMask(x + 1.tiles, y, MASK_WALL_W)
                        addMask(x, y - 1.tiles, MASK_WALL_N)
                    }
                    3 -> {
                        addMask(x, y, MASK_WALL_S or MASK_WALL_W)
                        addMask(x, y - 1.tiles, MASK_WALL_N)
                        addMask(x - 1.tiles, y, MASK_WALL_E)
                    }
                }
            }
        }
        if (impenetrable) {
            when (type) {
                0 -> {
                    when (orientation) {
                        0 -> {
                            addMask(x, y, MASK_WALL_HIGH_W)
                            addMask(x - 1.tiles, y, MASK_WALL_HIGH_E)
                        }
                        1 -> {
                            addMask(x, y, MASK_WALL_HIGH_N)
                            addMask(x, y + 1.tiles, MASK_WALL_HIGH_S)
                        }
                        2 -> {
                            addMask(x, y, MASK_WALL_HIGH_E)
                            addMask(x + 1.tiles, y, MASK_WALL_HIGH_W)
                        }
                        3 -> {
                            addMask(x, y, MASK_WALL_HIGH_S)
                            addMask(x, y - 1.tiles, MASK_WALL_HIGH_N)
                        }
                    }

                }
                1, 3 -> {
                    when (orientation) {
                        0 -> {
                            addMask(x, y, MASK_PILLAR_HIGH_NW)
                            addMask(x - 1.tiles, y + 1.tiles, MASK_PILLAR_HIGH_SE)
                        }
                        1 -> {
                            addMask(x, y, MASK_PILLAR_HIGH_NE)
                            addMask(x + 1.tiles, y + 1.tiles, MASK_PILLAR_HIGH_SW)
                        }
                        2 -> {
                            addMask(x, y, MASK_PILLAR_HIGH_SE)
                            addMask(x + 1.tiles, y - 1.tiles, MASK_PILLAR_HIGH_NW)
                        }
                        3 -> {
                            addMask(x, y, MASK_PILLAR_HIGH_SW)
                            addMask(x - 1.tiles, y - 1.tiles, MASK_PILLAR_HIGH_NE)
                        }
                    }
                }
                2 -> {
                    when (orientation) {
                        0 -> {
                            addMask(x, y, MASK_WALL_HIGH_W or MASK_WALL_HIGH_N)
                            addMask(x - 1.tiles, y, MASK_WALL_HIGH_E)
                            addMask(x, y + 1.tiles, MASK_WALL_HIGH_S)
                        }
                        1 -> {
                            addMask(x, y, MASK_WALL_HIGH_N or MASK_WALL_HIGH_E)
                            addMask(x, y + 1.tiles, MASK_WALL_HIGH_S)
                            addMask(x + 1.tiles, y, MASK_WALL_HIGH_W)
                        }
                        2 -> {
                            addMask(x, y, MASK_WALL_HIGH_E or MASK_WALL_HIGH_S)
                            addMask(x + 1.tiles, y, MASK_WALL_HIGH_W)
                            addMask(x, y - 1.tiles, MASK_WALL_HIGH_N)
                        }
                        3 -> {
                            addMask(x, y, MASK_WALL_HIGH_S or MASK_WALL_HIGH_W)
                            addMask(x, y - 1.tiles, MASK_WALL_HIGH_N)
                            addMask(x - 1.tiles, y, MASK_WALL_HIGH_E)
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val MASK_PILLAR_NW = 0x1
        const val MASK_WALL_N = 0x2
        const val MASK_PILLAR_NE = 0x4
        const val MASK_WALL_E = 0x8
        const val MASK_PILLAR_SE = 0x10
        const val MASK_WALL_S = 0x20
        const val MASK_PILLAR_SW = 0x40
        const val MASK_WALL_W = 0x80
        const val MASK_LOC = 0x100
        const val MASK_PILLAR_HIGH_NW = 0x200
        const val MASK_WALL_HIGH_N = 0x400
        const val MASK_PILLAR_HIGH_NE = 0x800
        const val MASK_WALL_HIGH_E = 0x1000
        const val MASK_PILLAR_HIGH_SE = 0x2000
        const val MASK_WALL_HIGH_S = 0x4000
        const val MASK_PILLAR_HIGH_SW = 0x8000
        const val MASK_WALL_HIGH_W = 0x10000
        const val MASK_LOC_HIGH = 0x20000
        const val MASK_DECORATION = 0x40000
        const val MASK_TERRAIN_BLOCK = 0x200000
        const val BLOCK_TILE = MASK_LOC or MASK_TERRAIN_BLOCK or MASK_DECORATION
        const val BLOCK_NW = MASK_WALL_N or MASK_PILLAR_NW or MASK_WALL_W or BLOCK_TILE
        const val BLOCK_N = MASK_WALL_N or BLOCK_TILE
        const val BLOCK_NE = MASK_WALL_N or MASK_PILLAR_NE or MASK_WALL_E or BLOCK_TILE
        const val BLOCK_E = MASK_WALL_E or BLOCK_TILE
        const val BLOCK_SE = MASK_WALL_S or MASK_PILLAR_SE or MASK_WALL_E or BLOCK_TILE
        const val BLOCK_S = MASK_WALL_S or BLOCK_TILE
        const val BLOCK_SW = MASK_WALL_S or MASK_PILLAR_SW or MASK_WALL_W or BLOCK_TILE
        const val BLOCK_W = MASK_WALL_W or BLOCK_TILE
        const val BLOCK_HIGH_N = MASK_WALL_HIGH_N or MASK_LOC_HIGH
        const val BLOCK_HIGH_E = MASK_WALL_HIGH_E or MASK_LOC_HIGH
        const val BLOCK_HIGH_S = MASK_WALL_HIGH_S or MASK_LOC_HIGH
        const val BLOCK_HIGH_W = MASK_WALL_HIGH_W or MASK_LOC_HIGH
    }
}