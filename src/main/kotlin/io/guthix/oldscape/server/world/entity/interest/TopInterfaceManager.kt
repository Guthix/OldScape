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
import io.guthix.oldscape.server.content.InventoryTemplates
import io.guthix.oldscape.server.world.World
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
) : Interface(ctx, id, Type.TOPLEVELINTERFACE, children), InterestManager {
    val itemBag: InventoryManager = InventoryManager(InventoryTemplates.ITEM_BAG_93, INVENTORY_IFID, 0)

    val equipment: InventoryManager = InventoryManager(InventoryTemplates.EQUIPMENT_94)

    override fun initialize(world: World, player: Player) {
        itemBag.initialize(world, player)
        equipment.initialize(world, player)
    }

    override fun synchronize(world: World, player: Player): List<ChannelFuture> {
        val futures = mutableListOf<ChannelFuture>()
        futures.addAll(itemBag.synchronize(world, player))
        futures.addAll(equipment.synchronize(world, player))
        return futures
    }

    override fun postProcess() {
        itemBag.postProcess()
        equipment.postProcess()
    }

    fun openModal(subId: Int, type: Type): SubInterface {
        check(modalSlot != null) { "Can't open modal interface on top interface $id." }
        return modalSlot?.let {
            val subInterface = SubInterface(ctx, subId, type)
            modalOpen = true
            ctx.write(IfOpensubPacket(id, it, subId, type.opcode))
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