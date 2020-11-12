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
package io.guthix.oldscape.server.world

import io.guthix.oldscape.server.world.entity.Loc
import io.guthix.oldscape.server.world.map.Tile
import io.guthix.oldscape.server.world.map.Zone
import io.guthix.oldscape.server.world.map.dim.FloorUnit
import io.guthix.oldscape.server.world.map.dim.TileUnit
import io.guthix.oldscape.server.world.map.dim.tiles
import io.guthix.oldscape.server.world.map.dim.until

internal fun World.addCollision(tile: Tile, mask: Int): Unit? =
    getZone(tile)?.addCollision(tile.x.relativeZone, tile.y.relativeZone, mask)

internal fun World.addCollision(floor: FloorUnit, x: TileUnit, y: TileUnit, mask: Int): Unit? =
    getZone(floor, x, y)?.addCollision(x.relativeZone, y.relativeZone, mask)

internal fun World.delCollision(tile: Tile, mask: Int): Unit? =
    getZone(tile)?.deleteCollision(tile.x.relativeZone, tile.y.relativeZone, mask)

internal fun World.delCollision(floor: FloorUnit, x: TileUnit, y: TileUnit, mask: Int): Unit? =
    getZone(floor, x, y)?.deleteCollision(x.relativeZone, y.relativeZone, mask)

internal fun World.addLocCollision(loc: Loc): Unit =
    changeLocCollision(loc, ::addWallCollision, ::addRegularLocCollision, ::addDecorationCollision)

internal fun World.deleteLocCollision(loc: Loc): Unit =
    changeLocCollision(loc, ::deleteWallCollision, ::deleteRegularLocCollision, ::deleteDecorationCollision)

private fun changeLocCollision(
    loc: Loc,
    wallOp: (Tile, Int, Int, Boolean) -> Unit,
    regularOp: (Tile, TileUnit, TileUnit, Boolean) -> Unit,
    decOp: (Tile) -> Unit,
) {
    when (loc.type) {
        in 0..3 -> {
            if (loc.clipType != 0) {
                wallOp(loc.pos, loc.type, loc.orientation, loc.impenetrable)
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
                regularOp(loc.pos, sizeX, sizeY, loc.impenetrable)
            }
        }
        22 -> if (loc.clipType == 1) decOp(loc.pos)
    }
}

private fun World.addRegularLocCollision(
    tile: Tile,
    sizeX: TileUnit,
    sizeY: TileUnit,
    impenetrable: Boolean
): Unit = changeRegularLocCollision(tile, sizeX, sizeY, impenetrable, ::addCollision)

private fun World.deleteRegularLocCollision(
    tile: Tile,
    sizeX: TileUnit,
    sizeY: TileUnit,
    impenetrable: Boolean
): Unit = changeRegularLocCollision(tile, sizeX, sizeY, impenetrable, ::delCollision)

private fun changeRegularLocCollision(
    tile: Tile,
    sizeX: TileUnit,
    sizeY: TileUnit,
    impenetrable: Boolean,
    op: (FloorUnit, TileUnit, TileUnit, Int) -> Unit
) {
    var mask = Zone.MASK_LOC
    if (impenetrable) mask = mask or Zone.MASK_LOC_HIGH
    for (tileX in tile.x until (tile.x + sizeX)) {
        for (tileY in tile.y until tile.y + sizeY) {
            op(tile.floor, tileX, tileY, mask)
        }
    }
}

private fun World.addDecorationCollision(tile: Tile): Unit? =
    addCollision(tile.floor, tile.x, tile.y, Zone.MASK_DECORATION)

private fun World.deleteDecorationCollision(tile: Tile): Unit? =
    delCollision(tile.floor, tile.x, tile.y, Zone.MASK_DECORATION)

private fun World.addWallCollision(tile: Tile, type: Int, orientation: Int, impenetrable: Boolean): Unit =
    changeWallCollision(tile, type, orientation, impenetrable, ::addCollision)

