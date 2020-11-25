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
package io.guthix.oldscape.dump.yaml

import io.guthix.oldscape.server.template.AttackType
import io.guthix.oldscape.server.template.WeaponType
import io.guthix.oldscape.server.world.entity.interest.PlayerManager
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

fun String?.toEquipmentType(): PlayerManager.EquipmentType? = when {
    equals("ammo", true) -> PlayerManager.EquipmentType.AMMUNITION
    equals("body", true) -> PlayerManager.EquipmentType.BODY
    equals("cape", true) -> PlayerManager.EquipmentType.CAPE
    equals("feet", true) -> PlayerManager.EquipmentType.FEET
    equals("hands", true) -> PlayerManager.EquipmentType.HANDS
    equals("head", true) -> PlayerManager.EquipmentType.HEAD
    equals("legs", true) -> PlayerManager.EquipmentType.LEGS
    equals("neck", true) -> PlayerManager.EquipmentType.NECK
    equals("ring", true) -> PlayerManager.EquipmentType.RING
    equals("shield", true) -> PlayerManager.EquipmentType.SHIELD
    equals("2h", true) -> PlayerManager.EquipmentType.TWO_HAND_WEAPON
    equals("weapon", true) -> PlayerManager.EquipmentType.ONE_HAND_WEAPON
    this == null -> null
    else -> {
        logger.info { "Couldn't get equipment type for $this" }; null
    }
}

fun String?.toAttackType(): AttackType? = when {
    equals("stab", true) -> AttackType.STAB
    equals("slash", true) -> AttackType.SLASH
    equals("crush", true) -> AttackType.CRUSH
    equals("range", true) -> AttackType.RANGED
    equals("ranged", true) -> AttackType.RANGED
    equals("magic", true) -> AttackType.MAGIC
    this == null -> null
    else -> {
        logger.info { "Couldn't get attack style for $this" }; null
    }
}

fun String?.toWeaponType(): WeaponType? = when {
    equals("axe", ignoreCase = true) -> WeaponType.AXE
    equals("bludegon", ignoreCase = true) -> WeaponType.BLUDGEON
    equals("blunt", ignoreCase = true) -> WeaponType.BLUNT
    equals("bulwark", ignoreCase = true) -> WeaponType.BULWARK
    equals("claws", ignoreCase = true) -> WeaponType.CLAW
    equals("polearm", ignoreCase = true) -> WeaponType.POLEARM
    equals("pickaxe", ignoreCase = true) -> WeaponType.PICKAXE
    equals("scythe", ignoreCase = true) -> WeaponType.SCYTHE
    equals("hacksword", ignoreCase = true) -> WeaponType.SLASHING_SWORD
    equals("spear", ignoreCase = true) -> WeaponType.SPEAR
    equals("spiked", ignoreCase = true) -> WeaponType.SPIKED_WEAPON
    equals("stabsword", ignoreCase = true) -> WeaponType.STABBING_SWORD
    equals("heavysword", ignoreCase = true) -> WeaponType.TWOHANDED_SWORD
    equals("whip", ignoreCase = true) -> WeaponType.WHIP
    equals("bow", ignoreCase = true) -> WeaponType.BOW
    equals("grenade", ignoreCase = true) -> WeaponType.CHINCHOMPA
    equals("crossbow", ignoreCase = true) -> WeaponType.CROSSBOW
    equals("thrown", ignoreCase = true) -> WeaponType.THROW_WEAPON
    equals("staff", ignoreCase = true) -> WeaponType.STAFF
    equals("staff bladed", ignoreCase = true) -> WeaponType.BLADED_STAFF
    equals("staff selfpowering", ignoreCase = true) -> WeaponType.POWERED_STAFF
    equals("banner", ignoreCase = true) -> WeaponType.BANNER
    equals("blaster", ignoreCase = true) -> WeaponType.BLASTER
    equals("gun", ignoreCase = true) -> WeaponType.GUN
    equals("polestaff", ignoreCase = true) -> WeaponType.POLESTAFF
    equals("flamer", ignoreCase = true) -> WeaponType.SALAMANDER
    equals("unarmed", ignoreCase = true) -> WeaponType.UNARMED
    this == null -> null
    else -> {
        logger.info { "Couldn't get attack style for $this" }; null
    }
}