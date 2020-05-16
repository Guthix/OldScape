package io.guthix.oldscape.server.world.entity.interest

import io.guthix.oldscape.server.net.game.out.UpdateRunenergyPacket
import io.guthix.oldscape.server.net.game.out.UpdateRunweightPacket
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Player
import io.netty.channel.ChannelFuture

class EnergyManager : InterestManager {
    private var weightChanged = false

    var weight = 50
        set(value) {
            weightChanged = true
            field = value
        }

    private var energyChanged = false

    var energy = 100
        set(value) {
            energyChanged = true
            field = value
        }

    override fun initialize(world: World, player: Player) {
        player.ctx.write(UpdateRunweightPacket(weight))
        player.ctx.write(UpdateRunenergyPacket(energy))
    }

    override fun synchronize(world: World, player: Player): List<ChannelFuture> {
        val futures = mutableListOf<ChannelFuture>()
        if (weightChanged) futures.add(player.ctx.write(UpdateRunweightPacket(weight)))
        if (energyChanged) futures.add(player.ctx.write(UpdateRunenergyPacket(energy)))
        return futures
    }

    override fun postProcess() {
        weightChanged = false
        energyChanged = false
    }
}