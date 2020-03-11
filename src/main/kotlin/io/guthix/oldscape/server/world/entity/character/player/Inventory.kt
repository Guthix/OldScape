/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.world.entity.character.player

import io.guthix.oldscape.server.api.blueprint.InventoryBlueprints
import io.guthix.oldscape.server.world.entity.Obj

class Inventory(
    val player: Player,
    val id: Int,
    val interfaceId: Int = -1,
    val slotId: Int = 0
) {
    val objs: Array<Obj?> = arrayOfNulls(InventoryBlueprints[id].capacity)

    val maxSize get() = objs.size

    private var amountInInventory = objs.count { it != null }

    private val objsToUpdate = mutableMapOf<Int, Obj?>()

    fun setObject(obj: Obj) {
        if(obj.blueprint.stackable) {
            val slot = objs.indexOfFirst { it?.blueprint?.id == obj.blueprint.id }
            if(slot == -1) { // obj not already in inventory
                addNextSlot(obj)
            } else {
                val iObj = objs[slot] ?: error("No object in slot $slot of inventory $interfaceId")
                iObj.quantity += obj.quantity
                objsToUpdate[slot] = iObj
            }
        } else {
            addNextSlot(obj)
        }
    }

    fun addNextSlot(obj: Obj) = setObject(objs.indexOfFirst { it == null }, obj)

    fun setObject(slot: Int, obj: Obj) {
        require(slot in 0 until maxSize && amountInInventory != maxSize)
        objs[slot] = obj
        objsToUpdate[slot] = obj
        amountInInventory++
    }

    fun removeObject(slot: Int) {
        val obj = objs[slot]
        objs[slot] = null
        objsToUpdate[slot] = obj
        amountInInventory--
    }

    fun update() {
        if(objsToUpdate.isNotEmpty()) {
            if(objsToUpdate.size == amountInInventory) {
                player.addFullInventory(interfaceId, slotId, id, objs.toList())
            } else {
                player.addPartialInventory(interfaceId, slotId, id, objsToUpdate.toMap())
            }
            objsToUpdate.clear()
        }
    }

    fun release() {
        player.releaseInvMemory(id)
    }
}