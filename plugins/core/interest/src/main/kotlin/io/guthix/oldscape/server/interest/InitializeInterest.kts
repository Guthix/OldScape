package io.guthix.oldscape.server.interest

import io.guthix.oldscape.server.event.imp.LoginEvent
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.routine.FinalRoutine
import io.guthix.oldscape.server.world.entity.character.player.interest.MapInterest
import io.guthix.oldscape.server.world.entity.character.player.Player

import io.guthix.oldscape.server.event.imp.PlayerInitialized

on(LoginEvent::class).then {
    val pZone = player.position.inZones
    val xteas = MapInterest.getInterestedXteas(pZone)
    player.playerInterest.lastLocalInActivePlayers.add(player)
    val playersInWorld = mutableMapOf<Int, Player>()
    for(player in world.players) {
        playersInWorld[player.index] = player
    }
    player.initializeInterest(playersInWorld, xteas)
    player.addRoutine(FinalRoutine) {
        while(true) {
            player.playerInterestSync()
            wait(ticks = 1)
        }
    }
    EventBus.schedule(PlayerInitialized(), world, player)
}