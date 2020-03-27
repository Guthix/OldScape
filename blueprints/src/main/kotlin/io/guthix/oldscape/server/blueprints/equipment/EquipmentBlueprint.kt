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
package io.guthix.oldscape.server.blueprints.equipment

import io.guthix.oldscape.cache.config.ObjectConfig
import io.guthix.oldscape.server.blueprints.ExtraObjectConfig
import io.guthix.oldscape.server.blueprints.ObjectBlueprint
import io.guthix.oldscape.server.blueprints.StrengthBonus
import io.guthix.oldscape.server.blueprints.StyleBonus

open class ExtraEquipmentConfig(
    ids: List<Int>,
    weight: Float,
    examine: String,
    val equipment: EquipmentBlueprint.Equipment
) : ExtraObjectConfig(ids, weight, examine)

open class EquipmentBlueprint(
    cacheConfig: ObjectConfig,
    override val extraConfig: ExtraEquipmentConfig
) : ObjectBlueprint(cacheConfig, extraConfig) {
    val equipment get() = extraConfig.equipment

    enum class EquipmentSlot(val slot: Int) {
        HEAD(0),
        CAPE(1),
        NECK(2),
        AMMUNITION(3),
        WEAPON(4),
        SHIELD(5),
        TWO_HAND(4),
        BODY(6),
        LEGS(7),
        HANDS(8),
        FEET(9),
        RING(10)
    }

    class Equipment(
        val attackBonus: StyleBonus,
        val defenceBonus: StyleBonus,
        val strengthBonus: StrengthBonus,
        val prayerBonus: Int
    )
}
