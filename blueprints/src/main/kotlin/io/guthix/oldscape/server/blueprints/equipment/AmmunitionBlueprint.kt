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
package io.guthix.oldscape.server.blueprints.equipment

import io.guthix.oldscape.cache.config.ObjectConfig
import io.guthix.oldscape.server.blueprints.AmmunitionProjectile
import io.guthix.oldscape.server.blueprints.SpotAnimBlueprint

class ExtraAmmunitionConfig(
    override val ids: List<Int>,
    override val weight: Float,
    override val examine: String,
    val type: AmmunitionProjectile?,
    val projectileId: Int?,
    val drawBackSpotAnim: SpotAnimBlueprint?,
    override val equipment: EquipmentBlueprint.Equipment
) : ExtraEquipmentConfig(ids, weight, examine, equipment)

class AmmunitionBlueprint(
    cacheConfig: ObjectConfig,
    override val extraConfig: ExtraAmmunitionConfig
) : EquipmentBlueprint(cacheConfig, extraConfig) {
    val type: AmmunitionProjectile? get() = extraConfig.type

    val projectile: Int? get() = extraConfig.projectileId

    val drawBack: SpotAnimBlueprint? get() = extraConfig.drawBackSpotAnim
}