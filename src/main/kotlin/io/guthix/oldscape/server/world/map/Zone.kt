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
package io.guthix.oldscape.server.world.map

import io.guthix.oldscape.server.dimensions.FloorUnit
import io.guthix.oldscape.server.dimensions.TileUnit
import io.guthix.oldscape.server.dimensions.ZoneUnit
import io.guthix.oldscape.server.world.entity.Obj
import io.guthix.oldscape.server.world.entity.Loc
import io.guthix.oldscape.server.world.entity.Player

class Zone(
    val floor: FloorUnit,
    val x: ZoneUnit,
    val y: ZoneUnit,
    val mapsquareFloor: MapsquareFloor
) {
    val collisions = ZoneCollision(this)

    val players = mutableListOf<Player>()

    val groundObjects = mutableMapOf<Tile, MutableMap<Int, MutableList<Obj>>>()

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

    internal fun addStaticLoc(loc: Loc) {
        staticLocations[loc.mapKey] = loc
        collisions.addLocation(loc)
    }

    fun addUnwalkableTile(localX: TileUnit, localY: TileUnit) = collisions.addUnwalkableTile(localX, localY)

    fun addObject(tile: Tile, obj: Obj) {
        groundObjects.getOrPut(tile, { mutableMapOf() }).getOrPut(obj.blueprint.id, { mutableListOf() }).add(obj)
        players.forEach { player -> player.mapInterestManager.addObject(tile, obj) }
    }

    fun removeObject(tile: Tile, id: Int): Obj {
        val objIdMap = groundObjects[tile] ?: throw IllegalCallerException(
            "Object $id does not exist at tile $tile."
        )
        val objList = objIdMap[id] ?: throw IllegalCallerException(
            "Object $id does not exist at tile $tile."
        )
        val obj = objList.removeFirst()
        if(objIdMap.isEmpty()) groundObjects.remove(tile)
        if(objList.isEmpty()) groundObjects[tile]?.remove(id)
        players.forEach { player -> player.mapInterestManager.removeObject(tile, obj) }
        return obj
    }

    fun addDynamicLoc(loc: Loc) {
        staticLocations[loc.mapKey] = loc
        players.forEach { player -> player.mapInterestManager.addDynamicLoc(loc) }
    }

    fun removeDynamicLoc(loc: Loc) {
        staticLocations.remove(loc.mapKey)
        players.forEach { player -> player.mapInterestManager.removeDynamicLoc(loc) }
    }

    override fun toString() = "Zone(z=${floor.value}, x=${x.value}, y=${y.value})"
}