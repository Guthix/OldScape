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
import io.guthix.oldscape.server.blueprints.AmmunitionProjectile
import io.guthix.oldscape.server.blueprints.SpotAnimation

class ExtraAmmunitionConfig(
    override val ids: List<Int>,
    override val weight: Float,
    override val examine: String,
    val type: AmmunitionProjectile?,
    val projectileId: Int?,
    val drawBackSpotAnim: SpotAnimation?,
    override val equipment: EquipmentBlueprint.Equipment
) : ExtraEquipmentConfig(ids, weight, examine, equipment)

class AmmunitionBlueprint(
    cacheConfig: ObjectConfig,
    override val extraConfig: ExtraAmmunitionConfig
) : EquipmentBlueprint(cacheConfig, extraConfig) {
    val type: AmmunitionProjectile? get() = extraConfig.type

    val projectileId: Int? get() = extraConfig.projectileId

    val drawBackSpotAnim: SpotAnimation? get() = extraConfig.drawBackSpotAnim
}