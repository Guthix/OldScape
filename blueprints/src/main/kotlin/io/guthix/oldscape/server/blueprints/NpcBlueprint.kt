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

class NpcStats(
    val health: Int,
    val attack: Int,
    val strength: Int,
    val defence: Int,
    val range: Int,
    val magic: Int
)

class NpcAggressiveStats(
    val attack: Int,
    val range: Int,
    val magic: Int,
    val strengthBonus: StrengthBonus
)

class NpcCombat(
    val lvl: Int?,
    val isAggressive: Boolean,
    val isPoisonous: Boolean,
    val isImmumePoison: Boolean,
    val isImmuneVenom: Boolean,
    val stats: NpcStats,
    val aggressiveStats: NpcAggressiveStats,
    val defensiveStats: StyleBonus
)

open class NpcBlueprint(
    private val cacheConfig: NpcConfig,
    protected open val extraConfig: ExtraNpcConfig
) {
    val id: Int get() = cacheConfig.id
    val size: Int get() = cacheConfig.size.toInt()
    val contextMenu: Array<String?> get() = cacheConfig.options
    val level: Int? get() = extraConfig.combat?.lvl
    val isAggressive: Boolean? get() = extraConfig.combat?.isAggressive
    val isPoisonous: Boolean? get() = extraConfig.combat?.isPoisonous
    val isImmumePoison: Boolean? get() = extraConfig.combat?.isImmumePoison
    val isImmuneVenom: Boolean? get() = extraConfig.combat?.isImmuneVenom
    val stats: NpcStats? get() = extraConfig.combat?.stats
    val aggressiveStats: NpcAggressiveStats? get() = extraConfig.combat?.aggressiveStats
    val defensiveStats: StyleBonus? get() = extraConfig.combat?.defensiveStats
}

open class ExtraNpcConfig(
    val ids: List<Int>,
    val examine: String,
    val combat: NpcCombat?
)