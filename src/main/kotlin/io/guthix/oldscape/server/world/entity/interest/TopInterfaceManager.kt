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

import io.guthix.oldscape.server.net.game.out.IfClosesubPacket
import io.guthix.oldscape.server.net.game.out.IfOpensubPacket
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
    val inventory: InventoryManager = InventoryManager(93, 149, 0)

    val equipment: InventoryManager = InventoryManager(94)

    override fun initialize(world: World, player: Player) {
        inventory.initialize(world, player)
        equipment.initialize(world, player)
    }

    override fun synchronize(world: World, player: Player): List<ChannelFuture> {
        val futures = mutableListOf<ChannelFuture>()
        futures.addAll(inventory.synchronize(world, player))
        futures.addAll(equipment.synchronize(world, player))
        return futures
    }

    override fun postProcess() {
        inventory.postProcess()
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
}