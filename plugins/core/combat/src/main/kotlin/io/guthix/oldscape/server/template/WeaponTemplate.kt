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
package io.guthix.oldscape.server.template

import io.guthix.oldscape.server.Property
import io.guthix.oldscape.server.world.entity.Obj
import io.guthix.oldscape.server.world.map.dim.TileUnit
import io.guthix.oldscape.server.world.map.dim.tiles

val Obj.weaponType: WeaponType get() = weaponTemplate.type

val Obj.baseAttackSpeed: Int get() = weaponTemplate.attackSpeed

val Obj.baseAttackRange: TileUnit
    get() = weaponTemplate.attackRange?.tiles ?: throw TemplateNotFoundException(
        id, Obj::baseAttackRange
    )

val Obj.attackAnim: SequenceTemplate get() = SequenceTemplates[weaponSequences.attack]

val Obj.blockAnim: SequenceTemplate get() = SequenceTemplates[weaponSequences.defence]

private val Obj.weaponSequences: WeaponSequences get() = weaponTemplate.weaponSequences
    ?: throw TemplateNotFoundException(id, Obj::weaponSequences)

private val Obj.weaponTemplate: WeaponTemplate get() = template.weapon ?: throw TemplateNotFoundException(
    id, WeaponTemplate::class
)

internal val ObjTemplate.weapon: WeaponTemplate? by Property { null }

data class WeaponTemplate(
    override val ids: List<Int>,
    val type: WeaponType,
    val attackSpeed: Int,
    val attackRange: Int?,
    val weaponSequences: WeaponSequences?,
) : Template(ids)

data class WeaponSequences(
    val attack: Int,
    val defence: Int
)

enum class AttackStyle(
    val attackBonus: Int = 0,
    val strengthBonus: Int = 0,
    val defenceBonus: Int = 0,
    val rangeBonus: Int = 0,
    val magicBonus: Int = 0,
    val attackSpeedBonus: Int = 0,
    val attackRangeBonus: Int = 0,
    val attackXp: Int = 0,
    val strengthXp: Int = 0,
    val defenceXp: Int = 0,
    val rangeXp: Int = 0,
    val magicXp: Int = 0
) {
    MELEE_ACCURATE(attackBonus = 3),
    MELEE_AGGRESSIVE(strengthBonus = 3),
    MELEE_DEFENSIVE(defenceBonus = 3),
    MELEE_CONTROLLED(attackBonus = 1, strengthBonus = 1, defenceBonus = 1),
    RANGE_ACCURATE(rangeBonus = 3),
    RANGE_RAPID(attackSpeedBonus = 1),
    RANGE_LONGRANGE(defenceBonus = 3, attackRangeBonus = 2),
    MAGIC_ACCURATE(magicBonus = 3),
    MAGIC_LONGRANGE(defenceBonus = 3, attackRangeBonus = 2),
    MAGIC_AUTOCAST,
    MAGIC_AUTOCAST_DEFENSIVE,
    NONE
}

data class CombatStyle(val attackType: AttackType, val style: AttackStyle)

