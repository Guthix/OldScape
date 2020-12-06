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
package io.guthix.oldscape.server.music

import io.guthix.oldscape.server.event.CharacterMovedEvent
import io.guthix.oldscape.server.event.PlayerInitialized
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.map.dim.MapsquareUnit

on(CharacterMovedEvent::class).where { character is Player }.then {
    if (
        from.floor != character.pos.floor ||
        (from.x / MapsquareUnit.SIZE_TILE != character.pos.x / MapsquareUnit.SIZE_TILE) ||
        (from.y / MapsquareUnit.SIZE_TILE != character.pos.y / MapsquareUnit.SIZE_TILE)
    ) {
        val zone = world.getZone(character.pos)
        zone?.musicTrack?.let((character as Player)::playSong)
    }
}

on(PlayerInitialized::class).then {
    val zone = world.getZone(player.pos)
    zone?.musicTrack?.let(player::playSong)
}