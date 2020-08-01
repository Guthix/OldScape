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

//class ExtraAmmunitionConfig(
//    override val ids: List<Int>,
//    override val weight: Float,
//    override val examine: String,
//    val type: AmmunitionProjectile?,
//    val projectileId: Int?,
//    val drawBackSpotAnim: SpotAnimBlueprint?,
//    override val equipment: EquipmentBlueprint.Equipment
//) : ExtraEquipmentConfig(ids, weight, examine, equipment)
//
//class AmmunitionBlueprint(
//    cacheConfig: ObjectConfig,
//    override val extraConfig: ExtraAmmunitionConfig
//) : EquipmentBlueprint(cacheConfig, extraConfig) {
//    val type: AmmunitionProjectile? get() = extraConfig.type
//
//    val projectile: Int? get() = extraConfig.projectileId
//
//    val drawBack: SpotAnimBlueprint? get() = extraConfig.drawBackSpotAnim
//}

//data class AmmunitionEquipment(
//    private val blueprint: AmmunitionBlueprint,
//    override var quantity: Int
//) : Equipment(blueprint, quantity) {
//    val projectileBlueprint: ProjectileBlueprint = let {
//        val projId = projectileId ?: throw ConfigDataMissingException(
//            "No projectileId provided for ammunition equipment ${blueprint.name} id: $id"
//        )
//        val projType = type ?: throw ConfigDataMissingException(
//            "No type id provided for ammunition equipment ${blueprint.name} id: $id"
//        )
//        ProjectileBlueprint(
//            projId,
//            projType.startHeight,
//            projType.endHeight,
//            projType.speed,
//            projType.speedDelay,
//            projType.delay,
//            projType.angle,
//            projType.steepness
//        )
//    }
//
//    val type: AmmunitionProjectile? get() = blueprint.type
//
//    val projectileId: Int? get() = blueprint.projectile
//
//    val drawBackSpotAnim: SpotAnimBlueprint? get() = blueprint.drawBack
//
//    fun createProjectile(from: Tile, to: Character): Projectile = Projectile(projectileBlueprint, from, to)
//
//    companion object {
//        const val slot: Int = 13
//    }
//}