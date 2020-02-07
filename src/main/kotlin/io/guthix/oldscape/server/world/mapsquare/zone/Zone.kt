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

import io.guthix.oldscape.server.world.entity.Obj
import io.guthix.oldscape.server.world.entity.Loc
import io.guthix.oldscape.server.world.entity.character.player.Player
import io.guthix.oldscape.server.world.mapsquare.FloorUnit
import io.guthix.oldscape.server.world.mapsquare.MapsquareFloor
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import io.guthix.oldscape.server.world.mapsquare.zone.tile.TileUnit

class Zone(
    val floor: FloorUnit,
    val x: ZoneUnit,
    val y: ZoneUnit,
    val mapsquareFloor: MapsquareFloor
) {
    val collisions = ZoneCollision(this)

    val players = mutableListOf<Player>()

    val groundObjects = mutableMapOf<Tile, MutableList<Obj>>()

    val staticLocations: MutableMap<Int, Loc> = mutableMapOf()

    val dynamicLocations: MutableMap<Int, Loc> = mutableMapOf()

    fun getCollisionMask(localX: TileUnit, localY: TileUnit): Int {
        return collisions.masks[localX.value][localY.value]
    }

    fun getLoc(id: Int, localX: TileUnit, localY: TileUnit): Loc? {
        for(slot in 0 until Loc.UNIQUE_SLOTS) {
            val key = Loc.generateMapKey(localX, localY, slot)
            val mapObject = staticLocations[key] ?: dynamicLocations[key]
            mapObject?.let { if(id == it.blueprint.id) return it }
        }
        return null
    }

    fun addStaticLocation(loc: Loc) {
        staticLocations[loc.mapKey] = loc
        collisions.addLocation(loc)
    }

    fun addUnwalkableTile(localX: TileUnit, localY: TileUnit) = collisions.addUnwalkableTile(localX, localY)

    fun addObject(tile: Tile, obj: Obj) {
        groundObjects.getOrPut(tile, { mutableListOf() }).add(obj)
        players.forEach { player -> player.mapInterest.addObject(tile, obj) }
    }

    fun removeObject(tile: Tile, obj: Obj) {
        val objList = groundObjects[tile] ?: throw IllegalCallerException(
            "Object $obj does not exist at tile $tile."
        )
        if(!objList.remove(obj)) throw IllegalCallerException(
            "Object $obj does not exist at tile $tile."
        )
        if(objList.isEmpty()) groundObjects.remove(tile)
        players.forEach { player -> player.mapInterest.removeObject(tile, obj) }
    }

    override fun toString() = "Zone(z=${floor.value}, x=${x.value}, y=${y.value})"
}