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
package io.guthix.oldscape.server

import io.guthix.oldscape.cache.xtea.MapXtea
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import java.nio.file.Files
import java.nio.file.Path

object XTEA {
    lateinit var id: Map<Int, IntArray>

    fun initJson(filePath: Path) {
        val xteas = Json(JsonConfiguration.Stable).parse(MapXtea.serializer().list, Files.readString(filePath))
        val result = mutableMapOf<Int, IntArray>()
        for(mapXtea in xteas) {
            result[mapXtea.id] = mapXtea.key
        }
        id = result.toMap()
    }
}

