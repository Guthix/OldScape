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
package io.guthix.oldscape.server.blueprints

import io.guthix.oldscape.cache.config.NpcConfig
import io.guthix.oldscape.server.dimensions.TileUnit
import io.guthix.oldscape.server.dimensions.tiles

data class NpcAttackStats(
    val typeBonus: CombatBonus,
    val strengthBonus: CombatBonus
)

class NpcCombat(
    val lvl: Int,
    val maxHit: Int?,
    val attackType: AttackType?,
    val isAggressive: Boolean,
    val isPoisonous: Boolean,
    val isImmumePoison: Boolean,
    val isImmuneVenom: Boolean,
    val attackSpeed: Int?,
    val sequences: CombatSequences?,
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
    val wanderRadius: TileUnit get() = extraConfig.wanderRadius?.tiles ?: 0.tiles
    val level: Int? get() = extraConfig.combat?.lvl
    val maxHit: Int? get() = if (extraConfig.combat == null) null else extraConfig.combat?.maxHit ?: 0
    val attackType: AttackType get() = extraConfig.combat?.attackType ?: AttackType.NONE
    val isAggressive: Boolean? get() = extraConfig.combat?.isAggressive
    val isPoisonous: Boolean? get() = extraConfig.combat?.isPoisonous
    val isImmumePoison: Boolean? get() = extraConfig.combat?.isImmumePoison
    val isImmuneVenom: Boolean? get() = extraConfig.combat?.isImmuneVenom
    val attackSpeed: Int get() = extraConfig.combat?.attackSpeed ?: 0
    val combatSequences: CombatSequences? get() = extraConfig.combat?.sequences
    val stats: CombatStats? get() = extraConfig.combat?.stats
    val attackStats: NpcAttackStats? get() = extraConfig.combat?.attackStats
    val defensiveStats: StyleBonus? get() = extraConfig.combat?.defensiveStats
}

open class ExtraNpcConfig(
    val ids: List<Int>,
    val examine: String,
    val wanderRadius: Int?,
    val combat: NpcCombat?
)