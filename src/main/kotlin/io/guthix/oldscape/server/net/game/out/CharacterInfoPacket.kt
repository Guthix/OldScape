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
package io.guthix.oldscape.server.net.game.out

import io.guthix.oldscape.server.dimensions.TileUnit
import io.guthix.oldscape.server.dimensions.tiles
import io.guthix.oldscape.server.world.map.Tile

abstract class CharacterInfoPacket {
    companion object {
        val INTEREST_SIZE: TileUnit = 32.tiles

        private val INTEREST_RANGE = INTEREST_SIZE / 2.tiles

        fun Tile.isInterestedIn(other: Tile): Boolean = withInDistanceOf(other, INTEREST_RANGE)
    }
}