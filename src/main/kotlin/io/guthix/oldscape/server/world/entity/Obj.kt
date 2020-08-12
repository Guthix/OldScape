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

import io.guthix.oldscape.cache.config.ObjectConfig
import io.guthix.oldscape.server.PropertyHolder
import io.guthix.oldscape.server.template.*
import mu.KotlinLogging
import java.io.IOException
import kotlin.reflect.KProperty

private val logger = KotlinLogging.logger { }

fun ObjectTemplate.create(amount: Int): Obj = Obj(this, amount)

data class Obj(val template: ObjectTemplate, var quantity: Int) : PropertyHolder {
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

    companion object {
        lateinit var templates: Map<Int, ObjectTemplate>

        operator fun get(index: Int): ObjectTemplate = templates[index] ?: throw IOException(
            "Could not find blueprint $index."
        )

        internal fun loadTemplates(
            cConfigs: Map<Int, ObjectConfig>,
            eObjectConfigs: List<ObjectEngineTemplate>,
        ) {
            templates = mutableMapOf<Int, ObjectTemplate>().apply {
                addBlueprints(cConfigs, eObjectConfigs, ::ObjectTemplate)
            }
            logger.info { "Loaded ${templates.size} object blueprints" }
        }

        private fun MutableMap<Int, ObjectTemplate>.addBlueprints(
            cacheConfigs: Map<Int, ObjectConfig>,
            extraObjectConfigs: List<ObjectEngineTemplate>,
            construct: (ObjectConfig, ObjectEngineTemplate) -> ObjectTemplate
        ) {
            extraObjectConfigs.forEach { extraConfig ->
                extraConfig.ids.forEach inner@{ id ->
                    val cacheConfig = cacheConfigs[id] ?: kotlin.run {
                        logger.warn { "Could not find object config for id $id" }
                        return@inner
                    }
                    put(id, construct.invoke(cacheConfig, extraConfig))
                }
            }
        }
    }
}