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
package io.guthix.oldscape.server.damage

import io.guthix.oldscape.server.Property
import io.guthix.oldscape.server.template.stats
import io.guthix.oldscape.server.world.entity.HealthBarUpdate
import io.guthix.oldscape.server.world.entity.HitMark
import io.guthix.oldscape.server.world.entity.Npc
import io.guthix.oldscape.server.world.entity.Player

var Player.health: Int
    get() = stats.hitpoints.status
    set(value) {
        stats.hitpoints.status = value
    }

var Npc.health: Int by Property {
    stats.health
}

fun Player.hit(hmColor: HitMark.Color, amount: Int) {
    addHitMark(HitMark(hmColor, amount, 0))
    updateHealthBar(HealthBarUpdate(2, 0, 0, health))
}

fun Npc.hit(hmColor: HitMark.Color, amount: Int) {
    addHitMark(HitMark(hmColor, amount, 0))
    updateHealthBar(HealthBarUpdate(2, 0, 0, health))
}