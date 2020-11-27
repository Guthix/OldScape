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
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.event.ObjEquipedEvent
import io.guthix.oldscape.server.plugin.InvalidMessageException
import io.guthix.oldscape.server.template.*
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Obj
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.entity.interest.EquipmentManager

fun Player.equip(world: World, obj: Obj) {
    obj.template.equipment?.let { objEquipment ->
        objEquipment.type?.let { type ->
            val old = equipment[type.slot]
            equipment[type.slot] = obj
            objEquipment.coversFace?.let { equipment.coversFace = it }
            objEquipment.coversHair?.let { equipment.coversFace = it }
            objEquipment.isFullBody?.let { equipment.coversFace = it }
            equipment.updateBonuses(old, obj)
            old?.let { itemBag.add(old) }
            updateAppearance()
            EventBus.schedule(ObjEquipedEvent(obj, this, world))
        }
    } ?: throw InvalidMessageException("No equipment defined for $obj.")
}

var EquipmentManager.attackBonus: StyleBonus by Property {
    val bonusTemplates = objs.map { it?.attackBonus }
    StyleBonus(
        bonusTemplates.sumBy { it?.stab ?: 0 },
        bonusTemplates.sumBy { it?.slash ?: 0 },
        bonusTemplates.sumBy { it?.crush ?: 0 },
        bonusTemplates.sumBy { it?.range ?: 0 },
        bonusTemplates.sumBy { it?.magic ?: 0 }
    )
}

var EquipmentManager.defenceBonus: StyleBonus by Property {
    val bonusTemplates = objs.map { it?.defenceBonus }
    StyleBonus(
        bonusTemplates.sumBy { it?.stab ?: 0 },
        bonusTemplates.sumBy { it?.slash ?: 0 },
        bonusTemplates.sumBy { it?.crush ?: 0 },
        bonusTemplates.sumBy { it?.range ?: 0 },
        bonusTemplates.sumBy { it?.magic ?: 0 }
    )
}

var EquipmentManager.strengtBonus: CombatBonus by Property {
    val bonusTemplates = objs.map { it?.strengthBonus }
    CombatBonus(
        bonusTemplates.sumBy { it?.melee ?: 0 },
        bonusTemplates.sumBy { it?.range ?: 0 },
        bonusTemplates.sumBy { it?.magic ?: 0 }
    )
}

var EquipmentManager.prayerBonus: Int by Property { objs.sumBy { it?.prayerBonus ?: 0 } }

fun EquipmentManager.updateBonuses(old: Obj?, new: Obj?) {
    removeBonuses(old)
    addBonuses(new)
}

private fun EquipmentManager.addBonuses(obj: Obj?) {
    val bonusTemplates = obj?.equipmentTemplate
    attackBonus += bonusTemplates?.attackBonus
    defenceBonus += bonusTemplates?.defenceBonus
    strengtBonus += bonusTemplates?.strengthBonus
    prayerBonus += bonusTemplates?.prayerBonus ?: 0
}

private fun EquipmentManager.removeBonuses(obj: Obj?) {
    val bonusTemplates = obj?.equipmentTemplate
    attackBonus -= bonusTemplates?.attackBonus
    defenceBonus -= bonusTemplates?.defenceBonus
    strengtBonus -= bonusTemplates?.strengthBonus
    prayerBonus -= bonusTemplates?.prayerBonus ?: 0
}