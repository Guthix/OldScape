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
package io.guthix.oldscape.server.combat.player

import io.guthix.oldscape.dim.TileUnit
import io.guthix.oldscape.dim.max
import io.guthix.oldscape.dim.tiles
import io.guthix.oldscape.server.PersistentProperty
import io.guthix.oldscape.server.Property
import io.guthix.oldscape.server.combat.CombatSpell
import io.guthix.oldscape.server.combat.dmg.maxMagicHit
import io.guthix.oldscape.server.combat.dmg.maxMeleeHit
import io.guthix.oldscape.server.combat.dmg.maxRangeHit
import io.guthix.oldscape.server.combat.inCombatWith
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.event.NpcHitByPlayerEvent
import io.guthix.oldscape.server.pathing.DestinationRange
import io.guthix.oldscape.server.pathing.DestinationRectangleDirect
import io.guthix.oldscape.server.pathing.breadthFirstSearch
import io.guthix.oldscape.server.pathing.simplePathSearch
import io.guthix.oldscape.server.stat.AttackType
import io.guthix.oldscape.server.task.NormalTask
import io.guthix.oldscape.server.template.*
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Npc
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.entity.SpotAnimation
import io.guthix.oldscape.server.world.entity.interest.EquipmentType
import kotlin.random.Random

val Player.selectedTypes: IntArray by Property {
    IntArray(WeaponType.values().size)
}

val Player.currentStyle: CombatStyle
    get() {
        val weaponType = equipment.weapon?.weaponType ?: WeaponType.UNARMED
        val index = selectedTypes[weaponType.ordinal]
        return weaponType.styles[index]
    }

val Player.attackSpeed: Int get() = equipment.weapon?.baseAttackSpeed?.plus(currentStyle.style.attackSpeedBonus) ?: 4

val Player.attackRange: TileUnit
    get() = max(
        10.tiles, equipment.weapon?.baseAttackRange?.plus(currentStyle.style.attackRangeBonus.tiles) ?: 1.tiles
    )

val Player.attackSequence: Int by Property {
    equipment.weapon?.attackAnim ?: SequenceIds.PUNCH_422
}

val Player.defenceSequence: Int by Property {
    equipment.weapon?.blockAnim ?: SequenceIds.BLOCK_424
}

var Player.autoRetaliate: Boolean by PersistentProperty {
    true
}

fun Player.attackNpc(npc: Npc, world: World): Unit = when (currentStyle.attackType) {
    AttackType.RANGED -> startRangeAttack(npc, world)
    AttackType.MAGIC -> startMagicAttack(npc, world, CombatSpell.WIND_STRIKE)
    else -> startMeleeAttack(npc, world)
}

internal fun Player.startMeleeAttack(npc: Npc, world: World) {
    cancelTasks(NormalTask)
    var npcDestination = DestinationRectangleDirect(npc, world)
    path = breadthFirstSearch(pos, npcDestination, size, findAlternative = true, world)
    addTask(NormalTask) {
        inCombatWith = npc
        while (true) { // start player combat
            wait { npcDestination.reached(pos.x, pos.y, size) }
            animate(attackSequence)
            EventBus.schedule(NpcHitByPlayerEvent(this@startMeleeAttack, npc, world, maxMeleeHit()))
            wait(ticks = attackSpeed)
        }
    }.finalize {
        inCombatWith = null
    }
    addTask(NormalTask) {
        inCombatWith = npc
        turnToLock(npc)
        wait { npcDestination.reached(pos.x, pos.y, size) }
        while (true) {
            wait { npc.lastPos != npc.pos }
            npcDestination = DestinationRectangleDirect(npc, world)
            path = simplePathSearch(pos, npcDestination, size, world)
            wait(ticks = 1)
        }
    }.finalize {
        turnToLock(null)
    }
}

