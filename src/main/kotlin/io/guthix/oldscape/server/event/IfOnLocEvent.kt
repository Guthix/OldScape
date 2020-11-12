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
package io.guthix.oldscape.server.event

import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Loc
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.map.dim.TileUnit

data class IfOnLocEvent(
    val locId: Int,
    val interfaceId: Int,
    val interfaceSlotId: Int,
    val x: TileUnit,
    val y: TileUnit,
    val ctrlPressed: Boolean,
    val someInt: Int,
    override val player: Player,
    override val world: World
) : PlayerGameEvent(player, world) {
    val npc: Loc = world.getLoc(locId, player.pos.floor, x, y) ?: error("Could not find npc with id $locId.")
}