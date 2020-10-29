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

import io.guthix.oldscape.server.net.game.out.SetPlayerOpPacket
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Player
import io.netty.channel.ChannelFuture

class ContextMenuManager {
    var contextMenu: Array<String> = arrayOf("Follow", "Trade with", "Report")

    var changes: MutableMap<Int, String> = mutableMapOf()

    internal fun initialize(player: Player) {
        contextMenu.forEachIndexed { i, text ->
            player.ctx.write(SetPlayerOpPacket(false, i + 1, text))
        }
    }

    internal fun synchronize(player: Player): List<ChannelFuture> = changes.map { (slot, text) ->
        player.ctx.write(SetPlayerOpPacket(false, slot + 1, text))
    }

    internal fun postProcess(): Unit = changes.clear()
}