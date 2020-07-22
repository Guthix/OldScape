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
package io.guthix.oldscape.server.equipment

import io.guthix.oldscape.server.event.*
import io.guthix.oldscape.server.world.entity.*

on(InvObjClickEvent::class).then {
    val obj = player.topInterface.inventory.remove(inventorySlot) ?: return@then
    when (obj) {
        is TwoHandEquipment -> {
            player.topInterface.equipment[WeaponEquipment.slot] = obj
            player.equip(obj)
            player.equip(shield = null)
            EventBus.schedule(WeaponEquipedEvent(obj, player, world))
            EventBus.schedule(TwoHandEquipedEvent(obj, player, world))
        }
        is WeaponEquipment -> {
            player.topInterface.equipment[WeaponEquipment.slot] = obj
            player.equip(obj)
            EventBus.schedule(WeaponEquipedEvent(obj, player, world))
        }
        is ShieldEquipment -> {
            player.topInterface.equipment[ShieldEquipment.slot] = obj
            player.equip(obj)
            EventBus.schedule(ShieldEquipedEvent(obj, player, world))
        }
        is HeadEquipment -> {
            player.topInterface.equipment[HeadEquipment.slot] = obj
            player.equip(obj)
            EventBus.schedule(HeadEquipedEvent(obj, player, world))
        }
        is BodyEquipment -> {
            player.topInterface.equipment[BodyEquipment.slot] = obj
            player.equip(obj)
            EventBus.schedule(BodyEquipedEvent(obj, player, world))
        }
        is LegEquipment -> {
            player.topInterface.equipment[LegEquipment.slot] = obj
            player.equip(obj)
            EventBus.schedule(LegEquipedEvent(obj, player, world))
        }
        is AmmunitionEquipment -> {
            player.topInterface.equipment[AmmunitionEquipment.slot] = obj
            player.equip(obj)
            EventBus.schedule(AmmunitionEquipedEvent(obj, player, world))
        }
        is CapeEquipment -> {
            player.topInterface.equipment[CapeEquipment.slot] = obj
            player.equip(obj)
            EventBus.schedule(CapeEquipedEvent(obj, player, world))
        }
        is RingEquipment -> {
            player.topInterface.equipment[RingEquipment.slot] = obj
            player.equip(obj)
            EventBus.schedule(RingEquipedEvent(obj, player, world))
        }
        is NeckEquipment -> {
            player.topInterface.equipment[NeckEquipment.slot] = obj
            player.equip(obj)
            EventBus.schedule(NeckEquipedEvent(obj, player, world))
        }
        is HandEquipment -> {
            player.topInterface.equipment[HandEquipment.slot] = obj
            player.equip(obj)
            EventBus.schedule(HandEquipedEvent(obj, player, world))
        }
        is FeetEquipment -> {
            player.topInterface.equipment[FeetEquipment.slot] = obj
            player.equip(obj)
            EventBus.schedule(FeetEquipedEvent(obj, player, world))
        }
        else -> throw IllegalStateException("Object $obj should be equipment.")
    }
    EventBus.schedule(ObjEquipedEvent(obj as Equipment, player, world))
}