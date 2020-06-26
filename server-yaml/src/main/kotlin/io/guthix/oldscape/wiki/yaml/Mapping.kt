/**
 * This file is part of Guthix OldScape-Wiki.
 *
 * Guthix OldScape-Wiki is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape-Wiki is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.wiki.yaml

import io.guthix.oldscape.server.blueprints.*
import io.guthix.oldscape.server.blueprints.equipment.EquipmentBlueprint
import io.guthix.oldscape.server.blueprints.equipment.ExtraBodyConfig
import io.guthix.oldscape.server.blueprints.equipment.ExtraEquipmentConfig
import io.guthix.oldscape.server.blueprints.equipment.ExtraHeadConfig
import io.guthix.oldscape.wiki.wikitext.NpcWikiDefinition
import io.guthix.oldscape.wiki.wikitext.ObjectWikiDefinition
import mu.KotlinLogging

private val logger = KotlinLogging.logger {  }

fun getStyleMapping(str: String?): AttackStyle? = when {
    str == null -> null
    str.equals("stab", true) -> AttackStyle.STAB
    str.equals("slash", true) -> AttackStyle.SLASH
    str.equals("crush", true) -> AttackStyle.CRUSH
    str.equals("range", true) -> AttackStyle.RANGED
    str.equals("ranged", true) -> AttackStyle.RANGED
    str.equals("magic", true) -> AttackStyle.MAGIC
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