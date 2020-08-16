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
import io.guthix.oldscape.server.plugin.InvalidClientMessageException

on(InvObjClickEvent::class).then {
    val obj = player.topInterface.itemBag.remove(inventorySlot) ?: return@then
    val slot = obj.equipmentType?.slot ?: throw InvalidClientMessageException(
        "Obj $obj has no equipment type."
    )
    player.topInterface.equipment[slot] = obj
    player.equipment.equip(obj)
    EventBus.schedule(ObjEquipedEvent(obj, player, world))
}