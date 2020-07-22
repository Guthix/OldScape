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
package io.guthix.oldscape.server.api

import io.guthix.oldscape.cache.config.ObjectConfig
import io.guthix.oldscape.server.blueprints.ExtraObjectConfig
import io.guthix.oldscape.server.blueprints.ObjectBlueprint
import io.guthix.oldscape.server.blueprints.equipment.*
import mu.KotlinLogging
import java.io.IOException

private val logger = KotlinLogging.logger { }

object ObjectBlueprints {
    lateinit var blueprints: Map<Int, ObjectBlueprint>

    inline operator fun <reified T : ObjectBlueprint> get(index: Int): T {
        val bp = blueprints[index] ?: throw IOException("Could not find blueprint $index.")
        if (bp !is T) {
            throw TypeCastException("")
        }
        return bp
    }

    fun load(
        cConfigs: Map<Int, ObjectConfig>,
        eObjectConfigs: List<ExtraObjectConfig>,
        eHeadConfigs: List<ExtraHeadConfig>,
        extraCapeConfigs: List<ExtraEquipmentConfig>,
        eNeckConfigs: List<ExtraEquipmentConfig>,
        eAmmunitionConfigs: List<ExtraAmmunitionConfig>,
        eWeaponConfigs: List<ExtraWeaponConfig>,
        eTwoHandConfigs: List<ExtraWeaponConfig>,
        eShieldConfigs: List<ExtraEquipmentConfig>,
        eBodyConfigs: List<ExtraBodyConfig>,
        eLegConfigs: List<ExtraEquipmentConfig>,
        eHandConfigs: List<ExtraEquipmentConfig>,
        eFeetConfigs: List<ExtraEquipmentConfig>,
        eRingConfigs: List<ExtraEquipmentConfig>
    ) {
        blueprints = mutableMapOf<Int, ObjectBlueprint>().apply {
            addBlueprints(cConfigs, eObjectConfigs, ::ObjectBlueprint)
            addBlueprints(cConfigs, eHeadConfigs, ::HeadBlueprint)
            addBlueprints(cConfigs, extraCapeConfigs, ::CapeBlueprint)
            addBlueprints(cConfigs, eNeckConfigs, ::NeckBlueprint)
            addBlueprints(cConfigs, eAmmunitionConfigs, ::AmmunitionBlueprint)
            addBlueprints(cConfigs, eWeaponConfigs, ::WeaponBlueprint)
            addBlueprints(cConfigs, eTwoHandConfigs, ::TwoHandBlueprint)
            addBlueprints(cConfigs, eShieldConfigs, ::ShieldBlueprint)
            addBlueprints(cConfigs, eBodyConfigs, ::BodyBlueprint)
            addBlueprints(cConfigs, eLegConfigs, ::LegsBlueprint)
            addBlueprints(cConfigs, eHandConfigs, ::HandsBlueprint)
            addBlueprints(cConfigs, eFeetConfigs, ::FeetBlueprint)
            addBlueprints(cConfigs, eRingConfigs, ::RingBlueprint)
        }
        logger.info { "Loaded ${blueprints.size} object blueprints" }
    }

    private fun <E : ExtraObjectConfig, B : ObjectBlueprint> MutableMap<Int, ObjectBlueprint>.addBlueprints(
        cacheConfigs: Map<Int, ObjectConfig>,
        extraObjectConfigs: List<E>,
        construct: (ObjectConfig, E) -> B
    ) {
        extraObjectConfigs.forEach { extraConfig ->
            extraConfig.ids.forEach inner@{ id ->
                val cacheConfig = cacheConfigs[id] ?: kotlin.run {
                    logger.warn { "Could not find object config for id $id." }
                    return@inner
                }
                put(id, construct.invoke(cacheConfig, extraConfig))
            }
        }
    }
}