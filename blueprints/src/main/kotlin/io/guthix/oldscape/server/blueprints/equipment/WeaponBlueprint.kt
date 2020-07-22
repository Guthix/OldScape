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
import io.guthix.oldscape.server.blueprints.StanceSequences
import io.guthix.oldscape.server.blueprints.WeaponSequences
import io.guthix.oldscape.server.blueprints.WeaponType
import io.guthix.oldscape.server.dimensions.TileUnit
import io.guthix.oldscape.server.dimensions.tiles

data class ExtraWeaponConfig(
    override val ids: List<Int>,
    override val weight: Float,
    override val examine: String,
    val type: WeaponType,
    val attackSpeed: Int,
    val attackRange: Int = 1,
    val weaponSequences: WeaponSequences?,
    val overrideSequences: StanceSequences?,
    override val equipment: EquipmentBlueprint.Equipment
) : ExtraEquipmentConfig(ids, weight, examine, equipment)

open class WeaponBlueprint(
    cacheConfig: ObjectConfig,
    override val extraConfig: ExtraWeaponConfig
) : EquipmentBlueprint(cacheConfig, extraConfig) {
    val attackSpeed: Int get() = extraConfig.attackSpeed

    val type: WeaponType get() = extraConfig.type

    val attackRange: TileUnit get() = extraConfig.attackRange.tiles

    val weaponSequences: WeaponSequences? get() = extraConfig.weaponSequences

    val stanceSequences: StanceSequences? get() = extraConfig.overrideSequences
}