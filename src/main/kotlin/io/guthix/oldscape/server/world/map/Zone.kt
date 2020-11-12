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
import io.guthix.oldscape.server.world.map.dim.*

class Zone(val floor: FloorUnit, val x: ZoneUnit, val y: ZoneUnit) {
    val id: Int get() = id(floor, x, y)

    val masks: Array<IntArray> = Array(ZoneUnit.SIZE_TILE.value) {
        IntArray(ZoneUnit.SIZE_TILE.value)
    }

    val players: MutableList<Player> = mutableListOf()

    val npcs: MutableList<Npc> = mutableListOf()

    val groundObjects: MutableMap<Tile, MutableMap<Int, MutableList<Obj>>> = mutableMapOf()

    val staticLocations: MutableMap<Int, Loc> = mutableMapOf()

    val dynamicLocations: MutableMap<Int, Loc> = mutableMapOf()

    fun addCollision(localX: TileUnit, localY: TileUnit, mask: Int) {
        masks[localX.value][localY.value] = masks[localX.value][localY.value] or mask
    }

    fun deleteCollision(localX: TileUnit, localY: TileUnit, mask: Int) {
        masks[localX.value][localY.value] = masks[localX.value][localY.value] and mask.inv()
    }

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
    }

    fun addObject(tile: Tile, obj: Obj) {
        groundObjects.getOrPut(tile, { mutableMapOf() }).getOrPut(obj.id, { mutableListOf() }).add(obj)
        players.forEach { player -> player.scene.addObject(tile, obj) }
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
        players.forEach { player -> player.scene.removeObject(tile, obj) }
        return obj
    }

    fun addLoc(loc: Loc) {
        dynamicLocations[loc.mapKey] = loc
        players.forEach { player -> player.scene.addChangeLoc(loc) }
    }

    fun removeLoc(loc: Loc) {
        staticLocations.remove(loc.mapKey)
        players.forEach { player -> player.scene.removeLoc(loc) }
    }

    fun addProjectile(proj: Projectile) {
        players.forEach { player -> player.scene.addProjectile(proj) }
    }

    override fun toString(): String = "Zone(z=${floor.value}, x=${x.value}, y=${y.value})"

    companion object {
        fun id(floor: FloorUnit, x: ZoneUnit, y: ZoneUnit): Int = y.value or x.value shl 13 or floor.value shl 26

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