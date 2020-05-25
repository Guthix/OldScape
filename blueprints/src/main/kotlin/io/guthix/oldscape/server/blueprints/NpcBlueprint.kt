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

class NpcBlueprint(
    val id: Int,
    val name: String?,
    val examine: String,
    val size: Int,
    val contextMenu: Array<String?>,
    val combatLevel: Int?,
    val isInteractable: Boolean,
    var walkSequence: Int?,
    var walkLeftSequence: Int?,
    var walkRightSequence: Int?,
    var walkBackSequence: Int?,
    var turnLeftSequence: Int?,
    var turnRightSequence: Int?,
    val combat: NpcBlueprint.Combat?
) {
    class Combat(
        val isAggressive: Boolean,
        val isPoisonous: Boolean,
        val isImmumePoison: Boolean,
        val isImmuneVenom: Boolean,
        val stats: Stats,
        val aggressiveStats: AggressiveStats,
        val defensiveStats: StyleBonus
    ) {
        class Stats(
            val health: Int,
            val attack: Int,
            val strength: Int,
            val defence: Int,
            val range: Int,
            val magic: Int
        )

        class AggressiveStats(
            val attack: Int,
            val range: Int,
            val magic: Int,
            val strengthBonus: StrengthBonus
        )
    }
}

class ExtraNpcConfig(
    val ids: List<Int>,
    val examine: String,
    val combat: NpcBlueprint.Combat?
)