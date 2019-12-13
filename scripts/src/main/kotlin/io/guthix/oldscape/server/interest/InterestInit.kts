package io.guthix.oldscape.server.interest

import io.guthix.oldscape.server.event.imp.LoginEvent
import io.guthix.oldscape.server.interfaces.LoginInterfaceEvent
import io.guthix.oldscape.server.world.entity.EntityAttribute
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.world.entity.player.Player

var Player.playerInterest by EntityAttribute<PlayerInterest>()

on(LoginEvent::class).then {
    player.playerInterest = PlayerInterest()
    player.playerInterest.localPlayers.add(player)
    val pZone = player.position.inZones
    val xteas = getInterestedXTEAS(pZone)
    val playersInWorld = mutableMapOf<Int, Player>()
    for(player in world.players) {
        playersInWorld[player.index] = player
    }
    player.setupInterestManager(playersInWorld, xteas)
    EventBus.scheduleEvent(LoginInterfaceEvent(), world, player)
    EventBus.scheduleEvent(StartMapSyncEvent(pZone), world, player)
    EventBus.scheduleEvent(StartPlayerSyncEvent(), world, player)
}