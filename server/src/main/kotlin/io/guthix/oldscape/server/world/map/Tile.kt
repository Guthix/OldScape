/*
 * Copyright 2018-2021 Guthix
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

import io.guthix.oldscape.dim.FloorUnit
import io.guthix.oldscape.dim.TileUnit
import io.guthix.oldscape.dim.floors
import io.guthix.oldscape.dim.tiles
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.sqrt

@Serializable(with = TileSerializer::class)
data class Tile(val floor: FloorUnit, val x: TileUnit, val y: TileUnit) {
    fun withInDistanceOf(other: Tile, distance: TileUnit): Boolean = if (floor == other.floor) {
        abs((other.x - x).value) <= distance.value && abs((other.y - y).value) <= distance.value
    } else {
        false
    }

    fun distanceTo(other: Tile): Int {
        val dx = x - other.x
        val dy = y - other.y
        return ceil(sqrt((dx * dx + dy * dy).value.toDouble())).toInt()
    }

    override fun toString(): String = "Tile(z=${floor.value}, x=${x.value}, y=${y.value})"
}


@Serializable
@SerialName("Tile")
private data class TileSurrogate(val floor: Int, val x: Int, val y: Int)

object TileSerializer : KSerializer<Tile> {
    override val descriptor: SerialDescriptor = TileSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Tile) {
        val surrogate = TileSurrogate(value.floor.value, value.x.value, value.y.value)
        encoder.encodeSerializableValue(TileSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Tile {
        val surrogate = decoder.decodeSerializableValue(TileSurrogate.serializer())
        return Tile(surrogate.floor.floors, surrogate.x.tiles, surrogate.y.tiles)
    }
}