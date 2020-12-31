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
package io.guthix.oldscape.cache

import io.guthix.js5.Js5Archive
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