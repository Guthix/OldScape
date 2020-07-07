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
) : EquipmentBlueprint(cacheConfig, EquipmentSlot.WEAPON, extraConfig) {
    val attackSpeed: Int get() = extraConfig.attackSpeed

    val type: WeaponType get() = extraConfig.type

    val attackRange: TileUnit get() = extraConfig.attackRange.tiles

    val weaponSequences: WeaponSequences? get() = extraConfig.weaponSequences

    val stanceSequences: StanceSequences? get() = extraConfig.overrideSequences
}