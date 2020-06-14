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

class NpcAttackStats(
    val typeBonus: CombatBonus,
    val strengthBonus: CombatBonus
)

class NpcCombat(
    val lvl: Int,
    val maxHit: Int?,
    val attackStyle: AttackStyle?,
    val isAggressive: Boolean,
    val isPoisonous: Boolean,
    val isImmumePoison: Boolean,
    val isImmuneVenom: Boolean,
    val stats: CombatStats,
    val attackStats: NpcAttackStats,
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
    val attackStyle: AttackStyle? get() = extraConfig.combat?.attackStyle
    val isAggressive: Boolean? get() = extraConfig.combat?.isAggressive
    val isPoisonous: Boolean? get() = extraConfig.combat?.isPoisonous
    val isImmumePoison: Boolean? get() = extraConfig.combat?.isImmumePoison
    val isImmuneVenom: Boolean? get() = extraConfig.combat?.isImmuneVenom
    val stats: CombatStats? get() = extraConfig.combat?.stats
    val attackStats: NpcAttackStats? get() = extraConfig.combat?.attackStats
    val defensiveStats: StyleBonus? get() = extraConfig.combat?.defensiveStats
}

open class ExtraNpcConfig(
    val ids: List<Int>,
    val examine: String,
    val combat: NpcCombat?
)