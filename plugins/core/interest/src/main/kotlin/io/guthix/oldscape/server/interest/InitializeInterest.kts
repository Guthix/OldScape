package io.guthix.oldscape.server.interest

import io.guthix.oldscape.server.event.imp.LoginEvent
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.world.entity.character.player.Player

import io.guthix.oldscape.server.event.imp.PlayerInitialized

on(LoginEvent::class).then {
    player.mapInterest = MapInterest()
    val pZone = player.position.inZones
    val xteas = MapInterest.getInterestedXteas(pZone)

    player.playerInterest = PlayerInterest()
    player.playerInterest.localPlayers.add(player)
    val playersInWorld = mutableMapOf<Int, Player>()
    for(player in world.players) {
        playersInWorld[player.index] = player
    }

    player.initializeInterest(playersInWorld, xteas)
    EventBus.schedule(PlayerInitialized(), world, player)
}