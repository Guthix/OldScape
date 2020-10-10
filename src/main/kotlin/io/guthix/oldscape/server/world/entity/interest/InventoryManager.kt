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
package io.guthix.oldscape.server.world.entity.interest

import io.guthix.oldscape.server.net.game.out.UpdateInvClearPacket
import io.guthix.oldscape.server.net.game.out.UpdateInvFullPacket
import io.guthix.oldscape.server.net.game.out.UpdateInvPartialPacket
import io.guthix.oldscape.server.net.game.out.UpdateInvStopTransmitPacket
import io.guthix.oldscape.server.template.InventoryTemplate
import io.guthix.oldscape.server.template.ObjTemplate
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Obj
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.entity.intface.Interface
import io.netty.channel.ChannelFuture

/**
 * Manages [Player] inventories. An inventory is an [Interface] that holds [Obj]s. Two types of interfaces exists,
 * the old format (if1) which require an [interfaceId] and [interfaceSlotId] to be passed and the newer version (if3)
 * which doesn't need this.
 */
class InventoryManager(
    private val inventory: InventoryTemplate,
    private val interfaceId: Int = -1,
    private val interfaceSlotId: Int = 0,
    private val objs: Array<Obj?> = arrayOfNulls(inventory.capacity)
) : InterestManager {
    private val maxSize get() = objs.size

    private var objCount = objs.count { it != null }

    private val changes = mutableMapOf<Int, Obj?>()

    fun add(template: ObjTemplate, amount: Int): Obj {
        val obj = Obj(template, amount)
        add(obj)
        return obj
    }

    fun add(obj: Obj) {
        if (obj.isStackable) {
            val slot = objs.indexOfFirst { it?.id == obj.id }
            if (slot == -1) { // obj not already in inventory
                addNextSlot(obj)
            } else {
                val iObj = objs[slot] ?: error("No object in slot $slot of inventory $interfaceId.")
                iObj.quantity += obj.quantity
                changes[slot] = iObj
            }
        } else {
            addNextSlot(obj)
        }
    }

    private fun addNextSlot(obj: Obj): Boolean {
        val slot = objs.indexOfFirst { it == null }
        return if(slot == -1) {
            false
        } else {
            set(slot, obj)
            objCount++
            true
        }
    }

    fun remove(slot: Int): Obj? {
        val obj = objs[slot] ?: return null
        resetSlot(slot)
        return obj
    }

    fun remove(objTemplate: ObjTemplate): Obj? {
        val slot = objs.indexOfFirst { it?.template == objTemplate }
        return if(slot == -1) null else remove(slot)
    }

    fun remove(slot: Int, amount: Int): Obj? {
        val obj = objs[slot] ?: return null
        return when {
            obj.quantity < amount -> null
            obj.quantity == amount -> {
                resetSlot(slot)
                obj
            }
            else -> {
                obj.quantity -= amount
                changes[slot] = obj
                obj
            }
        }
    }

    fun remove(objTemplate: ObjTemplate, amount: Int): Obj? {
        val slot = objs.indexOfFirst { it?.template == objTemplate }
        return if(slot == -1) null else remove(slot, amount)
    }

    private fun resetSlot(slot: Int) {
        objs[slot] = null
        changes[slot] = null
        objCount--
    }

    operator fun get(slot: Int): Obj? = objs[slot]

    operator fun set(slot: Int, obj: Obj) {
        require(slot in 0 until maxSize && objCount != maxSize)
        if (obj.quantity <= 0) {
            resetSlot(slot)
            return
        }
        objs[slot] = obj
        changes[slot] = obj
    }

    /**
     * Moves an [Obj].
     * Doesn't write the result to the client.
     */
    fun move(fromSlot: Int, toSlot: Int) {
        val toObj = objs[toSlot]
        objs[toSlot] = objs[fromSlot]
        objs[fromSlot] = toObj
    }

    fun release(player: Player) {
        player.ctx.write(UpdateInvStopTransmitPacket(inventory.id))
    }

    fun clear(player: Player) {
        player.ctx.write(UpdateInvClearPacket(interfaceId, interfaceSlotId))
    }

    override fun initialize(world: World, player: Player): Unit = objs.forEachIndexed { i, obj -> changes[i] = obj }

    override fun synchronize(world: World, player: Player): List<ChannelFuture> {
        val futures = mutableListOf<ChannelFuture>()
        if (changes.isNotEmpty()) {
            if (changes.size == objCount) { // TODO use better heuristic
                futures.add(player.ctx.write(
                    UpdateInvFullPacket(interfaceId, interfaceSlotId, inventory.id, objs.toList())
                ))
            } else {
                futures.add(player.ctx.write(
                    UpdateInvPartialPacket(interfaceId, interfaceSlotId, inventory.id, changes.toMap())
                ))
            }
            changes.clear()
        }
        return futures
    }

    override fun postProcess() {}
}