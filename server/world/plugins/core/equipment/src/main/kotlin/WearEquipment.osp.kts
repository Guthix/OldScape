/*
 * Copyright 2018-2021 Guthix
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
package io.guthix.oldscape.server.core.equipment

import io.guthix.oldscape.server.event.ButtonClickEvent
import io.guthix.oldscape.server.event.InvObjClickEvent
import io.guthix.oldscape.server.plugin.invalidMessage

on(InvObjClickEvent::class).where {
    interfaceId == player.itemBag.interfaceId && (contextMenuEntry == "Wield" || contextMenuEntry == "Wear")
}.then {
    val obj = player.itemBag.removeFromSlot(inventorySlot) ?: return@then
    player.equip(world, obj)
}

on(ButtonClickEvent::class).where { interfaceId == 387 }.then {
    val slot = buttonToSlots[buttonId] ?: invalidMessage(
        "Button $buttonId does not exists on interface $interfaceId"
    )
    val obj = player.unequip(slot, world)
    obj?.let { player.itemBag.add(obj) }
}