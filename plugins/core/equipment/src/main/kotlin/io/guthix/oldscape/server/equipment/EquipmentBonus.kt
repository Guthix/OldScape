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
package io.guthix.oldscape.server.equipment

import io.guthix.oldscape.server.Property
import io.guthix.oldscape.server.template.*
import io.guthix.oldscape.server.world.entity.Obj
import io.guthix.oldscape.server.world.entity.interest.PlayerManager

var PlayerManager.EquipmentSet.attackBonus: StyleBonus by Property {
    val bonusTemplates: List<StyleBonus?> = equipment.values.map(Obj::attackBonus)
    StyleBonus(
        bonusTemplates.sumBy { it?.stab ?: 0 },
        bonusTemplates.sumBy { it?.slash ?: 0 },
        bonusTemplates.sumBy { it?.crush ?: 0 },
        bonusTemplates.sumBy { it?.range ?: 0 },
        bonusTemplates.sumBy { it?.magic ?: 0 }
    )
}

var PlayerManager.EquipmentSet.defenceBonus: StyleBonus by Property {
    val bonusTemplates = equipment.values.map(Obj::defenceBonus)
    StyleBonus(
        bonusTemplates.sumBy { it?.stab ?: 0 },
        bonusTemplates.sumBy { it?.slash ?: 0 },
        bonusTemplates.sumBy { it?.crush ?: 0 },
        bonusTemplates.sumBy { it?.range ?: 0 },
        bonusTemplates.sumBy { it?.magic ?: 0 }
    )
}

var PlayerManager.EquipmentSet.strengtBonus: CombatBonus by Property {
    val bonusTemplates = equipment.values.map(Obj::strengthBonus)
    CombatBonus(
        bonusTemplates.sumBy { it?.melee ?: 0 },
        bonusTemplates.sumBy { it?.range ?: 0 },
        bonusTemplates.sumBy { it?.magic ?: 0 }
    )
}

var PlayerManager.EquipmentSet.prayerBonus: Int by Property {
    equipment.values.sumBy { it.prayerBonus ?: 0 }
}

fun PlayerManager.EquipmentSet.updateBonuses(old: Obj?, new: Obj?) {
    removeBonuses(old)
    addBonuses(new)
}

private fun PlayerManager.EquipmentSet.addBonuses(obj: Obj?) {
    val bonusTemplates = obj?.equipmentTemplate
    attackBonus += bonusTemplates?.attackBonus
    defenceBonus += bonusTemplates?.defenceBonus
    strengtBonus += bonusTemplates?.strengthBonus
    prayerBonus += bonusTemplates?.prayerBonus ?: 0
}

private fun PlayerManager.EquipmentSet.removeBonuses(obj: Obj?) {
    val bonusTemplates = obj?.equipmentTemplate
    attackBonus -= bonusTemplates?.attackBonus
    defenceBonus -= bonusTemplates?.defenceBonus
    strengtBonus -= bonusTemplates?.strengthBonus
    prayerBonus -= bonusTemplates?.prayerBonus ?: 0
}