enum class WeaponType(val id: Int, val styles: List<CombatStyle>) {
    UNARMED(
        id = 0,
        styles = listOf(
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_ACCURATE), // punch
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_AGGRESSIVE), // kick
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_DEFENSIVE) // block
        )
    ),
    AXE(
        id = 1,
        styles = listOf(
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_ACCURATE), // chop
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_AGGRESSIVE), // hack
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_AGGRESSIVE), // smash
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_DEFENSIVE) // block
        )
    ),
    BLUNT(
        id = 2,
        styles = listOf(
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_ACCURATE), // pound
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_AGGRESSIVE), // pummel
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_AGGRESSIVE) // block
        )
    ),
    BOW(
        id = 3,
        styles = listOf(
            CombatStyle(AttackType.RANGED, AttackStyle.RANGE_ACCURATE), // accurate
            CombatStyle(AttackType.RANGED, AttackStyle.RANGE_RAPID), // rapid
            CombatStyle(AttackType.RANGED, AttackStyle.RANGE_LONGRANGE) // long range
        )
    ),
    CLAW(
        id = 4,
        styles = listOf(
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_ACCURATE), // chop
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_AGGRESSIVE), // slash
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_CONTROLLED), // lunge
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_DEFENSIVE) // block
        )
    ),
    CROSSBOW(
        id = 5,
        styles = listOf(
            CombatStyle(AttackType.RANGED, AttackStyle.RANGE_ACCURATE), // accurate
            CombatStyle(AttackType.RANGED, AttackStyle.RANGE_RAPID), // rapid
            CombatStyle(AttackType.RANGED, AttackStyle.RANGE_LONGRANGE) // long range
        )
    ),
    SALAMANDER(
        id = 6,
        styles = listOf(
            CombatStyle(AttackType.SLASH, AttackStyle.NONE), // scorch
            CombatStyle(AttackType.RANGED, AttackStyle.NONE), // flare
            CombatStyle(AttackType.MAGIC, AttackStyle.NONE) // blaze
        )
    ),
    CHINCHOMPA(
        id = 7,
        styles = listOf(
            CombatStyle(AttackType.RANGED, AttackStyle.RANGE_ACCURATE), // short fuse
            CombatStyle(AttackType.RANGED, AttackStyle.RANGE_RAPID), // medium fuse
            CombatStyle(AttackType.RANGED, AttackStyle.RANGE_LONGRANGE) // long fuse
        )
    ),
    GUN(
        id = 8,
        styles = listOf(
            CombatStyle(AttackType.NONE, AttackStyle.NONE), // aim and fire
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_AGGRESSIVE) // kick
        )
    ),
    SLASHING_SWORD(
        id = 9,
        styles = listOf(
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_ACCURATE), // chop
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_AGGRESSIVE), // slash
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_CONTROLLED), // lunge
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_DEFENSIVE) // block
        )
    ),
    TWOHANDED_SWORD(
        id = 10,
        styles = listOf(
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_ACCURATE), // chop
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_AGGRESSIVE), // slash
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_AGGRESSIVE), // smash
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_DEFENSIVE) // block
        )
    ),
    PICKAXE(
        id = 11,
        styles = listOf(
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_ACCURATE), // spike
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_AGGRESSIVE), // impale
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_AGGRESSIVE), // smash
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_DEFENSIVE) // block
        )
    ),
    POLEARM(
        id = 12,
        styles = listOf(
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_CONTROLLED), // jab
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_AGGRESSIVE), // swipe
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_DEFENSIVE), // fend
        )
    ),
    POLESTAFF(
        id = 13,
        styles = listOf(
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_ACCURATE), // bash
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_AGGRESSIVE), // pound
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_DEFENSIVE) // block
        )
    ),
    SCYTHE(
        id = 14,
        styles = listOf(
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_ACCURATE), // reap
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_AGGRESSIVE), // chop
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_AGGRESSIVE), // job
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_DEFENSIVE) // block
        )
    ),
    SPEAR(
        id = 15,
        styles = listOf(
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_CONTROLLED), // lunge
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_CONTROLLED), // swipe
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_CONTROLLED), // pound
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_DEFENSIVE) // block
        )
    ),
    SPIKED_WEAPON(
        id = 16,
        styles = listOf(
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_ACCURATE), // pound
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_AGGRESSIVE), // pummel
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_CONTROLLED), // spike
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_DEFENSIVE) // block
        )
    ),
    STABBING_SWORD(
        id = 17,
        styles = listOf(
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_ACCURATE), // stab
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_AGGRESSIVE), // lunge
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_AGGRESSIVE), // slash
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_DEFENSIVE) // block
        )
    ),
    STAFF(
        id = 18,
        styles = listOf(
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_ACCURATE), // bash
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_AGGRESSIVE), // pound
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_DEFENSIVE), // focus
            CombatStyle(AttackType.MAGIC, AttackStyle.MAGIC_AUTOCAST), // spell
            CombatStyle(AttackType.MAGIC, AttackStyle.MAGIC_AUTOCAST_DEFENSIVE) // spell (defensive)
        )
    ),
    THROW_WEAPON(
        id = 19,
        styles = listOf(
            CombatStyle(AttackType.RANGED, AttackStyle.RANGE_ACCURATE), // accurate
            CombatStyle(AttackType.RANGED, AttackStyle.RANGE_RAPID), // rapid
            CombatStyle(AttackType.RANGED, AttackStyle.RANGE_LONGRANGE) // long range
        )
    ),
    WHIP(
        id = 20,
        styles = listOf(
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_ACCURATE), // flick
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_CONTROLLED), // lash
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_DEFENSIVE) // deflect
        )
    ),
    BLADED_STAFF(
        id = 21,
        styles = listOf(
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_ACCURATE), // jab
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_AGGRESSIVE), // swipe
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_DEFENSIVE), // fend
            CombatStyle(AttackType.MAGIC, AttackStyle.MAGIC_AUTOCAST), // spell
            CombatStyle(AttackType.MAGIC, AttackStyle.MAGIC_AUTOCAST_DEFENSIVE) // spell (defensive)
        )
    ),
    POWERED_STAFF(
        id = 23,
        styles = listOf(
            CombatStyle(AttackType.MAGIC, AttackStyle.MAGIC_ACCURATE), // accurate
            CombatStyle(AttackType.MAGIC, AttackStyle.MAGIC_ACCURATE), // accurate
            CombatStyle(AttackType.MAGIC, AttackStyle.MAGIC_LONGRANGE) // deflect
        )
    ),
    BANNER(
        id = 24,
        styles = listOf(
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_ACCURATE), // lunge
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_AGGRESSIVE), // swipe
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_AGGRESSIVE), // pound
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_DEFENSIVE), // block
        )
    ),
    BLUDGEON(
        id = 26,
        styles = listOf(
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_AGGRESSIVE), // pound
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_AGGRESSIVE), // pummel
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_AGGRESSIVE) // block
        )
    ),
    BULWARK(
        id = 27,
        styles = listOf(
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_ACCURATE), // pummel
            CombatStyle(AttackType.NONE, AttackStyle.NONE) // block
        )
    ),
    BLASTER( // only available in easter events
        id = 0,
        styles = listOf(
            CombatStyle(AttackType.NONE, AttackStyle.NONE), // explosive
            CombatStyle(AttackType.NONE, AttackStyle.NONE) // flamer
        )
    )
}