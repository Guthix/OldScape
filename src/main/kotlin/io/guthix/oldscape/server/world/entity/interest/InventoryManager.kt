/**
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
package io.guthix.oldscape.server.world.entity.interest

import io.guthix.oldscape.server.api.InventoryBlueprints
import io.guthix.oldscape.server.net.game.out.UpdateInvClearPacket
import io.guthix.oldscape.server.net.game.out.UpdateInvFullPacket
import io.guthix.oldscape.server.net.game.out.UpdateInvPartialPacket
import io.guthix.oldscape.server.net.game.out.UpdateInvStopTransmitPacket
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
    private val inventoryId: Int,
    private val interfaceId: Int = -1,
    private val interfaceSlotId: Int = 0,
    private val objs: Array<Obj?> = arrayOfNulls(InventoryBlueprints[inventoryId].capacity)
) : InterestManager {
    private val maxSize get() = objs.size

    private var objCount = objs.count { it != null }

    private val changes = mutableMapOf<Int, Obj?>()

    fun setObject(obj: Obj) {
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

    fun addNextSlot(obj: Obj): Unit = setObject(objs.indexOfFirst { it == null }, obj)

    fun setObject(slot: Int, obj: Obj) {
        require(slot in 0 until maxSize && objCount != maxSize)
        objs[slot] = obj
        changes[slot] = obj
        objCount++
    }

    fun removeObject(slot: Int): Obj? {
        val obj = objs[slot]
        objs[slot] = null
        changes[slot] = null
        objCount--
        return obj
    }

    fun release(player: Player) {
        player.ctx.write(UpdateInvStopTransmitPacket(inventoryId))
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
                    UpdateInvFullPacket(interfaceId, interfaceSlotId, inventoryId, objs.toList())
                ))
            } else {
                futures.add(player.ctx.write(
                    UpdateInvPartialPacket(interfaceId, interfaceSlotId, inventoryId, changes.toMap())
                ))
            }
            changes.clear()
        }
        return futures
    }

    override fun postProcess() {}
}