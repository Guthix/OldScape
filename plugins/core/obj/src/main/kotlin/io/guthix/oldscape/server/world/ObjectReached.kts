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
package io.guthix.oldscape.server.world

import io.guthix.oldscape.server.event.ObjectReachedEvent
import io.guthix.oldscape.server.world.map.Tile

on(ObjectReachedEvent::class).then {
    val tile = Tile(player.pos.floor, x, y)
    val obj = world.map.removeObject(id, tile) ?: error(
        "Can not pick up object for id $id at position $tile."
    )
    player.topInterface.itemBag.add(obj)
}