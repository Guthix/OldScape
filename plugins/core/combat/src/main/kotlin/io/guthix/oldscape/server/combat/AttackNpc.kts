/*
 * This file is part of Guthix OldScape-Server.
 *
 * Guthix OldScape-Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape-Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Guthix OldScape-Server. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.combat

import io.guthix.oldscape.server.blueprints.AttackType
import io.guthix.oldscape.server.combat.type.meleeAttack
import io.guthix.oldscape.server.combat.type.rangeAttack
import io.guthix.oldscape.server.event.NpcClickEvent

on(NpcClickEvent::class).where { contextMenuEntry == "Attack" }.then {
    if (player.inCombatWith == npc) return@then
    player.turnToLock(npc)
    when (player.currentStyle.attackType) {
        AttackType.RANGED -> player.rangeAttack(npc, world)
        else -> player.meleeAttack(npc, world)
    }
}