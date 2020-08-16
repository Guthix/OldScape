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
package io.guthix.oldscape.server.template.type

import io.guthix.oldscape.cache.config.ObjectConfig
import io.guthix.oldscape.server.PropertyHolder
import io.guthix.oldscape.server.template.EngineTemplate
import io.guthix.oldscape.server.template.EngineConfigTemplate
import mu.KotlinLogging
import kotlin.reflect.KProperty

private val logger = KotlinLogging.logger { }

data class ObjTemplate(
    private val config: ObjectConfig,
    private val engineTemplate: ObjEngineTemplate
) : PropertyHolder, EngineConfigTemplate(config, engineTemplate) {
    val id: Int get() = config.id
    val name: String get() = config.name
    val weight: Float get() = engineTemplate.weight
    val examines: String get() = engineTemplate.examine
    val isStackable: Boolean get() = config.stackable
    val isTradable: Boolean get() = config.tradable
    val notedId: Int? get() = config.notedId
    val isNoted: Boolean get() = config.isNoted
    val placeHolderId: Int? get() = config.placeholderId
    val isPlaceHolder: Boolean get() = config.isPlaceHolder
    val interfaceOperations: Array<String?> get() = config.iop
    val groundOperations: Array<String?> get() = config.groundActions
    val equipmentType: EquipmentType? get() = engineTemplate.equipmentType
    val isFullBody: Boolean? get() = engineTemplate.isFullBody
    val coversFace: Boolean? get() = engineTemplate.coversFace
    val coversHair: Boolean? get() = engineTemplate.coversHair
    val stanceSequences: StanceSequences? get() = engineTemplate.stanceSequences
    override val properties: MutableMap<KProperty<*>, Any?> = mutableMapOf()
}

data class ObjEngineTemplate(
    override val ids: List<Int>,
    val weight: Float,
    val examine: String,
    val equipment: EquipmentEngineTemplate?
) : EngineTemplate(ids) {
    val equipmentType: EquipmentType? get() = equipment?.type
    val isFullBody: Boolean? get() = equipment?.isFullBody
    val coversFace: Boolean? get() = equipment?.coversFace
    val coversHair: Boolean? get() = equipment?.coversHair
    val stanceSequences: StanceSequences? = equipment?.stanceSequences
}

enum class EquipmentType(val slot: Int) {
    HEAD(0), CAPE(1), NECK(2), ONE_HAND_WEAPON(3), TWO_HAND_WEAPON(3), BODY(4),
    SHIELD(5), LEGS(7), HANDS(9), FEET(10), RING(11), AMMUNITION(13)
}

data class EquipmentEngineTemplate(
    val type: EquipmentType,
    val isFullBody: Boolean?,
    val coversFace: Boolean?,
    val coversHair: Boolean?,
    val stanceSequences: StanceSequences?,
)

data class StanceSequences(
    val stand: Int,
    val turn: Int,
    val walk: Int,
    val turn180: Int,
    val turn90CW: Int,
    val turn90CCW: Int,
    var run: Int
)