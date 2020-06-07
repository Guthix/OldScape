/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.cache

import io.guthix.cache.js5.Js5Archive
import io.guthix.oldscape.cache.map.MapSquareDefinition
import io.guthix.oldscape.cache.xtea.MapXtea
import java.io.FileNotFoundException

public class MapArchive(public val mapsquares: Map<Int, MapSquareDefinition>) {
    public companion object  {
        public const val id: Int = 5

        public fun load(archive: Js5Archive, xteas: List<MapXtea>): MapArchive {
            val mapSquares = mutableMapOf<Int, MapSquareDefinition>()
            xteas.forEach {
                val mapFile = archive.readGroup("m${it.x}_${it.y}").files[0] ?: throw FileNotFoundException(
                    "Map file not found for map m${it.x}_${it.y}."
                )
                val locFile = archive.readGroup("l${it.x}_${it.y}", it.key).files[0] ?: throw FileNotFoundException(
                    "Loc file not found for loc l${it.x}_${it.y}."
                )
                mapSquares[it.id] = MapSquareDefinition.decode(mapFile.data, locFile.data, it.x, it.y)
            }
            return MapArchive(mapSquares)
        }
    }
}