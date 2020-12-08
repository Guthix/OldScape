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

import io.guthix.oldscape.server.PropertyHolder
import io.guthix.oldscape.server.world.entity.*
import io.guthix.oldscape.server.world.map.dim.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.reflect.KProperty

@Serializable(with = ZoneSerializer::class)
data class Zone internal constructor(val floor: FloorUnit, val x: ZoneUnit, val y: ZoneUnit) : PropertyHolder {
    val id: Int get() = id(floor, x, y)

    val masks: Array<IntArray> = Array(ZoneUnit.SIZE_TILE.value) {
        IntArray(ZoneUnit.SIZE_TILE.value)
    }

    internal val playersLoaded: MutableList<Player> = mutableListOf()

    val players: MutableList<Player> = mutableListOf()

    val npcs: MutableList<Npc> = mutableListOf()

    val groundObjects: MutableMap<Tile, MutableMap<Int, MutableList<Obj>>> = mutableMapOf()

    val staticLocs: MutableMap<Int, Loc> = mutableMapOf()

    val addedLocs: MutableMap<Int, Loc> = mutableMapOf()

    val deletedLocs: MutableMap<Int, Loc> = mutableMapOf()

    override val properties: MutableMap<KProperty<*>, Any?> = mutableMapOf()

    fun addCollision(localX: TileUnit, localY: TileUnit, mask: Int) {
        masks[localX.value][localY.value] = masks[localX.value][localY.value] or mask
    }

    fun delCollision(localX: TileUnit, localY: TileUnit, mask: Int) {
        masks[localX.value][localY.value] = masks[localX.value][localY.value] and mask.inv()
    }

    fun getLoc(id: Int, localX: TileUnit, localY: TileUnit): Loc? {
        for (slot in 0 until Loc.UNIQUE_SLOTS) {
            val key = Loc.generateMapKey(localX, localY, slot)
            val loc = staticLocs[key] ?: addedLocs[key]
            loc?.let { if (id == it.id) return@getLoc if (deletedLocs[key] != null) null else it }
        }
        return null
    }

    fun addLoc(loc: Loc) {
        val locAtPosition = staticLocs[loc.mapKey]
        require(locAtPosition == null) { "Can't add loc because $locAtPosition already exists on ${loc.pos}." }
        addedLocs[loc.mapKey] = loc
        playersLoaded.forEach { player -> player.scene.addChangeLoc(loc) }
    }

    fun delLoc(loc: Loc) {
        val addedLoc = addedLocs[loc.mapKey]
        if(addedLoc != null) {
            addedLocs.remove(loc.mapKey)
        } else {
            val staticLoc = staticLocs[loc.mapKey]
            require(staticLoc != null) {  "Can't deleted loc because $loc doesn't exist." }
            deletedLocs[loc.mapKey] = staticLoc
        }
        playersLoaded.forEach { player -> player.scene.delLoc(loc) }
    }

    fun addObject(tile: Tile, obj: Obj) {
        groundObjects.getOrPut(tile, { mutableMapOf() }).getOrPut(obj.id, { mutableListOf() }).add(obj)
        playersLoaded.forEach { player -> player.scene.addObject(tile, obj) }
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
        playersLoaded.forEach { player -> player.scene.removeObject(tile, obj) }
        return obj
    }

    fun addProjectile(proj: Projectile) {
        playersLoaded.forEach { player -> player.scene.addProjectile(proj) }
    }

    override fun toString(): String = "Zone(z=${floor.value}, x=${x.value}, y=${y.value})"

    companion object {
        fun id(floor: FloorUnit, x: ZoneUnit, y: ZoneUnit): Int = y.value or x.value shl 13 or floor.value shl 26
    }
}

@Serializable
@SerialName("Zone")
private data class ZoneSurrogate(val floor: Int, val x: Int, val y: Int)

object ZoneSerializer : KSerializer<Zone> {
    override val descriptor: SerialDescriptor = ZoneSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Zone) {
        val surrogate = ZoneSurrogate(value.floor.value, value.x.value, value.y.value)
        encoder.encodeSerializableValue(ZoneSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Zone {
        val surrogate = decoder.decodeSerializableValue(ZoneSurrogate.serializer())
        return Zone(surrogate.floor.floors, surrogate.x.zones, surrogate.y.zones)
    }
}