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
package io.guthix.oldscape.server.world.entity.interest

import io.guthix.oldscape.server.Property
import io.guthix.oldscape.server.PropertyHolder
import io.guthix.oldscape.server.template.InventoryIds
import io.guthix.oldscape.server.template.ObjTemplate
import io.guthix.oldscape.server.template.Template
import io.guthix.oldscape.server.template.TemplateNotFoundException
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

    @Transient
    override val properties: MutableMap<KProperty<*>, Any?> = mutableMapOf()
}

private val Obj.bodyGearTemplate: BodyGearTemplate
    get() = template.bodyGear ?: throw TemplateNotFoundException(id, BodyGearTemplate::class)

internal val ObjTemplate.bodyGear: BodyGearTemplate? by Property { null }


internal val Obj.isFullBody get() = bodyGearTemplate.isFullBody

@Serializable
data class BodyGearTemplate(
    override val ids: List<Int>,
    val isFullBody: Boolean
) : Template

internal val Obj.coversHair get() = headGearTemplate.coversHair

internal val Obj.coversFace get() = headGearTemplate.coversFace

private val Obj.headGearTemplate: HeadGearTemplate
    get() = template.headGear ?: throw TemplateNotFoundException(id, HeadGearTemplate::class)

internal val ObjTemplate.headGear: HeadGearTemplate? by Property { null }

@Serializable
data class HeadGearTemplate(
    override val ids: List<Int>,
    val coversHair: Boolean,
    val coversFace: Boolean
) : Template

internal val Obj.stance get() = weaponTemplate.stance

private val Obj.weaponTemplate: WeaponTemplate
    get() = template.weapon ?: throw TemplateNotFoundException(id, WeaponTemplate::class)

internal val ObjTemplate.weapon: WeaponTemplate? by Property { null }

@Serializable
data class WeaponTemplate(
    override val ids: List<Int>,
    val stance: StanceSequences
) : Template