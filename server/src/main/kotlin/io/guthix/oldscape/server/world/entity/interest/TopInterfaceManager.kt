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

import io.guthix.oldscape.server.net.game.out.IfClosesubPacket
import io.guthix.oldscape.server.net.game.out.IfOpensubPacket
import io.guthix.oldscape.server.template.InventoryIds
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.entity.intface.IfComponent
import io.guthix.oldscape.server.world.entity.intface.Interface
import io.guthix.oldscape.server.world.entity.intface.SubInterface
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelHandlerContext

class TopInterfaceManager(
    ctx: ChannelHandlerContext,
    id: Int,
    var modalOpen: Boolean = false,
    var modalSlot: Int? = null,
    children: MutableMap<Int, IfComponent> = mutableMapOf()
) : Interface(ctx, id, Type.TOPLEVELINTERFACE, children) {
    val itemBag: InventoryManager = InventoryManager(InventoryIds.ITEM_BAG_93, INVENTORY_IFID, 0)

    val equipment: InventoryManager = InventoryManager(InventoryIds.EQUIPMENT_94)

    internal fun initialize() {
        itemBag.initialize()
        equipment.initialize()
    }

    internal fun synchronize(player: Player): List<ChannelFuture> {
        val futures = mutableListOf<ChannelFuture>()
        futures.addAll(itemBag.synchronize(player))
        futures.addAll(equipment.synchronize(player))
        return futures
    }

    internal fun postProcess() {
        itemBag.postProcess()
        equipment.postProcess()
    }

    fun openModal(id: Int, type: Type): SubInterface {
        check(modalSlot != null) { "Can't open modal interface on top interface ${this.id}." }
        return modalSlot?.let {
            val subInterface = SubInterface(ctx, id, type)
            modalOpen = true
            ctx.write(IfOpensubPacket(this.id, it, id, type.opcode))
            subInterface
        }!!
    }

    fun closeModal() {
        modalOpen = false
        modalSlot?.let { ctx.write(IfClosesubPacket(id, it)) }
    }

    companion object {
        const val INVENTORY_IFID: Int = 149
    }
}