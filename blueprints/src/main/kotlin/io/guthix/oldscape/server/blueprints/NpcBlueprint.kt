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

class MonsterBlueprint(
    id: Int,
    name: String?,
    examine: String,
    size: Int,
    contextMenu: Array<String?>,
    isInteractable: Boolean,
    walkSequence: Int?,
    walkLeftSequence: Int?,
    walkRightSequence: Int?,
    walkBackSequence: Int?,
    turnLeftSequence: Int?,
    turnRightSequence: Int?,
    var combat: Combat?
) : NpcBlueprint(id, name, examine, size, contextMenu, isInteractable, walkSequence, walkLeftSequence,
    walkRightSequence, walkBackSequence, turnLeftSequence, turnRightSequence
) {
    class Combat(
        val lvl: Int?,
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

open class NpcBlueprint(
    val id: Int,
    val name: String?,
    val examine: String,
    val size: Int,
    val contextMenu: Array<String?>,
    val isInteractable: Boolean,
    var walkSequence: Int?,
    var walkLeftSequence: Int?,
    var walkRightSequence: Int?,
    var walkBackSequence: Int?,
    var turnLeftSequence: Int?,
    var turnRightSequence: Int?,
)

class ExtraMonsterConfig(
    ids: List<Int>,
    examine: String,
    val combat: MonsterBlueprint.Combat?
) : ExtraNpcConfig(ids, examine)

open class ExtraNpcConfig(
    val ids: List<Int>,
    val examine: String
)