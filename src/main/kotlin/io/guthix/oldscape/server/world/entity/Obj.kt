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
package io.guthix.oldscape.server.world.entity

import io.guthix.oldscape.server.PropertyHolder
import io.guthix.oldscape.server.template.type.EquipmentType
import io.guthix.oldscape.server.template.type.ObjTemplate
import io.guthix.oldscape.server.template.type.StanceSequences
import kotlin.reflect.KProperty


fun ObjTemplate.new(amount: Int): Obj = Obj(this, amount)

data class Obj(val template: ObjTemplate, var quantity: Int) : PropertyHolder {
    val id: Int get() = template.id
    val name: String get() = template.name
    val weight: Float get() = template.weight
    val examines: String get() = template.examines
    val isStackable: Boolean get() = template.isStackable
    val isTradable: Boolean get() = template.isTradable
    val notedId: Int? get() = template.notedId
    val isNoted: Boolean get() = template.isNoted
    val placeHolderId: Int? get() = template.placeHolderId
    val isPlaceHolder: Boolean get() = template.isPlaceHolder
    val interfaceOperations: Array<String?> get() = template.interfaceOperations
    val groundOperations: Array<String?> get() = template.groundOperations
    val equipmentType: EquipmentType? get() = template.equipmentType
    val isFullBody: Boolean get() = template.isFullBody ?: false
    val coversFace: Boolean get() = template.coversFace ?: false
    val coversHair: Boolean get() = template.coversHair ?: false
    val stanceSequences: StanceSequences? get() = template.stanceSequences

    override val properties: MutableMap<KProperty<*>, Any?> = mutableMapOf()
}