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
package io.guthix.oldscape.server.api

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.nio.file.Files
import java.nio.file.Path

//TODO use MapXtea from the cache module
@Serializable
data class MapXtea(val id: Int, val key: IntArray) {
    val x get() = id shr 8

    val y get() = id and 0xFF

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as MapXtea
        if (id != other.id) return false
        if (!key.contentEquals(other.key)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + key.contentHashCode()
        return result
    }
}

object Xtea {
    lateinit var key: Map<Int, IntArray>

    fun initJson(filePath: Path) {
        val xteas = Json(JsonConfiguration.Stable).parse(MapXtea.serializer().list, Files.readString(filePath))
        val result = mutableMapOf<Int, IntArray>()
        for(mapXtea in xteas) {
            result[mapXtea.id] = mapXtea.key
        }
        key = result.toMap()
    }
}

