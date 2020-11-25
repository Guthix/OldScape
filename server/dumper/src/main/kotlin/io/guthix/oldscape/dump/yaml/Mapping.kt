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
package io.guthix.oldscape.dump.yaml

import io.guthix.oldscape.server.template.*
import io.guthix.oldscape.wiki.wikitext.NpcWikiDefinition
import io.guthix.oldscape.wiki.wikitext.ObjWikiDefinition

fun ObjWikiDefinition.toWeightTemplate(servert: ObjWeightTemplate?): ObjWeightTemplate = ObjWeightTemplate(
    ids!!,
    weight!!
)

fun ObjWikiDefinition.toEquipmentTemplate(serverT: EquipmentTemplate?): EquipmentTemplate = EquipmentTemplate(
    ids!!,
    slot.toEquipmentType(),
    coversHair = false, isFullBody = false, coversFace = false,
    StyleBonus(
        attBonusStab ?: 0,
        attBonusSlash ?: 0,
        attBonusCrush ?: 0,
        attBonusRange ?: 0,
        attBonusMagic ?: 0
    ), CombatBonus(strengthBonus ?: 0, rangeStrengthBonus ?: 0, magicDamageBonus ?: 0),
    StyleBonus(
        defBonusStab ?: 0,
        defBonusSlash ?: 0,
        defBonusCrush ?: 0,
        defBonusRange ?: 0,
        defBonusMagic ?: 0
    ),
    prayerBonus ?: 0
)

fun ObjWikiDefinition.toWeaponTemplate(serverT: WeaponTemplate?): WeaponTemplate = WeaponTemplate(
    ids!!,
    combatStyle.toWeaponType() ?: WeaponType.UNARMED,
    attackSpeed ?: 0,
    serverT?.attackRange,
    serverT?.weaponSequences
)

fun NpcWikiDefinition.toMonsterTemplate(serverT: MonsterTemplate?): MonsterTemplate = MonsterTemplate(
    ids!!,
    combatLvl ?: throw IllegalStateException("Combat lvl can't be null."),
    maxHit,
    attackStyles?.first().toAttackType(),
    isAggressive ?: false,
    isPoisonous ?: false,
    isImmuneToPoison ?: false,
    isImmuneToVenom ?: false,
    serverT?.attackSpeed,
    serverT?.sequences,
    CombatStats(
        hitPoints ?: 0,
        attackStat ?: 0,
        strengthStat ?: 0,
        defenceStat ?: 0,
        magicStat ?: 0,
        rangeStat ?: 0
    ),
    CombatBonus(
        attackBonusMelee ?: 0,
        attackBonusRange ?: 0,
        attackBonusMagic ?: 0,
    ),
    CombatBonus(
        strengthBonus ?: 0,
        rangeStrengthBonus ?: 0,
        magicStrengthBonus ?: 0
    ),
    StyleBonus(
        defBonusStab ?: 0,
        defBonusSlash ?: 0,
        defBonusCrush ?: 0,
        defBonusRange ?: 0,
        defBonusMagic ?: 0
    )
)