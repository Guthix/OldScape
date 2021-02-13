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
package io.guthix.oldscape.server.core.combat.event

import io.guthix.oldscape.server.event.NpcGameEvent
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Npc
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.entity.SpotAnimation

data class NpcHitByPlayerEvent(
    val player: Player,
    override val npc: Npc,
    override val world: World,
    val maxHit: Int,
    val spotAnimOnSuccess: SpotAnimation? = null,
    val spotAnimOnFail: SpotAnimation? = null,
) : NpcGameEvent(npc, world)