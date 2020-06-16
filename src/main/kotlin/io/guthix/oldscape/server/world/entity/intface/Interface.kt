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
package io.guthix.oldscape.server.world.entity.intface

import io.guthix.oldscape.server.net.game.out.IfClosesubPacket
import io.guthix.oldscape.server.net.game.out.IfOpensubPacket
import io.guthix.oldscape.server.net.game.out.IfSettextPacket
import io.netty.channel.ChannelHandlerContext

abstract class Interface(
    protected val ctx: ChannelHandlerContext,
    val id: Int,
    val type: Type,
    val children: MutableMap<Int, IfComponent> = mutableMapOf()
) {
    fun openSubInterface(slot: Int, subId: Int, type: Type): SubInterface {
        val subInterface = SubInterface(ctx, subId, type)
        children[slot] = subInterface
        ctx.write(IfOpensubPacket(id, slot, subId, type.opcode))
        return subInterface
    }

    fun setText(slot: Int, text: String): Interface {
        children[slot] = TextComponent(text)
        ctx.write(IfSettextPacket(id, slot, text))
        return this
    }

    fun closeComponent(slot: Int): IfComponent? {
        ctx.write(IfClosesubPacket(id, slot))
        return children.remove(slot)
    }

    enum class Type(val opcode: Int) {
        OVERLAYINTERFACE(0), CLIENTINTERFACE(1), TOPLEVELINTERFACE(2)
    }
}