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
package io.guthix.oldscape.server.net.game.out

import io.guthix.buffer.writeStringCP1252
import io.guthix.oldscape.server.net.game.OutGameEvent
import io.guthix.oldscape.server.net.game.VarShortSize
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class RunclientscriptPacket(private val id: Int, vararg val params: Any) : OutGameEvent {
    override val opcode: Int = 49

    override val size: VarShortSize = VarShortSize

    override fun encode(ctx: ChannelHandlerContext): ByteBuf {
        val buf = ctx.alloc().buffer()
        val argumentListIdentifier = StringBuilder()
        for (param in params.reversed()) {
            if (param is String) {
                argumentListIdentifier.append("s")
            } else {
                argumentListIdentifier.append("i")
            }
        }
        buf.writeStringCP1252("$argumentListIdentifier")
        for (param in params) {
            if (param is String) {
                buf.writeStringCP1252(param)
            } else {
                buf.writeInt(param as Int)
            }
        }
        buf.writeInt(id)
        return buf
    }
}