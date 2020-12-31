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
package io.guthix.oldscape.server.world.entity.interest

import io.guthix.oldscape.server.net.game.out.UpdateRunenergyPacket
import io.guthix.oldscape.server.net.game.out.UpdateRunweightPacket
import io.guthix.oldscape.server.world.entity.Player
import io.netty.channel.ChannelFuture

class EnergyManager {
    private var weightChanged = false

    var weight: Int = 50
        set(value) {
            weightChanged = true
            field = value
        }

    private var energyChanged = false

    var energy: Int = 100
        set(value) {
            energyChanged = true
            field = value
        }

    internal fun initialize(player: Player) {
        player.ctx.write(UpdateRunweightPacket(weight))
        player.ctx.write(UpdateRunenergyPacket(energy))
    }

    internal fun synchronize(player: Player): List<ChannelFuture> {
        val futures = mutableListOf<ChannelFuture>()
        if (weightChanged) futures.add(player.ctx.write(UpdateRunweightPacket(weight)))
        if (energyChanged) futures.add(player.ctx.write(UpdateRunenergyPacket(energy)))
        return futures
    }

    internal fun postProcess() {
        weightChanged = false
        energyChanged = false
    }
}