package io.guthix.oldscape.server.interest

import io.guthix.oldscape.server.event.imp.LoginEvent
import io.guthix.oldscape.server.world.entity.EntityAttribute
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.world.entity.player.Player
import io.guthix.oldscape.server.net.state.game.outp.InterestInitPacket

var Player.playerInterest by EntityAttribute<PlayerInterest>()

on(LoginEvent::class).then {
    player.playerInterest = PlayerInterest()
    player.playerInterest.localPlayers.add(player)
    val pZone = player.position.inZones
    val xteas = getInterestedXTEAS(pZone)
    player.write(InterestInitPacket(player, world.players, xteas, player.position.inZones))
    EventBus.scheduleEvent(StartMapSyncEvent(pZone), world, player)
    EventBus.scheduleEvent(StartPlayerSyncEvent(), world, player)
}