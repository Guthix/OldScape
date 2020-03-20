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
package io.guthix.oldscape.server.blueprints

import io.guthix.oldscape.cache.config.ObjectConfig

class ObjectBlueprint private constructor(
    val id: Int,
    val name: String,
    val weight: Float?,
    val examines: String,
    val isStackable: Boolean,
    val isTradable: Boolean,
    val notedId: Int?,
    val isNoted: Boolean,
    val placeHolderId: Int?,
    val isPlaceHolder: Boolean,
    val interfaceOperations: Array<String?>,
    val groundOperations: Array<String?>,
    val equipment: Equipment?
) {
    class Equipment(
        val slot: EquipmentSlot,
        val attackBonus: StyleBonus,
        val defenceBonus: StyleBonus,
        val strengthBonus: StrengthBonus,
        val prayerBonus: Int
    )

    companion object {
        fun create(config: ObjectConfig, extraConfig: ExtraObjectConfig): ObjectBlueprint {
            return ObjectBlueprint(
                config.id,
                config.name,
                extraConfig.weight,
                extraConfig.examine,
                config.stackable,
                config.tradable,
                config.notedId,
                config.isNoted,
                config.placeholderId,
                config.isPlaceHolder,
                config.iop,
                config.groundActions,
                extraConfig.equipment
            )
        }
    }
}

class ExtraObjectConfig(
    val ids: List<Int>,
    val weight: Float,
    val examine: String,
    val equipment: ObjectBlueprint.Equipment?
)