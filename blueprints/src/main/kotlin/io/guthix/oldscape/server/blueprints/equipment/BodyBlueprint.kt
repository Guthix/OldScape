/**
 * This file is part of Guthix OldScape-Server.
 *
 * Guthix OldScape-Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape-Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
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
) : EquipmentBlueprint(cacheConfig, EquipmentSlot.BODY, extraConfig) {
    val isFullBody: Boolean get() = extraConfig.isFullBody
}