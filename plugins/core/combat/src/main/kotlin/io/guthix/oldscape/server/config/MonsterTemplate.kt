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
package io.guthix.oldscape.server.config

import io.guthix.oldscape.server.blueprints.CombatBonus
import io.guthix.oldscape.server.blueprints.CombatStats
import io.guthix.oldscape.server.blueprints.StyleBonus

data class MonsterTemplate(
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
    val attackBonus: CombatBonus,
    val strengthBonus: CombatBonus,
    val defensiveStats: StyleBonus
)