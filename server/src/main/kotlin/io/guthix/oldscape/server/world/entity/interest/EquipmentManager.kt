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
package io.guthix.oldscape.server.world.entity.interest

import io.guthix.oldscape.server.PropertyHolder
import io.guthix.oldscape.server.template.InventoryIds
import io.guthix.oldscape.server.world.entity.Obj
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.reflect.KProperty

enum class EquipmentType(val slot: Int) {
    HEAD(0), CAPE(1), NECK(2), ONE_HAND_WEAPON(3), TWO_HAND_WEAPON(3), BODY(4),
    SHIELD(5), ARMS(6), LEGS(7), HAIR(8), HANDS(9), FEET(10), BEARD(11), RING(12), AMMUNITION(13)
}

@Serializable
class EquipmentManager : InventoryManager(InventoryIds.EQUIPMENT_94), PropertyHolder {
    val head: Obj? get() = objs[EquipmentType.HEAD.slot]
    val cape: Obj? get() = objs[EquipmentType.CAPE.slot]
    val neck: Obj? get() = objs[EquipmentType.NECK.slot]
    val weapon: Obj? get() = objs[EquipmentType.ONE_HAND_WEAPON.slot]
    val body: Obj? get() = objs[EquipmentType.BODY.slot]
    val shield: Obj? get() = objs[EquipmentType.SHIELD.slot]
    val legs: Obj? get() = objs[EquipmentType.LEGS.slot]
    val hands: Obj? get() = objs[EquipmentType.HANDS.slot]
    val feet: Obj? get() = objs[EquipmentType.FEET.slot]
    val ring: Obj? get() = objs[EquipmentType.RING.slot]
    val ammunition: Obj? get() = objs[EquipmentType.AMMUNITION.slot]

    var coversHair: Boolean = false
    var isFullBody: Boolean = false
    var coversFace: Boolean = false

    @Transient
    override val properties: MutableMap<KProperty<*>, Any?> = mutableMapOf()
}