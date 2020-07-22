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

import io.guthix.oldscape.server.api.ObjectBlueprints
import io.guthix.oldscape.server.blueprints.*
import io.guthix.oldscape.server.blueprints.equipment.*
import io.guthix.oldscape.server.dimensions.TileUnit
import io.guthix.oldscape.server.plugin.ConfigDataMissingException
import io.guthix.oldscape.server.world.map.Tile

abstract class Equipment(id: Int, quantity: Int) : Obj(id, quantity) {
    override val blueprint: EquipmentBlueprint = ObjectBlueprints[id]

    val attackBonus: StyleBonus get() = blueprint.attackBonus

    val defenceBonus: StyleBonus get() = blueprint.defenceBonus

    val strengthBonus: CombatBonus get() = blueprint.strengthBonus

    val prayerBonus: Int get() = blueprint.prayerBonus
}

class HeadEquipment(id: Int, quantity: Int) : Equipment(id, quantity) {
    override val blueprint: HeadBlueprint = ObjectBlueprints[id]

    val coversFace: Boolean get() = blueprint.coversFace
    val coversHair: Boolean get() = blueprint.coversHair

    companion object {
        const val slot: Int = 0
    }
}

class CapeEquipment(id: Int, quantity: Int) : Equipment(id, quantity) {
    override val blueprint: CapeBlueprint = ObjectBlueprints[id]

    companion object {
        const val slot: Int = 1
    }
}

class NeckEquipment(id: Int, quantity: Int) : Equipment(id, quantity) {
    override val blueprint: NeckBlueprint = ObjectBlueprints[id]

    companion object {
        const val slot: Int = 2
    }
}

open class WeaponEquipment(id: Int, quantity: Int) : Equipment(id, quantity) {
    override val blueprint: WeaponBlueprint = ObjectBlueprints[id]

    val baseAttackSpeed: Int get() = blueprint.attackSpeed

    val type: WeaponType get() = blueprint.type

    val baseAttackRange: TileUnit get() = blueprint.attackRange

    val weaponSequences: WeaponSequences? get() = blueprint.weaponSequences

    val stanceSequences: StanceSequences? get() = blueprint.stanceSequences

    companion object {
        const val slot: Int = 3
    }
}

class TwoHandEquipment(id: Int, quantity: Int) : WeaponEquipment(id, quantity) {
    override val blueprint: TwoHandBlueprint = ObjectBlueprints[id]
}


class BodyEquipment(id: Int, quantity: Int) : Equipment(id, quantity) {
    override val blueprint: BodyBlueprint = ObjectBlueprints[id]

    val isFullBody: Boolean get() = blueprint.isFullBody

    companion object {
        const val slot: Int = 4
    }
}

class ShieldEquipment(id: Int, quantity: Int) : Equipment(id, quantity) {
    override val blueprint: ShieldBlueprint = ObjectBlueprints[id]

    companion object {
        const val slot: Int = 5
    }
}

class LegEquipment(id: Int, quantity: Int) : Equipment(id, quantity) {
    override val blueprint: LegsBlueprint = ObjectBlueprints[id]

    companion object {
        const val slot: Int = 7
    }
}

class HandEquipment(id: Int, quantity: Int) : Equipment(id, quantity) {
    override val blueprint: HandsBlueprint = ObjectBlueprints[id]

    companion object {
        const val slot: Int = 9
    }
}

class FeetEquipment(id: Int, quantity: Int) : Equipment(id, quantity) {
    override val blueprint: FeetBlueprint = ObjectBlueprints[id]

    companion object {
        const val slot: Int = 10
    }
}

class RingEquipment(id: Int, quantity: Int) : Equipment(id, quantity) {
    override val blueprint: RingBlueprint = ObjectBlueprints[id]

    companion object {
        const val slot: Int = 11
    }
}

class AmmunitionEquipment(id: Int, quantity: Int) : Equipment(id, quantity) {
    override val blueprint: AmmunitionBlueprint = ObjectBlueprints[id]

    val projectileBlueprint: ProjectileBlueprint = let {
        val projId = projectileId ?: throw ConfigDataMissingException(
            "No projectileId provided for ammunition equipment ${blueprint.name} id: $id"
        )
        val projType = type ?: throw ConfigDataMissingException(
            "No type id provided for ammunition equipment ${blueprint.name} id: $id"
        )
        ProjectileBlueprint(
            projId,
            projType.startHeight,
            projType.endHeight,
            projType.speed,
            projType.speedDelay,
            projType.delay,
            projType.angle,
            projType.steepness
        )
    }

    val type: AmmunitionProjectile? get() = blueprint.type

    val projectileId: Int? get() = blueprint.projectile

    val drawBackSpotAnim: SpotAnimBlueprint? get() = blueprint.drawBack

    fun createProjectile(from: Tile, to: Character): Projectile = Projectile(projectileBlueprint, from, to)

    companion object {
        const val slot: Int = 13
    }
}