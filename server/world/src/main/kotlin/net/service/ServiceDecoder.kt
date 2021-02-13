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
package io.guthix.oldscape.server.net.service

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import java.io.IOException

class ServiceDecoder : ByteToMessageDecoder() {
    override fun decode(ctx: ChannelHandlerContext, inc: ByteBuf, out: MutableList<Any>) {
        if (!inc.isReadable) return
        inc.markReaderIndex()
        when (val opcode = inc.readUnsignedByte().toInt()) {
            ServiceType.GAME.opcode -> {
                out.add(GameConnectionRequest())
            }
            ServiceType.JS5.opcode -> {
                if (!inc.isReadable(4)) {
                    inc.resetReaderIndex()
                    return
                }
                out.add(Js5ConnectionRequest(inc.readInt()))
            }
            else -> throw IOException("Could not identify service with opcode $opcode.")
        }
    }
}