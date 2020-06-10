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

import io.guthix.oldscape.cache.config.NpcConfig

class MonsterBlueprint(
    cacheConfig: NpcConfig,
    override val extraConfig: ExtraMonsterConfig,
) : NpcBlueprint(cacheConfig, extraConfig) {
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
    private val cacheConfig: NpcConfig,
    protected open val extraConfig: ExtraNpcConfig
) {
    val id: Int get() = cacheConfig.id
    val size: Int get() = cacheConfig.size.toInt()
    val contextMenu: Array<String?> get() = cacheConfig.options
}

class ExtraMonsterConfig(
    ids: List<Int>,
    examine: String,
    val combat: MonsterBlueprint.Combat?
) : ExtraNpcConfig(ids, examine)

open class ExtraNpcConfig(
    val ids: List<Int>,
    val examine: String
)