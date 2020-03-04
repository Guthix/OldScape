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
package io.guthix.oldscape.server.world.entity.character.player.intface

import io.guthix.oldscape.server.net.state.game.outp.IfClosesubPacket
import io.guthix.oldscape.server.net.state.game.outp.IfOpensubPacket
import io.guthix.oldscape.server.world.entity.character.player.intface.component.SubInterface
import io.netty.channel.ChannelHandlerContext

class TopInterface(
    ctx: ChannelHandlerContext,
    id: Int,
    var modalOpen: Boolean = false,
    var modalSlot: Int? = null,
    children: MutableMap<Int, IfComponent> = mutableMapOf()
) : Interface(ctx, id, Type.TOPLEVELINTERFACE, children) {
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
        modalSlot?.let {
            ctx.write(IfClosesubPacket(id, it))
        }
    }
}