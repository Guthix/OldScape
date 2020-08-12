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

import io.guthix.oldscape.server.world.map.dim.FloorUnit
import io.guthix.oldscape.server.world.map.dim.TileUnit
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.sqrt

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


