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

import io.guthix.oldscape.cache.config.ObjectConfig
import io.guthix.oldscape.server.PropertyHolder
import kotlin.reflect.KProperty

enum class EquipmentType(val slot: Int) {
    HEAD(0), CAPE(1), NECK(2), ONE_HAND_WEAPON(3), TWO_HAND_WEAPON(3), BODY(4),
    SHIELD(5), LEGS(7), HANDS(9), FEET(10), RING(11), AMMUNITION(13)
}

data class ObjectTemplate(
    private val cacheConfig: ObjectConfig,
    private val engineTemplate: ObjectEngineTemplate
) : PropertyHolder {
    val id: Int get() = cacheConfig.id
    val name: String get() = cacheConfig.name
    val weight: Float get() = engineTemplate.weight
    val examines: String get() = engineTemplate.examine
    val isStackable: Boolean get() = cacheConfig.stackable
    val isTradable: Boolean get() = cacheConfig.tradable
    val notedId: Int? get() = cacheConfig.notedId
    val isNoted: Boolean get() = cacheConfig.isNoted
    val placeHolderId: Int? get() = cacheConfig.placeholderId
    val isPlaceHolder: Boolean get() = cacheConfig.isPlaceHolder
    val interfaceOperations: Array<String?> get() = cacheConfig.iop
    val groundOperations: Array<String?> get() = cacheConfig.groundActions
    val equipmentType: EquipmentType? get() = engineTemplate.equipmentType
    val isFullBody: Boolean? get() = engineTemplate.isFullBody
    val coversFace: Boolean? get() = engineTemplate.coversFace
    val coversHair: Boolean? get() = engineTemplate.coversHair
    val stanceSequences: StanceSequences? get() = engineTemplate.stanceSequences
    override val properties: MutableMap<KProperty<*>, Any?> = mutableMapOf()
}

data class ObjectEngineTemplate(
    val ids: List<Int>,
    val weight: Float,
    val examine: String,
    val equipment: EquipmentEngineTemplate?
) {
    val equipmentType: EquipmentType? get() = equipment?.type
    val isFullBody: Boolean? get() = equipment?.isFullBody
    val coversFace: Boolean? get() = equipment?.coversFace
    val coversHair: Boolean? get() = equipment?.coversHair
    val stanceSequences: StanceSequences? = equipment?.stanceSequences
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