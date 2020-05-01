/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.api.blueprint

import io.guthix.oldscape.cache.config.ObjectConfig
import io.guthix.oldscape.server.blueprints.ExtraObjectConfig
import io.guthix.oldscape.server.blueprints.ObjectBlueprint
import io.guthix.oldscape.server.blueprints.equipment.*
import mu.KotlinLogging
import java.io.FileNotFoundException
import java.io.IOException

private val logger = KotlinLogging.logger {  }

object ObjectBlueprints {
    lateinit var blueprints: Map<Int, ObjectBlueprint>

    inline operator fun <reified T : ObjectBlueprint> get(index: Int): T {
        val bp = blueprints[index] ?: throw IOException("Could not find blueprint $index.")
        if(bp !is T) {
            throw TypeCastException("")
        } else {
            return bp
        }
    }

    fun load(
        cConfigs: Map<Int, ObjectConfig>,
        eObjectConfigs: List<ExtraObjectConfig>,
        eHeadConfigs: List<ExtraHeadConfig>,
        extraCapeConfigs: List<ExtraEquipmentConfig>,
        eNeckConfigs: List<ExtraEquipmentConfig>,
        eAmmunitionConfigs: List<ExtraEquipmentConfig>,
        eWeaponConfigs: List<ExtraEquipmentConfig>,
        eShieldConfigs: List<ExtraEquipmentConfig>,
        eTwoHandedConfigs: List<ExtraEquipmentConfig>,
        eBodyConfigs: List<ExtraBodyConfig>,
        eLegConfigs: List<ExtraEquipmentConfig>,
        eHandConfigs: List<ExtraEquipmentConfig>,
        eFeetConfigs: List<ExtraEquipmentConfig>,
        eRingConfigs: List<ExtraEquipmentConfig>
    ) {
        blueprints = mutableMapOf<Int, ObjectBlueprint>().apply {
            addBlueprints(cConfigs, eObjectConfigs) { cConfig, eConfig -> ObjectBlueprint(cConfig, eConfig) }
            addBlueprints(cConfigs, eHeadConfigs) { cConfig, eConfig -> HeadBlueprint(cConfig, eConfig) }
            addBlueprints(cConfigs, extraCapeConfigs) { cConfig, eConfig -> CapeBlueprint(cConfig, eConfig) }
            addBlueprints(cConfigs, eNeckConfigs) { cConfig, eConfig -> NeckBlueprint(cConfig, eConfig) }
            addBlueprints(cConfigs, eAmmunitionConfigs) { cConfig, eConfig -> AmmunitionBlueprint(cConfig, eConfig) }
            addBlueprints(cConfigs, eWeaponConfigs) { cConfig, eConfig -> WeaponBlueprint(cConfig, eConfig) }
            addBlueprints(cConfigs, eShieldConfigs) { cConfig, eConfig -> ShieldBlueprint(cConfig, eConfig) }
            addBlueprints(cConfigs, eTwoHandedConfigs) { cConfig, eConfig -> TwoHandedBlueprint(cConfig, eConfig) }
            addBlueprints(cConfigs, eBodyConfigs) { cConfig, eConfig -> BodyBlueprint(cConfig, eConfig) }
            addBlueprints(cConfigs, eLegConfigs) { cConfig, eConfig -> LegsBlueprint(cConfig, eConfig) }
            addBlueprints(cConfigs, eHandConfigs) { cConfig, eConfig -> HandsBlueprint(cConfig, eConfig) }
            addBlueprints(cConfigs, eFeetConfigs) { cConfig, eConfig -> FeetBlueprint(cConfig, eConfig) }
            addBlueprints(cConfigs, eRingConfigs) { cConfig, eConfig -> RingBlueprint(cConfig, eConfig) }
        }
        logger.info { "Loaded ${blueprints.size} object blueprints" }
    }

    private fun <E : ExtraObjectConfig, B : ObjectBlueprint> MutableMap<Int, ObjectBlueprint>.addBlueprints(
        cacheConfigs: Map<Int, ObjectConfig>,
        extraObjectConfigs: List<E>,
        construct: (ObjectConfig, E) -> B
    ) {
        extraObjectConfigs.forEach { extraConfig ->
            extraConfig.ids.forEach { id ->
                val cacheConfig = cacheConfigs[id] ?: throw FileNotFoundException(
                    "Could not find object config for id $id."
                )
                put(id, construct.invoke(cacheConfig, extraConfig))
            }
        }
    }
}