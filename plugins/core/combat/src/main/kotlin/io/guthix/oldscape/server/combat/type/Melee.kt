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
package io.guthix.oldscape.server.combat.type

import io.guthix.oldscape.server.combat.attackSequence
import io.guthix.oldscape.server.combat.attackSpeed
import io.guthix.oldscape.server.combat.dmg.calcHit
import io.guthix.oldscape.server.combat.dmg.maxMeleeHit
import io.guthix.oldscape.server.combat.inCombatWith
import io.guthix.oldscape.server.template.SequenceTemplates
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.event.NpcAttackedEvent
import io.guthix.oldscape.server.pathing.DestinationRectangleDirect
import io.guthix.oldscape.server.pathing.breadthFirstSearch
import io.guthix.oldscape.server.task.NormalTask
import io.guthix.oldscape.server.template.TemplateNotFoundException
import io.guthix.oldscape.server.template.sequences
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.HitMark
import io.guthix.oldscape.server.world.entity.Npc
import io.guthix.oldscape.server.world.entity.Player

fun Player.meleeAttack(npc: Npc, world: World) {
    val npcDestination = DestinationRectangleDirect(npc, world.map)
    path = breadthFirstSearch(pos, npcDestination, size, true, world.map)
    inCombatWith = npc
    cancelTasks(NormalTask)
    val player = this
    addTask(NormalTask) {
        wait { npcDestination.reached(pos.x, pos.y, size) }
        EventBus.schedule(NpcAttackedEvent(npc, player, world))
        while (true) { // start player combat
            animate(attackSequence)
            val damage = calcHit(npc, maxMeleeHit()) ?: 0
            val hmColor = if (damage == 0) HitMark.Color.BLUE else HitMark.Color.RED
            npc.hit(hmColor, damage, 0)
            npc.animate(SequenceTemplates[npc.sequences?.defence ?: throw TemplateNotFoundException(npc.id)])
            wait(ticks = attackSpeed)
        }
    }.onCancel {
        inCombatWith = null
        turnToLock(null)
    }
}