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
    val slot: EquipmentSlot,
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

enum class EquipmentSlot(val id: Int) {
    HEAD(0),
    CAPE(1),
    NECK(2),
    WEAPON(3),
    BODY(4),
    SHIELD(5),
    LEGS(7),
    HANDS(9),
    FEET(10),
    RING(11),
    AMMUNITION(13),
}