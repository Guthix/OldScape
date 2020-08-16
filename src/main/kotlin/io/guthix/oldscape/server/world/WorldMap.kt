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

import io.guthix.cache.js5.Js5Archive
import io.guthix.oldscape.cache.MapArchive
import io.guthix.oldscape.cache.xtea.MapXtea
import io.guthix.oldscape.server.template.type.LocTemplate
import io.guthix.oldscape.server.template.type.ObjTemplate
import io.guthix.oldscape.server.template.type.ProjectileTemplate
import io.guthix.oldscape.server.world.entity.Character
import io.guthix.oldscape.server.world.entity.Loc
import io.guthix.oldscape.server.world.entity.Obj
import io.guthix.oldscape.server.world.entity.Projectile
import io.guthix.oldscape.server.world.map.Mapsquare
import io.guthix.oldscape.server.world.map.Tile
import io.guthix.oldscape.server.world.map.Zone
import io.guthix.oldscape.server.world.map.ZoneCollision
import io.guthix.oldscape.server.world.map.dim.FloorUnit
import io.guthix.oldscape.server.world.map.dim.TileUnit
import io.guthix.oldscape.server.world.map.dim.ZoneUnit
import io.guthix.oldscape.server.world.map.dim.mapsquares

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

    fun addObject(template: ObjTemplate, amount: Int, tile: Tile): Obj {
        val obj = Obj(template, amount)
        mapsquares[id(tile.x, tile.y)]?.addObject(tile, obj)
        return obj
    }

    fun addObject(obj: Obj, tile: Tile): Unit? = mapsquares[id(tile.x, tile.y)]?.addObject(tile, obj)

    fun removeObject(id: Int, tile: Tile): Obj? = mapsquares[id(tile.x, tile.y)]?.removeObject(tile, id)

    fun addDynamicLoc(template: LocTemplate, type: Int, orientation: Int, tile: Tile): Loc {
        val loc = Loc(template, type, tile, orientation)
        mapsquares[id(loc.pos.x, loc.pos.y)]?.addDynamicLoc(loc)
        return loc
    }

    fun removeDynamicLoc(loc: Loc): Unit? = mapsquares[id(loc.pos.x, loc.pos.y)]?.removeDynamicLoc(loc)

    fun addProjectile(template: ProjectileTemplate, start: Tile, target: Character): Projectile {
        val projectile = Projectile(template, start, target)
        mapsquares[id(start.x, start.y)]?.addProjectile(projectile)
        return projectile
    }
}