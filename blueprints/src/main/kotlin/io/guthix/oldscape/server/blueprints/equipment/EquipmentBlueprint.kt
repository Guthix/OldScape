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
import io.guthix.oldscape.server.blueprints.CombatBonus
import io.guthix.oldscape.server.blueprints.ExtraObjectConfig
import io.guthix.oldscape.server.blueprints.ObjectBlueprint
import io.guthix.oldscape.server.blueprints.StyleBonus

open class ExtraEquipmentConfig(
    ids: List<Int>,
    weight: Float,
    examine: String,
    open val equipment: EquipmentBlueprint.Equipment
) : ExtraObjectConfig(ids, weight, examine)

open class EquipmentBlueprint(
    cacheConfig: ObjectConfig,
    override val extraConfig: ExtraEquipmentConfig
) : ObjectBlueprint(cacheConfig, extraConfig) {
    val attackBonus: StyleBonus get() = extraConfig.equipment.attackBonus

    val strengthBonus: CombatBonus get() = extraConfig.equipment.strengthBonus

    val defenceBonus: StyleBonus get() = extraConfig.equipment.defenceBonus

    val prayerBonus: Int get() = extraConfig.equipment.prayerBonus

    class Equipment(
        val attackBonus: StyleBonus,
        val defenceBonus: StyleBonus,
        val strengthBonus: CombatBonus,
        val prayerBonus: Int
    )
}