private fun World.deleteWallCollision(tile: Tile, type: Int, orientation: Int, impenetrable: Boolean): Unit =
    changeWallCollision(tile, type, orientation, impenetrable, ::delCollision)

private fun changeWallCollision(
    tile: Tile,
    type: Int,
    orientation: Int,
    impenetrable: Boolean,
    op: (FloorUnit, TileUnit, TileUnit, Int) -> Unit?
) {
    when (type) {
        0 -> {
            when (orientation) {
                0 -> {
                    op(tile.floor, tile.x, tile.y, Zone.MASK_WALL_W)
                    op(tile.floor, tile.x - 1.tiles, tile.y, Zone.MASK_WALL_E)
                }
                1 -> {
                    op(tile.floor, tile.x, tile.y, Zone.MASK_WALL_N)
                    op(tile.floor, tile.x, tile.y + 1.tiles, Zone.MASK_WALL_S)
                }
                2 -> {
                    op(tile.floor, tile.x, tile.y, Zone.MASK_WALL_E)
                    op(tile.floor, tile.x + 1.tiles, tile.y, Zone.MASK_WALL_W)
                }
                3 -> {
                    op(tile.floor, tile.x, tile.y, Zone.MASK_WALL_S)
                    op(tile.floor, tile.x, tile.y - 1.tiles, Zone.MASK_WALL_N)
                }
            }
        }
        1, 3 -> {
            when (orientation) {
                0 -> {
                    op(tile.floor, tile.x, tile.y, Zone.MASK_PILLAR_NW)
                    op(tile.floor, tile.x - 1.tiles, tile.y + 1.tiles, Zone.MASK_PILLAR_SE)
                }
                1 -> {
                    op(tile.floor, tile.x, tile.y, Zone.MASK_PILLAR_NE)
                    op(tile.floor, tile.x + 1.tiles, tile.y + 1.tiles, Zone.MASK_PILLAR_SW)
                }
                2 -> {
                    op(tile.floor, tile.x, tile.y, Zone.MASK_PILLAR_SE)
                    op(tile.floor, tile.x + 1.tiles, tile.y - 1.tiles, Zone.MASK_PILLAR_NW)
                }
                3 -> {
                    op(tile.floor, tile.x, tile.y, Zone.MASK_PILLAR_SW)
                    op(tile.floor, tile.x - 1.tiles, tile.y - 1.tiles, Zone.MASK_PILLAR_NE)
                }
            }
        }
        2 -> {
            when (orientation) {
                0 -> {
                    op(tile.floor, tile.x, tile.y, Zone.MASK_WALL_W or Zone.MASK_WALL_N)
                    op(tile.floor, tile.x - 1.tiles, tile.y, Zone.MASK_WALL_E)
                    op(tile.floor, tile.x, tile.y + 1.tiles, Zone.MASK_WALL_S)
                }
                1 -> {
                    op(tile.floor, tile.x, tile.y, Zone.MASK_WALL_N or Zone.MASK_WALL_E)
                    op(tile.floor, tile.x, tile.y + 1.tiles, Zone.MASK_WALL_S)
                    op(tile.floor, tile.x + 1.tiles, tile.y, Zone.MASK_WALL_W)
                }
                2 -> {
                    op(tile.floor, tile.x, tile.y, Zone.MASK_WALL_E or Zone.MASK_WALL_S)
                    op(tile.floor, tile.x + 1.tiles, tile.y, Zone.MASK_WALL_W)
                    op(tile.floor, tile.x, tile.y - 1.tiles, Zone.MASK_WALL_N)
                }
                3 -> {
                    op(tile.floor, tile.x, tile.y, Zone.MASK_WALL_S or Zone.MASK_WALL_W)
                    op(tile.floor, tile.x, tile.y - 1.tiles, Zone.MASK_WALL_N)
                    op(tile.floor, tile.x - 1.tiles, tile.y, Zone.MASK_WALL_E)
                }
            }
        }
    }
    if (impenetrable) {
        when (type) {
            0 -> {
                when (orientation) {
                    0 -> {
                        op(tile.floor, tile.x, tile.y, Zone.MASK_WALL_HIGH_W)
                        op(tile.floor, tile.x - 1.tiles, tile.y, Zone.MASK_WALL_HIGH_E)
                    }
                    1 -> {
                        op(tile.floor, tile.x, tile.y, Zone.MASK_WALL_HIGH_N)
                        op(tile.floor, tile.x, tile.y + 1.tiles, Zone.MASK_WALL_HIGH_S)
                    }
                    2 -> {
                        op(tile.floor, tile.x, tile.y, Zone.MASK_WALL_HIGH_E)
                        op(tile.floor, tile.x + 1.tiles, tile.y, Zone.MASK_WALL_HIGH_W)
                    }
                    3 -> {
                        op(tile.floor, tile.x, tile.y, Zone.MASK_WALL_HIGH_S)
                        op(tile.floor, tile.x, tile.y - 1.tiles, Zone.MASK_WALL_HIGH_N)
                    }
                }

            }
            1, 3 -> {
                when (orientation) {
                    0 -> {
                        op(tile.floor, tile.x, tile.y, Zone.MASK_PILLAR_HIGH_NW)
                        op(tile.floor, tile.x - 1.tiles, tile.y + 1.tiles, Zone.MASK_PILLAR_HIGH_SE)
                    }
                    1 -> {
                        op(tile.floor, tile.x, tile.y, Zone.MASK_PILLAR_HIGH_NE)
                        op(tile.floor, tile.x + 1.tiles, tile.y + 1.tiles, Zone.MASK_PILLAR_HIGH_SW)
                    }
                    2 -> {
                        op(tile.floor, tile.x, tile.y, Zone.MASK_PILLAR_HIGH_SE)
                        op(tile.floor, tile.x + 1.tiles, tile.y - 1.tiles, Zone.MASK_PILLAR_HIGH_NW)
                    }
                    3 -> {
                        op(tile.floor, tile.x, tile.y, Zone.MASK_PILLAR_HIGH_SW)
                        op(tile.floor, tile.x - 1.tiles, tile.y - 1.tiles, Zone.MASK_PILLAR_HIGH_NE)
                    }
                }
            }
            2 -> {
                when (orientation) {
                    0 -> {
                        op(tile.floor, tile.x, tile.y, Zone.MASK_WALL_HIGH_W or Zone.MASK_WALL_HIGH_N)
                        op(tile.floor, tile.x - 1.tiles, tile.y, Zone.MASK_WALL_HIGH_E)
                        op(tile.floor, tile.x, tile.y + 1.tiles, Zone.MASK_WALL_HIGH_S)
                    }
                    1 -> {
                        op(tile.floor, tile.x, tile.y, Zone.MASK_WALL_HIGH_N or Zone.MASK_WALL_HIGH_E)
                        op(tile.floor, tile.x, tile.y + 1.tiles, Zone.MASK_WALL_HIGH_S)
                        op(tile.floor, tile.x + 1.tiles, tile.y, Zone.MASK_WALL_HIGH_W)
                    }
                    2 -> {
                        op(tile.floor, tile.x, tile.y, Zone.MASK_WALL_HIGH_E or Zone.MASK_WALL_HIGH_S)
                        op(tile.floor, tile.x + 1.tiles, tile.y, Zone.MASK_WALL_HIGH_W)
                        op(tile.floor, tile.x, tile.y - 1.tiles, Zone.MASK_WALL_HIGH_N)
                    }
                    3 -> {
                        op(tile.floor, tile.x, tile.y, Zone.MASK_WALL_HIGH_S or Zone.MASK_WALL_HIGH_W)
                        op(tile.floor, tile.x, tile.y - 1.tiles, Zone.MASK_WALL_HIGH_N)
                        op(tile.floor, tile.x - 1.tiles, tile.y, Zone.MASK_WALL_HIGH_E)
                    }
                }
            }
        }
    }
}