/*
 * Copyright 2018-2020 Guthix
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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