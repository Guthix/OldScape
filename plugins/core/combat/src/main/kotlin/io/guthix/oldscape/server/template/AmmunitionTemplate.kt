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
package io.guthix.oldscape.server.template

import io.guthix.oldscape.server.Property
import io.guthix.oldscape.server.world.entity.Obj

val Obj.ammunitionType: AmmunitionTypeProjectile get() = ammunitionTemplate.type

val Obj.ammunitionProjectile: ProjectileTemplate get() = ammunitionTemplate.projectile

val Obj.drawback: PhysicalSpotAnimTemplate
    get() = PhysicalSpotAnimTemplate(
        SpotAnimTemplates[ammunitionTemplate.drawBackSpotAnim],
        ammunitionTemplate.drawBackSpotAnimHeight
    )

private val Obj.ammunitionTemplate: AmmunitionTemplate
    get() = template.ammunition ?: throw TemplateNotFoundException(
        id, AmmunitionTemplate::class
    )

internal val ObjTemplate.ammunition: AmmunitionTemplate? by Property { null }

data class AmmunitionTemplate(
    override val ids: List<Int>,
    val type: AmmunitionTypeProjectile,
    val projectileId: Int,
    val drawBackSpotAnim: Int,
    val drawBackSpotAnimHeight: Int
) : Template(ids) {
    val projectile: ProjectileTemplate
        get() = ProjectileTemplate(
            projectileId,
            type.startHeight,
            type.endHeight,
            type.speed,
            type.speedDelay,
            type.delay,
            type.angle,
            type.steepness
        )
}

enum class AmmunitionTypeProjectile(
    val startHeight: Int,
    val endHeight: Int,
    val speed: Int,
    val speedDelay: Int,
    val delay: Int,
    val angle: Int,
    val steepness: Int
) {
    ARROW(startHeight = 40, endHeight = 36, speed = 5, speedDelay = 5, delay = 41, angle = 15, steepness = 11),
    BOLT(startHeight = 38, endHeight = 36, speed = 5, speedDelay = 5, delay = 41, angle = 5, steepness = 11),
    JAVELIN(startHeight = 38, endHeight = 36, speed = 3, speedDelay = 2, delay = 42, angle = 1, steepness = 120),
    THROWN(startHeight = 40, endHeight = 36, speed = 5, speedDelay = 5, delay = 32, angle = 15, steepness = 11),
    CHINCHOMPA(startHeight = 40, endHeight = 36, speed = 5, speedDelay = 5, delay = 21, angle = 15, steepness = 11),
    MAGIC(startHeight = 43, endHeight = 31, speed = 10, speedDelay = 5, delay = 51, angle = 16, steepness = 64)
}