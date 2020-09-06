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
import io.guthix.oldscape.server.plugin.InvalidMessageException
import io.guthix.oldscape.server.template.equipment
import io.guthix.oldscape.server.template.equipmentType
import io.guthix.oldscape.server.world.entity.Obj
import io.guthix.oldscape.server.world.entity.interest.PlayerManager

fun PlayerManager.EquipmentSet.unequip(equipmentType: PlayerManager.EquipmentType): Obj? =
    equipment.remove(equipmentType.slot)

on(InvObjClickEvent::class).then {
    val obj = player.topInterface.itemBag.remove(inventorySlot) ?: return@then
    obj.template.equipment?.let { (_, type,  coversHair, isFullBody, coversFace) ->
        type?.let {
            player.topInterface.equipment[it.slot] = obj
            player.equipmentSet.equipment[it.slot] = obj
        }
        coversFace?.let { player.equipmentSet.coversFace = it }
        coversHair?.let { player.equipmentSet.coversFace = it }
        isFullBody?.let { player.equipmentSet.coversFace = it }
    } ?: throw InvalidMessageException("No equipment defined for $obj.")
    EventBus.schedule(ObjEquipedEvent(obj, player, world))
}