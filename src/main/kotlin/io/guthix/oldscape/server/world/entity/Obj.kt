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
import io.guthix.oldscape.server.blueprints.*
import mu.KotlinLogging
import java.io.IOException
import kotlin.reflect.KProperty

private val logger = KotlinLogging.logger { }

fun ObjectBlueprint.create(amount: Int): Obj = Obj(this, amount)

data class Obj(private val blueprint: ObjectBlueprint, var quantity: Int) : PropertyHolder {
    val id: Int get() = blueprint.id
    val name: String get() = blueprint.name
    val weight: Float get() = blueprint.weight
    val examines: String get() = blueprint.examines
    val isStackable: Boolean get() = blueprint.isStackable
    val isTradable: Boolean get() = blueprint.isTradable
    val notedId: Int? get() = blueprint.notedId
    val isNoted: Boolean get() = blueprint.isNoted
    val placeHolderId: Int? get() = blueprint.placeHolderId
    val isPlaceHolder: Boolean get() = blueprint.isPlaceHolder
    val interfaceOperations: Array<String?> get() = blueprint.interfaceOperations
    val groundOperations: Array<String?> get() = blueprint.groundOperations
    val equipmentType: EquipmentType? get() = blueprint.equipmentType
    val isFullBody: Boolean get() = blueprint.isFullBody ?: false
    val coversFace: Boolean get() = blueprint.coversFace ?: false
    val coversHair: Boolean get() = blueprint.coversHair ?: false
    val stanceSequences: StanceSequences? get() = blueprint.stanceSequences
    val attackBonus: StyleBonus? get() = blueprint.attackBonus
    val strengthBonus: CombatBonus? get() = blueprint.strengthBonus
    val defenceBonus: StyleBonus? get() = blueprint.defenceBonus
    val prayerBonus: Int? get() = blueprint.prayerBonus

    override val properties: MutableMap<KProperty<*>, Any?> = mutableMapOf()

    companion object {
        internal lateinit var blueprints: Map<Int, ObjectBlueprint>

        internal operator fun get(index: Int): ObjectBlueprint = blueprints[index] ?: throw IOException(
            "Could not find blueprint $index."
        )

        internal fun loadBlueprints(
            cConfigs: Map<Int, ObjectConfig>,
            eObjectConfigs: List<ExtraObjectConfig>,
        ) {
            blueprints = mutableMapOf<Int, ObjectBlueprint>().apply {
                addBlueprints(cConfigs, eObjectConfigs, ::ObjectBlueprint)
            }
            logger.info { "Loaded ${blueprints.size} object blueprints" }
        }

        private fun MutableMap<Int, ObjectBlueprint>.addBlueprints(
            cacheConfigs: Map<Int, ObjectConfig>,
            extraObjectConfigs: List<ExtraObjectConfig>,
            construct: (ObjectConfig, ExtraObjectConfig) -> ObjectBlueprint
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