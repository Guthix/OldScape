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

import io.guthix.oldscape.server.world.entity.*
import io.guthix.oldscape.server.world.map.dim.FloorUnit
import io.guthix.oldscape.server.world.map.dim.TileUnit
import io.guthix.oldscape.server.world.map.dim.ZoneUnit

class Zone(
    val floor: FloorUnit,
    val x: ZoneUnit,
    val y: ZoneUnit,
    val mapsquareFloor: MapsquareFloor
) {
    val collisions: ZoneCollision = ZoneCollision(this)

    val players: MutableList<Player> = mutableListOf()

    val npcs: MutableList<Npc> = mutableListOf()

    val groundObjects: MutableMap<Tile, MutableMap<Int, MutableList<Obj>>> = mutableMapOf()

    val staticLocations: MutableMap<Int, Loc> = mutableMapOf()

    val dynamicLocations: MutableMap<Int, Loc> = mutableMapOf()

    fun getCollisionMask(localX: TileUnit, localY: TileUnit): Int = collisions.masks[localX.value][localY.value]

    fun getLoc(id: Int, localX: TileUnit, localY: TileUnit): Loc? {
        for (slot in 0 until Loc.UNIQUE_SLOTS) {
            val key = Loc.generateMapKey(localX, localY, slot)
            val mapObject = staticLocations[key] ?: dynamicLocations[key]
            mapObject?.let { if (id == it.id) return@getLoc it }
        }
        return null
    }

    internal fun addStaticLoc(loc: Loc) {
        staticLocations[loc.mapKey] = loc
        collisions.addLocation(loc)
    }

    fun addUnwalkableTile(localX: TileUnit, localY: TileUnit): Unit = collisions.addUnwalkableTile(localX, localY)

    fun addObject(tile: Tile, obj: Obj) {
        groundObjects.getOrPut(tile, { mutableMapOf() }).getOrPut(obj.id, { mutableListOf() }).add(obj)
        players.forEach { player -> player.sceneManager.addObject(tile, obj) }
    }

    fun removeObject(tile: Tile, id: Int): Obj {
        val objIdMap = groundObjects[tile] ?: throw IllegalCallerException(
            "Object $id does not exist at tile $tile."
        )
        val objList = objIdMap[id] ?: throw IllegalCallerException(
            "Object $id does not exist at tile $tile."
        )
        val obj = objList.removeFirst()
        if (objIdMap.isEmpty()) groundObjects.remove(tile)
        if (objList.isEmpty()) groundObjects[tile]?.remove(id)
        players.forEach { player -> player.sceneManager.removeObject(tile, obj) }
        return obj
    }

    fun addDynamicLoc(loc: Loc) {
        staticLocations[loc.mapKey] = loc
        players.forEach { player -> player.sceneManager.addDynamicLoc(loc) }
    }

    fun removeDynamicLoc(loc: Loc) {
        staticLocations.remove(loc.mapKey)
        players.forEach { player -> player.sceneManager.removeDynamicLoc(loc) }
    }

    fun addProjectile(proj: Projectile) {
        players.forEach { player -> player.sceneManager.addProjectile(proj) }

    }

    override fun toString(): String = "Zone(z=${floor.value}, x=${x.value}, y=${y.value})"
}