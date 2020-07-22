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

data class ExtraBodyConfig(
    override val ids: List<Int>,
    override val weight: Float,
    override val examine: String,
    val isFullBody: Boolean = false,
    override val equipment: EquipmentBlueprint.Equipment
) : ExtraEquipmentConfig(ids, weight, examine, equipment)

class BodyBlueprint(
    cacheConfig: ObjectConfig,
    override val extraConfig: ExtraBodyConfig
) : EquipmentBlueprint(cacheConfig, extraConfig) {
    val isFullBody: Boolean get() = extraConfig.isFullBody
}