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
package io.guthix.oldscape.server.world

import io.guthix.cache.js5.Js5Archive
import io.guthix.oldscape.cache.MapArchive
import io.guthix.oldscape.cache.xtea.MapXtea
import io.guthix.oldscape.server.dimensions.FloorUnit
import io.guthix.oldscape.server.dimensions.TileUnit
import io.guthix.oldscape.server.dimensions.ZoneUnit
import io.guthix.oldscape.server.dimensions.mapsquares
import io.guthix.oldscape.server.world.entity.Loc
import io.guthix.oldscape.server.world.entity.Obj
import io.guthix.oldscape.server.world.entity.Projectile
import io.guthix.oldscape.server.world.map.Mapsquare
import io.guthix.oldscape.server.world.map.Tile
import io.guthix.oldscape.server.world.map.Zone
import io.guthix.oldscape.server.world.map.ZoneCollision

class WorldMap(val mapsquares: MutableMap<Int, Mapsquare>) {
    private fun id(x: TileUnit, y: TileUnit) = Mapsquare.id(x.inMapsquares, y.inMapsquares)

    private fun id(x: ZoneUnit, y: ZoneUnit) = Mapsquare.id(x.inMapsquares, y.inMapsquares)

    fun init(archive: Js5Archive, xteas: List<MapXtea>): WorldMap {
        val mapArchive = MapArchive.load(archive, xteas)
        for ((id, key) in xteas) {
            val mapsquare = mapArchive.mapsquares[id] ?: continue
            mapsquares[id] = Mapsquare(mapsquare.x.mapsquares, mapsquare.y.mapsquares, key, this)
        }
        mapsquares.forEach { (id, mapsquare) ->
            val def = mapArchive.mapsquares[id] ?: throw IllegalStateException(
                "Could not find mapsquare definition for id $id."
            )
            mapsquare.initialize(def)
        }
        return this
    }

    fun getZone(tile: Tile): Zone? = getZone(tile.floor, tile.x, tile.y)

    fun getZone(floor: FloorUnit, x: TileUnit, y: TileUnit): Zone? = mapsquares[id(x, y)]?.getZone(
        floor, x.relativeMapSquare, y.relativeMapSquare
    )

    fun getZone(floor: FloorUnit, x: ZoneUnit, y: ZoneUnit): Zone? = mapsquares[id(x, y)]?.getZone(
        floor, x.relativeMapSquare, y.relativeMapSquare
    )

    fun getLoc(id: Int, floor: FloorUnit, x: TileUnit, y: TileUnit): Loc? = mapsquares[id(x, y)]?.getLoc(
        id, floor, x.relativeMapSquare, y.relativeMapSquare
    )

    fun getCollisionMask(tile: Tile): Int = getCollisionMask(tile.floor, tile.x, tile.y)

    fun getCollisionMask(floor: FloorUnit, x: TileUnit, y: TileUnit): Int = mapsquares[id(x, y)]?.getCollisionMask(
        floor, x.relativeMapSquare, y.relativeMapSquare
    ) ?: ZoneCollision.MASK_TERRAIN_BLOCK

    fun addUnwalkableTile(tile: Tile): Unit? = addUnwalkableTile(tile.floor, tile.x, tile.y)

    fun addUnwalkableTile(floor: FloorUnit, x: TileUnit, y: TileUnit): Unit? = mapsquares[id(x, y)]?.addUnwalkableTile(
        floor, x.relativeMapSquare, y.relativeMapSquare
    )

    fun addObject(tile: Tile, obj: Obj): Unit? = mapsquares[id(tile.x, tile.y)]?.addObject(tile, obj)

    fun removeObject(tile: Tile, id: Int): Obj? = mapsquares[id(tile.x, tile.y)]?.removeObject(tile, id)

    fun addDynamicLoc(loc: Loc): Unit? = mapsquares[id(loc.pos.x, loc.pos.y)]?.addDynamicLoc(loc)

    fun removeDynamicLoc(loc: Loc): Unit? = mapsquares[id(loc.pos.x, loc.pos.y)]?.removeDynamicLoc(loc)

    fun addProjectile(proj: Projectile): Unit? = mapsquares[id(proj.start.x, proj.start.y)]?.addProjectile(proj)
}