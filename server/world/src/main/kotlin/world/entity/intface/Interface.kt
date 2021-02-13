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
package io.guthix.oldscape.server.world.entity.intface

import io.guthix.oldscape.server.net.game.out.IfClosesubPacket
import io.guthix.oldscape.server.net.game.out.IfOpensubPacket
import io.guthix.oldscape.server.net.game.out.IfSettextPacket
import io.netty.channel.ChannelHandlerContext

abstract class Interface(
    protected val ctx: ChannelHandlerContext,
    var id: Int,
    val type: Type,
    var children: MutableMap<Int, IfComponent> = mutableMapOf()
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