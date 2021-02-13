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
package io.guthix.oldscape.server.core.equipment

import io.guthix.oldscape.server.Property
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.core.equipment.event.ObjEquipedEvent
import io.guthix.oldscape.server.core.equipment.event.ObjUnEquipedEvent
import io.guthix.oldscape.server.core.equipment.template.*
import io.guthix.oldscape.server.core.equipment.template.equipment
import io.guthix.oldscape.server.core.stat.CombatBonus
import io.guthix.oldscape.server.core.stat.StyleBonus
import io.guthix.oldscape.server.plugin.invalidMessage
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Obj
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.entity.interest.EquipmentManager
import io.guthix.oldscape.server.world.entity.interest.EquipmentType

fun Player.equip(world: World, obj: Obj) {
    obj.template.equipment?.let { (_, nType) ->
        nType?.let { type ->
            val old = equipment[type.slot]
            equipment[type.slot] = obj
            equipment.updateBonuses(old, obj)
            old?.let { itemBag.add(old) }
            updateAppearance()
            EventBus.schedule(ObjEquipedEvent(obj, this, world))
        }
    } ?: invalidMessage("No equipment defined for $obj.")
}

internal val buttonToSlots = mapOf(
    14 to EquipmentType.HEAD,
    15 to EquipmentType.CAPE,
    16 to EquipmentType.NECK,
    17 to EquipmentType.ONE_HAND_WEAPON,
    18 to EquipmentType.BODY,
    19 to EquipmentType.SHIELD,
    20 to EquipmentType.LEGS,
    21 to EquipmentType.HANDS,
    22 to EquipmentType.FEET,
    23 to EquipmentType.RING,
    24 to EquipmentType.AMMUNITION,
)

fun Player.unequip(equipmentType: EquipmentType, world: World): Obj? {
    val obj = equipment.removeFromSlot(equipmentType.slot) ?: return null
    equipment.removeBonuses(obj)
    updateAppearance()
    EventBus.schedule(ObjUnEquipedEvent(obj, this, world))
    return obj
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