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

import io.guthix.oldscape.server.blueprints.*
import io.guthix.oldscape.server.blueprints.equipment.*
import io.guthix.oldscape.server.dimensions.TileUnit
import io.guthix.oldscape.server.plugin.ConfigDataMissingException
import io.guthix.oldscape.server.world.map.Tile

abstract class Equipment(
    private val blueprint: EquipmentBlueprint,
    override var quantity: Int
) : Obj(blueprint, quantity) {
    val attackBonus: StyleBonus get() = blueprint.attackBonus
    val defenceBonus: StyleBonus get() = blueprint.defenceBonus
    val strengthBonus: CombatBonus get() = blueprint.strengthBonus
    val prayerBonus: Int get() = blueprint.prayerBonus
}

fun HeadBlueprint.create(amount: Int): HeadEquipment = HeadEquipment(this, amount)

data class HeadEquipment(
    private val blueprint: HeadBlueprint,
    override var quantity: Int
) : Equipment(blueprint, quantity) {
    val coversFace: Boolean get() = blueprint.coversFace
    val coversHair: Boolean get() = blueprint.coversHair

    companion object {
        const val slot: Int = 0
    }
}

fun CapeBlueprint.create(amount: Int): CapeEquipment = CapeEquipment(this, amount)

data class CapeEquipment(
    private val blueprint: CapeBlueprint,
    override var quantity: Int
) : Equipment(blueprint, quantity) {
    companion object {
        const val slot: Int = 1
    }
}

fun NeckBlueprint.create(amount: Int): NeckEquipment = NeckEquipment(this, amount)

data class NeckEquipment(
    private val blueprint: NeckBlueprint,
    override var quantity: Int
) : Equipment(blueprint, quantity) {
    companion object {
        const val slot: Int = 2
    }
}

abstract class WeaponEquipment(
    private val blueprint: WeaponBlueprint,
    override var quantity: Int
) : Equipment(blueprint, quantity) {
    val baseAttackSpeed: Int get() = blueprint.attackSpeed
    val type: WeaponType get() = blueprint.type
    val baseAttackRange: TileUnit get() = blueprint.attackRange
    val weaponSequences: WeaponSequences? get() = blueprint.weaponSequences
    val stanceSequences: StanceSequences? get() = blueprint.stanceSequences

    companion object {
        const val slot: Int = 3
    }
}

fun WeaponBlueprint.create(amount: Int): SingleHandEquipment = SingleHandEquipment(this, amount)

data class SingleHandEquipment(
    private val blueprint: WeaponBlueprint,
    override var quantity: Int
) : Equipment(blueprint, quantity)

fun TwoHandBlueprint.create(amount: Int): TwoHandEquipment = TwoHandEquipment(this, amount)

data class TwoHandEquipment(
    private val blueprint: TwoHandBlueprint,
    override var quantity: Int
) : WeaponEquipment(blueprint, quantity)

fun BodyBlueprint.create(amount: Int): BodyEquipment = BodyEquipment(this, amount)

data class BodyEquipment(
    private val blueprint: BodyBlueprint,
    override var quantity: Int
) : Equipment(blueprint, quantity) {
    val isFullBody: Boolean get() = blueprint.isFullBody

    companion object {
        const val slot: Int = 4
    }
}

fun ShieldBlueprint.create(amount: Int): ShieldEquipment = ShieldEquipment(this, amount)

data class ShieldEquipment(
    private val blueprint: ShieldBlueprint,
    override var quantity: Int
) : Equipment(blueprint, quantity) {
    companion object {
        const val slot: Int = 5
    }
}

fun LegBlueprint.create(amount: Int): LegEquipment = LegEquipment(this, amount)

data class LegEquipment(
    private val blueprint: LegBlueprint,
    override var quantity: Int
) : Equipment(blueprint, quantity) {
    companion object {
        const val slot: Int = 7
    }
}

fun HandBlueprint.create(amount: Int): HandEquipment = HandEquipment(this, amount)

data class HandEquipment(
    private val blueprint: HandBlueprint,
    override var quantity: Int
) : Equipment(blueprint, quantity) {
    companion object {
        const val slot: Int = 9
    }
}

fun FeetBlueprint.create(amount: Int): FeetEquipment = FeetEquipment(this, amount)

data class FeetEquipment(
    private val blueprint: FeetBlueprint,
    override var quantity: Int
) : Equipment(blueprint, quantity) {
    companion object {
        const val slot: Int = 10
    }
}

fun RingBlueprint.create(amount: Int): RingEquipment = RingEquipment(this, amount)

data class RingEquipment(
    private val blueprint: RingBlueprint,
    override var quantity: Int
) : Equipment(blueprint, quantity) {
    companion object {
        const val slot: Int = 11
    }
}

fun AmmunitionBlueprint.create(amount: Int): AmmunitionEquipment = AmmunitionEquipment(this, amount)

data class AmmunitionEquipment(
    private val blueprint: AmmunitionBlueprint,
    override var quantity: Int
) : Equipment(blueprint, quantity) {
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