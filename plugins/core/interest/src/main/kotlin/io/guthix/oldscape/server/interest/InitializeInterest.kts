package io.guthix.oldscape.server.interest

import io.guthix.oldscape.server.event.imp.LoginEvent
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.net.state.game.outp.PlayerInfoPacket
import io.guthix.oldscape.server.routine.FinalRoutine
import io.guthix.oldscape.server.world.entity.character.player.interest.MapInterest
import io.guthix.oldscape.server.event.imp.PlayerInitialized

on(LoginEvent::class).then {
    val pZone = world.getZone(player.position) ?: throw IllegalStateException("Player location can't be null")
    val xteas = MapInterest.getInterestedXteas(pZone, world.map)
    player.updateFlags.add(PlayerInfoPacket.appearance)
    player.playerInterest.initialize(player, world.players)
    player.initializeInterest(world.players, xteas)
    player.addRoutine(FinalRoutine) {
        while(true) {
            player.playerInterestSync(world.players)
            wait(ticks = 1)
        }
    }
    EventBus.schedule(PlayerInitialized(), world, player)
}