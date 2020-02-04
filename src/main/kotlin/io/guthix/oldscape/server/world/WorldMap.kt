package io.guthix.oldscape.server.world

import io.guthix.cache.js5.Js5Archive
import io.guthix.oldscape.cache.MapArchive
import io.guthix.oldscape.cache.xtea.MapXtea
import io.guthix.oldscape.server.world.mapsquare.FloorUnit
import io.guthix.oldscape.server.world.mapsquare.Mapsquare
import io.guthix.oldscape.server.world.mapsquare.mapsquares
import io.guthix.oldscape.server.world.mapsquare.zone.ZoneCollision
import io.guthix.oldscape.server.world.mapsquare.zone.ZoneUnit
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import io.guthix.oldscape.server.world.mapsquare.zone.tile.TileUnit

class WorldMap(val mapsquares: MutableMap<Int, Mapsquare>) {
    private fun id(x: TileUnit, y: TileUnit) = Mapsquare.id(x.inMapsquares, y.inMapsquares)

    private fun id(x: ZoneUnit, y: ZoneUnit) = Mapsquare.id(x.inMapsquares, y.inMapsquares)

    fun init(archive: Js5Archive, xteas: List<MapXtea>): WorldMap {
        val mapArchive = MapArchive.load(archive, xteas)
        for(mapXtea in xteas) {
            val mapsquare = mapArchive.mapsquares[mapXtea.id] ?: continue
            mapsquares[mapXtea.id] = Mapsquare(mapsquare.x.mapsquares, mapsquare.y.mapsquares, mapXtea.key, this)
        }
        mapsquares.forEach { (id, mapsquare) ->
            val def = mapArchive.mapsquares[id] ?: throw IllegalStateException(
                "Could not find mapsquare definition for id $id."
            )
            mapsquare.initialize(def)
        }
        return this
    }

    fun getZone(tile: Tile) = getZone(tile.floor, tile.x, tile.y)

    fun getZone(floor: FloorUnit, x: TileUnit, y: TileUnit) = mapsquares[id(x, y)]?.getZone(
        floor, x.relativeMapSquare, y.relativeMapSquare
    )

    fun getZone(floor: FloorUnit, x: ZoneUnit, y: ZoneUnit) = mapsquares[id(x, y)]?.getZone(
        floor, x.relativeMapSquare, y.relativeMapSquare
    )

    fun getLocation(id: Int, floor: FloorUnit, x: TileUnit, y: TileUnit) = mapsquares[id(x, y)]?.getLocation(
        id, floor, x.relativeMapSquare, y.relativeMapSquare
    )

    fun addUnwalkableTile(floor: FloorUnit, x: TileUnit, y: TileUnit) = mapsquares[id(x, y)]?.addUnwalkableTile(
        floor, x.relativeMapSquare, y.relativeMapSquare
    )

    fun getCollisionMask(floor: FloorUnit, x: TileUnit, y: TileUnit) = mapsquares[id(x, y)]?.getCollisionMask(
        floor, x.relativeMapSquare, y.relativeMapSquare
    ) ?: ZoneCollision.MASK_UNWALKABLE_TILE
}