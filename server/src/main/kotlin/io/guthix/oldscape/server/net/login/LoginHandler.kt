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
package io.guthix.oldscape.server.net.login

import io.guthix.oldscape.server.net.PacketInboundHandler
import io.guthix.oldscape.server.net.StatusEncoder
import io.guthix.oldscape.server.net.StatusResponse
import io.guthix.oldscape.server.world.World
import io.netty.channel.ChannelHandlerContext

class LoginHandler(val world: World, val sessionId: Long) : PacketInboundHandler<LoginRequest>() {
    override fun channelRead0(ctx: ChannelHandlerContext, msg: LoginRequest) {
        if (msg.sessionId != sessionId) {
            ctx.writeAndFlush(StatusResponse.BAD_SESSION_ID)
            return
        }
        if (world.isFull) {
            ctx.writeAndFlush(StatusResponse.SERVER_FULL)
        }
        ctx.write(StatusResponse.NORMAL)
        ctx.pipeline().replace(StatusEncoder::class.qualifiedName, LoginEncoder::class.qualifiedName, LoginEncoder())
        msg.ctx = ctx
        world.loginQueue.add(msg)
    }
}