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

import io.guthix.oldscape.server.world.map.dim.*
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Loc
import io.guthix.oldscape.server.world.entity.Obj
import io.guthix.oldscape.server.world.entity.Projectile

class MapsquareFloor(
    val floor: FloorUnit,
    val x: MapsquareUnit,
    val y: MapsquareUnit,
    val mapsquare: Mapsquare
) {
    lateinit var world: World

    val zones: Array<Array<Zone>> = Array(MapsquareUnit.SIZE_ZONE.value) { zoneX ->
        Array(MapsquareUnit.SIZE_ZONE.value) { zoneY ->
            Zone(floor, x.inZones + zoneX.zones, y.inZones + zoneY.zones, this)
        }
    }

    fun getZone(localX: TileUnit, localY: TileUnit): Zone = zones[localX.inZones.value][localY.inZones.value]

    fun getZone(localX: ZoneUnit, localY: ZoneUnit): Zone = zones[localX.value][localY.value]

    fun getCollisionMask(localX: TileUnit, localY: TileUnit): Int = zones[localX.inZones.value][localY.inZones.value]
        .getCollisionMask(localX.relativeZone, localY.relativeZone)

    fun getLoc(id: Int, localX: TileUnit, localY: TileUnit): Loc? = zones[localX.inZones.value][localY.inZones.value]
        .getLoc(id, localX.relativeZone, localY.relativeZone)

    fun addStaticLocation(loc: Loc) {
        val zoneX = loc.pos.x.inZones.relativeMapSquare
        val zoneY = loc.pos.y.inZones.relativeMapSquare
        zones[zoneX.value][zoneY.value].addStaticLoc(loc)
    }

    fun addUnwalkableTile(localX: TileUnit, localY: TileUnit): Unit = zones[localX.inZones.value][localY.inZones.value]
        .addUnwalkableTile(localX.relativeZone, localY.relativeZone)

    fun addObject(tile: Tile, obj: Obj) {
        val zoneX = tile.x.inZones.relativeMapSquare
        val zoneY = tile.y.inZones.relativeMapSquare
        zones[zoneX.value][zoneY.value].addObject(tile, obj)
    }

    fun removeObject(tile: Tile, id: Int): Obj {
        val zoneX = tile.x.inZones.relativeMapSquare
        val zoneY = tile.y.inZones.relativeMapSquare
        return zones[zoneX.value][zoneY.value].removeObject(tile, id)
    }

    fun addDynamicLoc(loc: Loc) {
        val zoneX = loc.pos.x.inZones.relativeMapSquare
        val zoneY = loc.pos.y.inZones.relativeMapSquare
        zones[zoneX.value][zoneY.value].addDynamicLoc(loc)
    }

    fun removeDynamicLoc(loc: Loc) {
        val zoneX = loc.pos.x.inZones.relativeMapSquare
        val zoneY = loc.pos.y.inZones.relativeMapSquare
        zones[zoneX.value][zoneY.value].removeDynamicLoc(loc)
    }

    fun addProjectile(proj: Projectile) {
        val zoneX = proj.start.x.inZones.relativeMapSquare
        val zoneY = proj.start.y.inZones.relativeMapSquare
        zones[zoneX.value][zoneY.value].addProjectile(proj)
    }
}