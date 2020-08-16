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
package io.guthix.oldscape.server.world.entity

import io.guthix.oldscape.server.template.type.ProjectileTemplate
import io.guthix.oldscape.server.world.map.Tile

data class Projectile(private val bp: ProjectileTemplate, val start: Tile, val target: Character) {
    val id: Int get() = bp.id

    val startHeight: Int get() = bp.startHeight

    val endHeight: Int get() = bp.endHeight

    val speed: Int get() = bp.speed

    val speedDelay: Int get() = bp.speedDelay

    val delay: Int get() = bp.delay

    val angle: Int get() = bp.angle

    val steepness: Int get() = bp.steepness

    val distance: Int = start.distanceTo(target.pos)

    val lifetimeClientTicks: Int = bp.delay + bp.speedDelay + bp.speed * distance

    val lifeTimeServerTicks: Int = lifetimeClientTicks / 30
}