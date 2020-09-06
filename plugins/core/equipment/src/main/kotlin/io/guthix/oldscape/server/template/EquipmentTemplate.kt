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
import io.guthix.oldscape.server.template.ObjTemplate
import io.guthix.oldscape.server.world.entity.Obj
import io.guthix.oldscape.server.world.entity.interest.PlayerManager

public val Obj.equipmentType: PlayerManager.EquipmentType? get() = equipmentTemplate?.type

public val Obj.attackBonus: StyleBonus? get() = equipmentTemplate?.attackBonus

public val Obj.strengthBonus: CombatBonus? get() = equipmentTemplate?.strengthBonus

public val Obj.defenceBonus: StyleBonus? get() = equipmentTemplate?.defenceBonus

public val Obj.prayerBonus: Int? get() = equipmentTemplate?.prayerBonus

internal val Obj.equipmentTemplate: EquipmentTemplate? get() = template.equipment

internal val ObjTemplate.equipment: EquipmentTemplate? by Property { null }

data class EquipmentTemplate(
    override val ids: List<Int>,
    val type: PlayerManager.EquipmentType?,
    val coversHair: Boolean?,
    val isFullBody: Boolean?,
    val coversFace: Boolean?,
    val attackBonus: StyleBonus?,
    val strengthBonus: CombatBonus?,
    val defenceBonus: StyleBonus?,
    val prayerBonus: Int?
) : Template(ids)

data class StyleBonus(
    var stab: Int,
    var slash: Int,
    var crush: Int,
    var range: Int,
    var magic: Int
) {
    operator fun plus(value: StyleBonus?): StyleBonus {
        if (value == null) return this
        stab += value.stab
        slash += value.slash
        crush += value.crush
        range += value.range
        magic += value.magic

        return this
    }

    operator fun minus(value: StyleBonus?): StyleBonus {
        if (value == null) return this
        stab -= value.stab
        slash -= value.slash
        crush -= value.crush
        range -= value.range
        magic -= value.magic
        return this
    }
}

data class CombatBonus(
    var melee: Int,
    var range: Int,
    var magic: Int
) {
    operator fun plus(value: CombatBonus?): CombatBonus {
        if (value == null) return this
        melee += value.melee
        range += value.range
        magic += value.magic
        return this
    }

    operator fun minus(value: CombatBonus?): CombatBonus {
        if (value == null) return this
        melee -= value.melee
        range -= value.range
        magic -= value.magic
        return this
    }
}