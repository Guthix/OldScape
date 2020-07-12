/*
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

    val type: AmmunitionProjectile? get() = blueprint.type

    val projectileId: Int? get() = blueprint.projectileId

    val drawBackSpotAnim: SpotAnimation? get() = blueprint.drawBackSpotAnim

    fun createProjectile(from: Tile, to: Character): Projectile {
        val projId = projectileId ?: throw ConfigDataMissingException(
            "No projectileId provided for ammunition equipment ${blueprint.name} id: $id"
        )
        val projType = type ?: throw ConfigDataMissingException(
            "No type id provided for ammunition equipment ${blueprint.name} id: $id"
        )
        return Projectile(
            projId,
            from,
            projType.startHeight,
            to,
            projType.targetHeight,
            projType.speed,
            projType.speedDelay,
            projType.delay,
            projType.angle,
            projType.steepness
        )
    }

    companion object {
        const val slot: Int = 13
    }
}