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
package io.guthix.oldscape.server.combat.dmg

import io.guthix.oldscape.server.template.TemplateNotFoundException
import io.guthix.oldscape.server.template.maxHit
import io.guthix.oldscape.server.world.entity.Npc
import io.guthix.oldscape.server.world.entity.Player
import kotlin.random.Random

private fun calcDamage(accuracy: Double, maxHit: Int): Int? = if (Random.nextDouble(1.0) < accuracy) {
    Random.nextInt(maxHit + 1)
} else null

fun Player.calcHit(other: Player, maxHit: Int): Int? = calcDamage(accuracy(other), maxHit)

fun Player.calcHit(other: Npc, maxHit: Int): Int? = calcDamage(accuracy(other), maxHit)

fun Npc.calcHit(other: Player): Int? = calcDamage(accuracy(other), maxHit)

fun Npc.calcHit(other: Npc): Int? = calcDamage(accuracy(other), maxHit)