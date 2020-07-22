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
package io.guthix.oldscape.server.combat

import io.guthix.oldscape.server.blueprints.SequenceBlueprint
import io.guthix.oldscape.server.blueprints.SpotAnimBlueprint
import io.guthix.oldscape.server.combat.type.magicAttack
import io.guthix.oldscape.server.event.IfOnNpcEvent
import io.guthix.oldscape.server.plugin.Script
import io.guthix.oldscape.server.world.entity.Npc
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.entity.Projectile

fun Script.registerCombatSpell(
    interfaceId: Int,
    interfaceSlotId: Int,
    castAnim: SequenceBlueprint,
    spellAnim: SpotAnimBlueprint,
    projectile: Projectile,
    onHit: (Player, Npc) -> Int // TODO
) {
    on(IfOnNpcEvent::class).where { this.interfaceId == interfaceId && this.interfaceSlotId == interfaceSlotId }.then {
        player.magicAttack(npc, world, castAnim, spellAnim, projectile, onHit)
    }
}