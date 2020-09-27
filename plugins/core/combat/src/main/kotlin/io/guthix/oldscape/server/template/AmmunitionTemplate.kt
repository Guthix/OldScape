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
import io.guthix.oldscape.server.combat.ProjectileType
import io.guthix.oldscape.server.world.entity.Obj

val Obj.ammunitionType: ProjectileType get() = ammunitionTemplate.type

val Obj.ammunitionProjectile: ProjectileTemplate get() = ammunitionTemplate.projectile

val Obj.drawback: PhysicalSpotAnimTemplate
    get() = PhysicalSpotAnimTemplate(
        SpotAnimTemplates[ammunitionTemplate.drawBackSpotAnim], ammunitionTemplate.drawBackSpotAnimHeight
    )

private val Obj.ammunitionTemplate: AmmunitionTemplate
    get() = template.ammunition ?: throw TemplateNotFoundException(id, AmmunitionTemplate::class)

internal val ObjTemplate.ammunition: AmmunitionTemplate? by Property { null }

data class AmmunitionTemplate(
    override val ids: List<Int>,
    val type: ProjectileType,
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