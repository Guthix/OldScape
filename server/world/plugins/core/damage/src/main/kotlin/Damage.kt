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
package io.guthix.oldscape.server.core.damage

import io.guthix.oldscape.server.Property
import io.guthix.oldscape.server.core.damage.event.NpcDiedEvent
import io.guthix.oldscape.server.core.damage.event.PlayerDiedEvent
import io.guthix.oldscape.server.core.monster.template.deathSequence
import io.guthix.oldscape.server.core.monster.template.stats
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.task.NormalTask
import io.guthix.oldscape.cache.SequenceIds
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.HitMark
import io.guthix.oldscape.server.world.entity.Npc
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.entity.StaticHealthBarUpdate

val Player.deathSequence: Int by Property { SequenceIds.PLAYER_DEATH_836 }

var Player.health: Int
    get() = stats.hitpoints.status
    set(value) {
        stats.hitpoints.status = value
    }

var Npc.health: Int by Property {
    stats.health
}

fun Player.hit(world: World, hitDamage: Int): Boolean {
    if (hitDamage >= health) {
        addHitMark(HitMark(HitMark.Color.RED, health, 0))
        health = 0
        updateHealthBar(StaticHealthBarUpdate(id = 0, curHealth = health, maxHealth = stats.hitpoints.level))
        cancelTasks(NormalTask)
        animate(deathSequence)
        addTask(NormalTask) {
            wait(sequence?.duration ?: 0)
            stopAnimation() // dead animation runs for a very long time
            EventBus.schedule(PlayerDiedEvent(this@hit, world))
        }
        return true
    }
    val hmColor = if (hitDamage == 0) HitMark.Color.BLUE else HitMark.Color.RED
    addHitMark(HitMark(hmColor, hitDamage, 0))
    health -= hitDamage
    updateHealthBar(StaticHealthBarUpdate(id = 0, curHealth = health, maxHealth = stats.hitpoints.level))
    return false
}

fun Npc.hit(world: World, hitDamage: Int): Boolean {
    if (hitDamage >= health) {
        addHitMark(HitMark(HitMark.Color.RED, health, 0))
        health = 0
        updateHealthBar(StaticHealthBarUpdate(id = 0, curHealth = health, maxHealth = stats.health))
        cancelTasks(NormalTask)
        animate(deathSequence)
        addTask(NormalTask) {
            wait(sequence?.duration ?: 0)
            EventBus.schedule(NpcDiedEvent(this@hit, world))
        }
        return true
    }
    val hmColor = if (hitDamage == 0) HitMark.Color.BLUE else HitMark.Color.RED
    addHitMark(HitMark(hmColor, hitDamage, 0))
    health -= hitDamage
    updateHealthBar(StaticHealthBarUpdate(id = 0, curHealth = health, maxHealth = stats.health))
    return false
}