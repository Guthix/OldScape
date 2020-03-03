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
    var modal: Pair<Int, SubInterface>?,
    children: MutableMap<Int, IfComponent> = mutableMapOf()
) : Interface(ctx, id, Type.TOPLEVELINTERFACE, children) {
    fun openModal(slot: Int, subId: Int, type: Type): SubInterface {
        val subInterface = SubInterface(ctx, subId, type)
        modal = slot to subInterface
        ctx.write(IfOpensubPacket(id, slot, subId, type.opcode))
        return subInterface
    }

    fun closeModal() {
        check(modal != null) { "Can not close modal interface because there is no modal interface open." }
        modal?.let {
            ctx.write(IfClosesubPacket(id, it.first))
        }
        modal = null
    }
}