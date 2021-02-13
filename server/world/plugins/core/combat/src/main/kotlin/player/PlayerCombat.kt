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
package io.guthix.oldscape.server.core.combat.player

import io.guthix.oldscape.dim.TileUnit
import io.guthix.oldscape.dim.max
import io.guthix.oldscape.dim.tiles
import io.guthix.oldscape.server.PersistentProperty
import io.guthix.oldscape.server.Property
import io.guthix.oldscape.server.core.combat.CombatSpell
import io.guthix.oldscape.server.core.equipment.template.*
import io.guthix.oldscape.server.core.stat.AttackType
import io.guthix.oldscape.server.template.SequenceIds
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Npc
import io.guthix.oldscape.server.world.entity.Player

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

val Player.currentMagicSpell: CombatSpell? by Property { null }

fun Player.attackNpc(npc: Npc, world: World): Unit = when {
    currentStyle.attackType == AttackType.RANGED -> startRangeAttack(npc, world)
    currentStyle.attackType == AttackType.MAGIC && currentMagicSpell != null -> {
        startMagicAttack(npc, world, currentMagicSpell!!)
    }
    else -> startMeleeAttack(npc, world)
}