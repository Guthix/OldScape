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
package io.guthix.oldscape.wiki.yaml

import io.guthix.oldscape.server.blueprints.*
import io.guthix.oldscape.server.blueprints.equipment.*
import io.guthix.oldscape.wiki.wikitext.NpcWikiDefinition
import io.guthix.oldscape.wiki.wikitext.ObjectWikiDefinition
import mu.KotlinLogging

private val logger = KotlinLogging.logger {  }

fun getStyleMapping(str: String?): AttackType? = when {
    str == null -> null
    str.equals("stab", true) -> AttackType.STAB
    str.equals("slash", true) -> AttackType.SLASH
    str.equals("crush", true) -> AttackType.CRUSH
    str.equals("range", true) -> AttackType.RANGED
    str.equals("ranged", true) -> AttackType.RANGED
    str.equals("magic", true) -> AttackType.MAGIC
    else -> {
        logger.info { "Couldn't get attack style for $str" }
        null
    }
}

fun ObjectWikiDefinition.toExtraObjectConfig(): ExtraObjectConfig = ExtraObjectConfig(
    ids!!,
    weight ?: 0f,
    examine ?: ""
)

fun ObjectWikiDefinition.toExtraHeadConfig(curExtraConfig: ExtraHeadConfig?): ExtraHeadConfig {
    val equipmentConfig = toExtraEquipmentConfig()
    return ExtraHeadConfig(
        equipmentConfig.ids,
        equipmentConfig.weight,
        equipmentConfig.examine,
        curExtraConfig?.coversFace ?: false,
        curExtraConfig?.coversHair ?: false,
        equipmentConfig.equipment
    )
}

fun ObjectWikiDefinition.toExtraBodyConfig(curExtraConfig: ExtraBodyConfig?): ExtraBodyConfig {
    val equipmentConfig = toExtraEquipmentConfig()
    return ExtraBodyConfig(
        equipmentConfig.ids,
        equipmentConfig.weight,
        equipmentConfig.examine,
        curExtraConfig?.isFullBody ?: false,
        equipmentConfig.equipment
    )
}

fun ObjectWikiDefinition.toExtraWeaponConfig(curExtraConfig: ExtraWeaponConfig?): ExtraWeaponConfig {
    val equipmentConfig = toExtraEquipmentConfig()
    return ExtraWeaponConfig(
        equipmentConfig.ids,
        equipmentConfig.weight,
        equipmentConfig.examine,
        findWeaponType(combatStyle),
        attackSpeed ?: 0,
        curExtraConfig?.attackRange ?: 1,
        equipmentConfig.equipment
    )
}

fun findWeaponType(str: String?): WeaponType = when {
    str.equals("axe", ignoreCase = true) -> WeaponType.AXE
    str.equals("bludegon", ignoreCase = true) -> WeaponType.BLUDGEON
    str.equals("blunt", ignoreCase = true) -> WeaponType.BLUNT
    str.equals("bulwark", ignoreCase = true) -> WeaponType.BULWARK
    str.equals("claws", ignoreCase = true) -> WeaponType.CLAW
    str.equals("polearm", ignoreCase = true) -> WeaponType.POLEARM
    str.equals("pickaxe", ignoreCase = true) -> WeaponType.PICKAXE
    str.equals("scythe", ignoreCase = true) -> WeaponType.SCYTHE
    str.equals("hacksword", ignoreCase = true) -> WeaponType.SLASHING_SWORD
    str.equals("spear", ignoreCase = true) -> WeaponType.SPEAR
    str.equals("spiked", ignoreCase = true) -> WeaponType.SPIKED_WEAPON
    str.equals("stabsword", ignoreCase = true) -> WeaponType.STABBING_SWORD
    str.equals("heavysword", ignoreCase = true) -> WeaponType.TWOHANDED_SWORD
    str.equals("whip", ignoreCase = true) -> WeaponType.WHIP
    str.equals("bow", ignoreCase = true) -> WeaponType.BOW
    str.equals("grenade", ignoreCase = true) -> WeaponType.CHINCHOMPA
    str.equals("crossbow", ignoreCase = true) -> WeaponType.CROSSBOW
    str.equals("thrown", ignoreCase = true) -> WeaponType.THROW_WEAPON
    str.equals("staff", ignoreCase = true) -> WeaponType.STAFF
    str.equals("staff bladed", ignoreCase = true) -> WeaponType.BLADED_STAFF
    str.equals("staff selfpowering", ignoreCase = true) -> WeaponType.POWERED_STAFF
    str.equals("banner", ignoreCase = true) -> WeaponType.BANNER
    str.equals("blaster", ignoreCase = true) -> WeaponType.BLASTER
    str.equals("gun", ignoreCase = true) -> WeaponType.GUN
    str.equals("polestaff", ignoreCase = true) -> WeaponType.POLESTAFF
    str.equals("flamer", ignoreCase = true) -> WeaponType.SALAMANDER
    str.equals("unarmed", ignoreCase = true) -> WeaponType.UNARMED
    else -> WeaponType.UNARMED
}

fun ObjectWikiDefinition.toExtraEquipmentConfig(): ExtraEquipmentConfig {
    if(slot == null) throw IllegalStateException("Unmapped slot for id $ids found: $slot.")
    val equipment = EquipmentBlueprint.Equipment(
        StyleBonus(
            attBonusStab ?: 0,
            attBonusSlash ?: 0,
            attBonusCrush ?: 0,
            attBonusRange ?: 0,
            attBonusMagic ?: 0
        ),
        StyleBonus(
            defBonusStab ?: 0,
            defBonusSlash?: 0,
            defBonusCrush ?: 0,
            defBonusRange ?: 0,
            defBonusMagic ?: 0
        ),
        CombatBonus(
            strengthBonus ?: 0,
            rangeStrengthBonus ?: 0,
            magicDamageBonus ?: 0
        ),
        prayerBonus ?: 0
    )

    return ExtraEquipmentConfig(
        ids!!,
        weight ?: 0f,
        examine ?: "",
        equipment
    )
}

fun NpcWikiDefinition.toExtraNpcConfig(curExtraConfig: ExtraNpcConfig?): ExtraNpcConfig {
    val combat = if(combatLvl != null) {
        NpcCombat(
            combatLvl ?: throw IllegalStateException("Combat lvl can't be null."),
            maxHit,
            getStyleMapping(attackStyles?.first()),
            isAggressive ?: false,
            isPoisonous ?: false,
            isImmuneToPoison ?: false,
            isImmuneToVenom ?: false,
            curExtraConfig?.combat?.attackSpeed,
            curExtraConfig?.combat?.sequences,
            CombatStats(
                hitPoints ?: 0,
                attackStat ?: 0,
                strengthStat ?: 0,
                defenceStat ?: 0,
                magicStat ?: 0,
                rangeStat ?: 0
            ),
            NpcAttackStats(
                CombatBonus(
                    attackBonusMelee ?: 0,
                    attackBonusRange ?: 0,
                    attackBonusMagic ?: 0,
                ),
                CombatBonus(
                    strengthBonus ?: 0,
                    rangeStrengthBonus ?: 0,
                    magicStrengthBonus ?: 0
                )
            ),
            StyleBonus(
                defBonusStab ?: 0,
                defBonusSlash ?: 0,
                defBonusCrush ?: 0,
                defBonusRange ?: 0,
                defBonusMagic ?: 0
            )
        )
    } else null
    return ExtraNpcConfig(
        ids!!,
        examine ?: "",
        curExtraConfig?.wanderRadius,
        combat
    )
}