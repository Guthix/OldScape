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
package io.guthix.oldscape.server.net.login

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class LoginEncoder : MessageToByteEncoder<LoginResponse>() {
    override fun encode(ctx: ChannelHandlerContext, msg: LoginResponse, out: ByteBuf) {
        out.writeByte(13)
        out.writeBoolean(false)
        out.writeInt(0)
        out.writeByte(msg.playerRights)
        out.writeBoolean(true)
        out.writeShort(msg.playerIndex)
        out.writeBoolean(false)
    }
}