internal fun Player.startRangeAttack(npc: Npc, world: World) {
    cancelTasks(NormalTask)
    var npcDestination = DestinationRange(npc, attackRange, world)
    path = breadthFirstSearch(pos, npcDestination, size, true, world)
    addTask(NormalTask) {
        inCombatWith = npc
        main@ while (true) { // start player combat
            wait { npcDestination.reached(pos.x, pos.y, size) }
            val ammunition = equipment.ammunition
            if (ammunition == null || ammunition.quantity <= 0) {
                senGameMessage("There is no ammo left in your quiver.")
                cancel()
                break@main
            }
            equipment[EquipmentType.AMMUNITION.slot] = ammunition.apply { quantity-- }
            animate(attackSequence)
            spotAnimate(ammunition.drawBackSpotAnim, ammunition.drawBackSpotAnimHeight)
            val projectile = world.addProjectile(ammunition.ammunitionProjectile, pos, npc)
            world.addTask(NormalTask) { // projectile task
                val oldNpcPos = npc.pos
                wait(ticks = projectile.lifeTimeServerTicks - 1)
                if (Random.nextDouble(1.0) < 0.8) world.addObject(ammunition.copy(quantity = 1), oldNpcPos)
                EventBus.schedule(NpcHitByPlayerEvent(this@startRangeAttack, npc, world, maxRangeHit()))
            }
            wait(ticks = attackSpeed)
        }
    }.finalize {
        inCombatWith = null
    }
    addTask(NormalTask) {
        turnToLock(npc)
        wait { npcDestination.reached(pos.x, pos.y, size) }
        while (true) {
            wait { npc.lastPos != npc.pos }
            npcDestination = DestinationRange(npc, attackRange, world)
            path = simplePathSearch(pos, npcDestination, size, world)
            wait(ticks = 1)
        }
    }.finalize {
        turnToLock(null)
    }
}

val spashAnimation: SpotAnimation = SpotAnimation(SpotAnimIds.SPLASH_85, height = 123)

internal fun Player.magicAttack(
    npc: Npc,
    world: World,
    spellTemplate: CombatSpell
) {
    cancelTasks(NormalTask)
    val npcDestination = DestinationRange(npc, attackRange, world)
    path = breadthFirstSearch(pos, npcDestination, size, true, world)
    addTask(NormalTask) {
        wait { npcDestination.reached(pos.x, pos.y, size) }
        animate(spellTemplate.castAnim)
        spotAnimate(spellTemplate.castSpotAnim)
        val projectile = world.addProjectile(spellTemplate.projectile, pos, npc)
        world.addTask(NormalTask) {
            wait(ticks = projectile.lifeTimeServerTicks - 1)
            EventBus.schedule(NpcHitByPlayerEvent(
                this@magicAttack,
                npc,
                world,
                maxMagicHit(spellTemplate.hit(world, this@magicAttack, npc)),
                spotAnimOnSuccess = spellTemplate.impactSpotAnim,
                spotAnimOnFail = spashAnimation
            ))
        }
        wait(ticks = attackSpeed)
    }.finalize {
        inCombatWith = null
    }
    addTask(NormalTask) {
        wait { npcDestination.reached(pos.x, pos.y, size) }
        turnTo(npc)
    }
}

internal fun Player.startMagicAttack(
    npc: Npc,
    world: World,
    spellTemplate: CombatSpell
) {
    cancelTasks(NormalTask)
    var npcDestination = DestinationRange(npc, attackRange, world)
    path = breadthFirstSearch(pos, npcDestination, size, true, world)
    addTask(NormalTask) {
        inCombatWith = npc
        while (true) { // start player combat
            wait { npcDestination.reached(pos.x, pos.y, size) }
            animate(spellTemplate.castAnim)
            spotAnimate(spellTemplate.castSpotAnim)
            val projectile = world.addProjectile(spellTemplate.projectile, pos, npc)
            world.addTask(NormalTask) {
                wait(ticks = projectile.lifeTimeServerTicks - 1)
                EventBus.schedule(NpcHitByPlayerEvent(
                    this@startMagicAttack,
                    npc,
                    world,
                    maxMagicHit(spellTemplate.hit(world, this@startMagicAttack, npc)),
                    spotAnimOnSuccess = spellTemplate.impactSpotAnim,
                    spotAnimOnFail = spashAnimation
                ))
            }
            wait(ticks = attackSpeed)
        }
    }.finalize {
        inCombatWith = null
    }
    addTask(NormalTask) {
        turnToLock(npc)
        wait { npcDestination.reached(pos.x, pos.y, size) }
        while (true) {
            wait { npc.lastPos != npc.pos }
            npcDestination = DestinationRange(npc, attackRange, world)
            path = simplePathSearch(pos, npcDestination, size, world)
            wait(ticks = 1)
        }
    }.finalize {
        turnToLock(null)
    }
}