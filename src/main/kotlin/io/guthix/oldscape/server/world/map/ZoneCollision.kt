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
package io.guthix.oldscape.server.world.map

import io.guthix.oldscape.server.world.entity.Loc
import io.guthix.oldscape.server.world.map.dim.*

class ZoneCollision(val zone: Zone) {
    val masks: Array<IntArray> = Array(ZoneUnit.SIZE_TILE.value) {
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

    private fun deleteMask(localX: TileUnit, localY: TileUnit, mask: Int) {
        if (localX >= ZoneUnit.SIZE_TILE || localY >= ZoneUnit.SIZE_TILE || localX < 0.tiles || localY < 0.tiles) {
            val zone = zone.mapsquareFloor.mapsquare.world.getZone(
                zone.floor, zone.x.inTiles + localX, zone.y.inTiles + localY
            )
            zone?.collisions?.deleteMask(localX.relativeZone, localY.relativeZone, mask)
        } else {
            masks[localX.value][localY.value] = masks[localX.value][localY.value] and mask.inv()
        }
    }

    fun addLocation(loc: Loc): Unit = changeLoc(loc, ::addWall, ::addRegularLoc, ::addDecoration)

    fun deleteLoc(loc: Loc): Unit = changeLoc(loc, ::deleteWall, ::deleteRegularLoc, ::deleteDecoration)
    
    private fun changeLoc(
        loc: Loc,
        wallOp: (TileUnit, TileUnit, Int, Int, Boolean) -> Unit,
        regularOp: (TileUnit, TileUnit, TileUnit, TileUnit, Boolean) -> Unit,
        decOp: (TileUnit, TileUnit) -> Unit,
    ) {
        when (loc.type) {
            in 0..3 -> {
                if (loc.clipType != 0) {
                    wallOp(loc.pos.x.relativeZone, loc.pos.y.relativeZone, loc.type, loc.orientation, loc.impenetrable)
                }
            }
            in 9..21 -> {
                if (loc.clipType != 0) {
                    var sizeX = loc.width
                    var sizeY = loc.length
                    if (loc.orientation == 1 || loc.orientation == 3) {
                        sizeX = loc.length
                        sizeY = loc.width
                    }
                    regularOp(loc.pos.x.relativeZone, loc.pos.y.relativeZone, sizeX, sizeY, loc.impenetrable)
                }
            }
            22 -> if (loc.clipType == 1) decOp(loc.pos.x.relativeZone, loc.pos.y.relativeZone)
        }
    }

    fun addRegularLoc(x: TileUnit, y: TileUnit, sizeX: TileUnit, sizeY: TileUnit, impenetrable: Boolean): Unit =
        changeRegularLoc(x, y, sizeX, sizeY, impenetrable, ::addMask)

    fun deleteRegularLoc(x: TileUnit, y: TileUnit, sizeX: TileUnit, sizeY: TileUnit, impenetrable: Boolean): Unit =
        changeRegularLoc(x, y, sizeX, sizeY, impenetrable, ::deleteMask)

    private fun changeRegularLoc(
        x: TileUnit,
        y: TileUnit,
        sizeX: TileUnit,
        sizeY: TileUnit,
        impenetrable: Boolean,
        op: (TileUnit, TileUnit, Int) -> Unit
    ) {
        var mask = MASK_LOC
        if (impenetrable) mask = mask or MASK_LOC_HIGH
        for (tileX in x until (x + sizeX)) {
            for (tileY in y until y + sizeY) {
                op(tileX, tileY, mask)
            }
        }
    }

    fun addDecoration(x: TileUnit, y: TileUnit): Unit = addMask(x, y, MASK_DECORATION)

    fun deleteDecoration(x: TileUnit, y: TileUnit): Unit = deleteMask(x, y, MASK_DECORATION)

    fun addUnwalkableTile(x: TileUnit, y: TileUnit): Unit = addMask(x, y, MASK_TERRAIN_BLOCK)

    fun deleteunwalkableTile(x: TileUnit, y: TileUnit): Unit = deleteMask(x, y, MASK_TERRAIN_BLOCK)

    fun addWall(x: TileUnit, y: TileUnit, type: Int, orientation: Int, impenetrable: Boolean): Unit =
        changeWall(x, y, type, orientation, impenetrable, ::addMask)

    fun deleteWall(x: TileUnit, y: TileUnit, type: Int, orientation: Int, impenetrable: Boolean): Unit =
        changeWall(x, y, type, orientation, impenetrable, ::deleteMask)

    private fun changeWall(
        x: TileUnit,
        y: TileUnit,
        type: Int,
        orientation: Int,
        impenetrable: Boolean,
        op: (TileUnit, TileUnit, Int) -> Unit
    ) {
        when (type) {
            0 -> {
                when (orientation) {
                    0 -> {
                        op(x, y, MASK_WALL_W)
                        op(x - 1.tiles, y, MASK_WALL_E)
                    }
                    1 -> {
                        op(x, y, MASK_WALL_N)
                        op(x, y + 1.tiles, MASK_WALL_S)
                    }
                    2 -> {
                        op(x, y, MASK_WALL_E)
                        op(x + 1.tiles, y, MASK_WALL_W)
                    }
                    3 -> {
                        op(x, y, MASK_WALL_S)
                        op(x, y - 1.tiles, MASK_WALL_N)
                    }
                }
            }
            1, 3 -> {
                when (orientation) {
                    0 -> {
                        op(x, y, MASK_PILLAR_NW)
                        op(x - 1.tiles, y + 1.tiles, MASK_PILLAR_SE)
                    }
                    1 -> {
                        op(x, y, MASK_PILLAR_NE)
                        op(x + 1.tiles, y + 1.tiles, MASK_PILLAR_SW)
                    }
                    2 -> {
                        op(x, y, MASK_PILLAR_SE)
                        op(x + 1.tiles, y - 1.tiles, MASK_PILLAR_NW)
                    }
                    3 -> {
                        op(x, y, MASK_PILLAR_SW)
                        op(x - 1.tiles, y - 1.tiles, MASK_PILLAR_NE)
                    }
                }
            }
            2 -> {
                when (orientation) {
                    0 -> {
                        op(x, y, MASK_WALL_W or MASK_WALL_N)
                        op(x - 1.tiles, y, MASK_WALL_E)
                        op(x, y + 1.tiles, MASK_WALL_S)
                    }
                    1 -> {
                        op(x, y, MASK_WALL_N or MASK_WALL_E)
                        op(x, y + 1.tiles, MASK_WALL_S)
                        op(x + 1.tiles, y, MASK_WALL_W)
                    }
                    2 -> {
                        op(x, y, MASK_WALL_E or MASK_WALL_S)
                        op(x + 1.tiles, y, MASK_WALL_W)
                        op(x, y - 1.tiles, MASK_WALL_N)
                    }
                    3 -> {
                        op(x, y, MASK_WALL_S or MASK_WALL_W)
                        op(x, y - 1.tiles, MASK_WALL_N)
                        op(x - 1.tiles, y, MASK_WALL_E)
                    }
                }
            }
        }
        if (impenetrable) {
            when (type) {
                0 -> {
                    when (orientation) {
                        0 -> {
                            op(x, y, MASK_WALL_HIGH_W)
                            op(x - 1.tiles, y, MASK_WALL_HIGH_E)
                        }
                        1 -> {
                            op(x, y, MASK_WALL_HIGH_N)
                            op(x, y + 1.tiles, MASK_WALL_HIGH_S)
                        }
                        2 -> {
                            op(x, y, MASK_WALL_HIGH_E)
                            op(x + 1.tiles, y, MASK_WALL_HIGH_W)
                        }
                        3 -> {
                            op(x, y, MASK_WALL_HIGH_S)
                            op(x, y - 1.tiles, MASK_WALL_HIGH_N)
                        }
                    }

                }
                1, 3 -> {
                    when (orientation) {
                        0 -> {
                            op(x, y, MASK_PILLAR_HIGH_NW)
                            op(x - 1.tiles, y + 1.tiles, MASK_PILLAR_HIGH_SE)
                        }
                        1 -> {
                            op(x, y, MASK_PILLAR_HIGH_NE)
                            op(x + 1.tiles, y + 1.tiles, MASK_PILLAR_HIGH_SW)
                        }
                        2 -> {
                            op(x, y, MASK_PILLAR_HIGH_SE)
                            op(x + 1.tiles, y - 1.tiles, MASK_PILLAR_HIGH_NW)
                        }
                        3 -> {
                            op(x, y, MASK_PILLAR_HIGH_SW)
                            op(x - 1.tiles, y - 1.tiles, MASK_PILLAR_HIGH_NE)
                        }
                    }
                }
                2 -> {
                    when (orientation) {
                        0 -> {
                            op(x, y, MASK_WALL_HIGH_W or MASK_WALL_HIGH_N)
                            op(x - 1.tiles, y, MASK_WALL_HIGH_E)
                            op(x, y + 1.tiles, MASK_WALL_HIGH_S)
                        }
                        1 -> {
                            op(x, y, MASK_WALL_HIGH_N or MASK_WALL_HIGH_E)
                            op(x, y + 1.tiles, MASK_WALL_HIGH_S)
                            op(x + 1.tiles, y, MASK_WALL_HIGH_W)
                        }
                        2 -> {
                            op(x, y, MASK_WALL_HIGH_E or MASK_WALL_HIGH_S)
                            op(x + 1.tiles, y, MASK_WALL_HIGH_W)
                            op(x, y - 1.tiles, MASK_WALL_HIGH_N)
                        }
                        3 -> {
                            op(x, y, MASK_WALL_HIGH_S or MASK_WALL_HIGH_W)
                            op(x, y - 1.tiles, MASK_WALL_HIGH_N)
                            op(x - 1.tiles, y, MASK_WALL_HIGH_E)
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val MASK_PILLAR_NW: Int = 0x1
        const val MASK_WALL_N: Int = 0x2
        const val MASK_PILLAR_NE: Int = 0x4
        const val MASK_WALL_E: Int = 0x8
        const val MASK_PILLAR_SE: Int = 0x10
        const val MASK_WALL_S: Int = 0x20
        const val MASK_PILLAR_SW: Int = 0x40
        const val MASK_WALL_W: Int = 0x80
        const val MASK_LOC: Int = 0x100
        const val MASK_PILLAR_HIGH_NW: Int = 0x200
        const val MASK_WALL_HIGH_N: Int = 0x400
        const val MASK_PILLAR_HIGH_NE: Int = 0x800
        const val MASK_WALL_HIGH_E: Int = 0x1000
        const val MASK_PILLAR_HIGH_SE: Int = 0x2000
        const val MASK_WALL_HIGH_S: Int = 0x4000
        const val MASK_PILLAR_HIGH_SW: Int = 0x8000
        const val MASK_WALL_HIGH_W: Int = 0x10000
        const val MASK_LOC_HIGH: Int = 0x20000
        const val MASK_DECORATION: Int = 0x40000
        const val MASK_TERRAIN_BLOCK: Int = 0x200000
        const val BLOCK_TILE: Int = MASK_LOC or MASK_TERRAIN_BLOCK or MASK_DECORATION
        const val BLOCK_NW: Int = MASK_WALL_N or MASK_PILLAR_NW or MASK_WALL_W or BLOCK_TILE
        const val BLOCK_N: Int = MASK_WALL_N or BLOCK_TILE
        const val BLOCK_NE: Int = MASK_WALL_N or MASK_PILLAR_NE or MASK_WALL_E or BLOCK_TILE
        const val BLOCK_E: Int = MASK_WALL_E or BLOCK_TILE
        const val BLOCK_SE: Int = MASK_WALL_S or MASK_PILLAR_SE or MASK_WALL_E or BLOCK_TILE
        const val BLOCK_S: Int = MASK_WALL_S or BLOCK_TILE
        const val BLOCK_SW: Int = MASK_WALL_S or MASK_PILLAR_SW or MASK_WALL_W or BLOCK_TILE
        const val BLOCK_W: Int = MASK_WALL_W or BLOCK_TILE
        const val BLOCK_HIGH_N: Int = MASK_WALL_HIGH_N or MASK_LOC_HIGH
        const val BLOCK_HIGH_E: Int = MASK_WALL_HIGH_E or MASK_LOC_HIGH
        const val BLOCK_HIGH_S: Int = MASK_WALL_HIGH_S or MASK_LOC_HIGH
        const val BLOCK_HIGH_W: Int = MASK_WALL_HIGH_W or MASK_LOC_HIGH
    }
}