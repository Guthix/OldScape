package io.guthix.oldscape.server.interest

import io.guthix.oldscape.server.event.imp.LoginEvent
import io.guthix.oldscape.server.world.entity.EntityAttribute
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.world.entity.player.Player
import io.guthix.oldscape.server.net.state.game.outp.InterestInitPacket
import io.guthix.oldscape.server.net.state.game.outp.IfOpentopPacket
import io.guthix.oldscape.server.net.state.game.outp.IfOpensubPacket
import io.guthix.oldscape.server.net.state.game.outp.IfSettext

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
    player.write(InterestInitPacket(player, playersInWorld, xteas, player.position.inZones))
    player.write(IfOpentopPacket(165))
    player.write(IfOpensubPacket(165, 1, 162, true))
    player.write(IfOpensubPacket(165, 2, 651, true))
    player.write(IfOpensubPacket(165, 24, 163, true))
    player.write(IfOpensubPacket(165, 25, 160, true))
    player.write(IfOpensubPacket(165, 28, 378, false))
    player.write(IfSettext(378, 75, "You have a Bank PIN!"))
    player.write(IfSettext(378, 7, "The Twisted League is here! Can you rise above the rest in this area-locked seasonal challenge?<br> Log in to a League World to find out!"))

    player.write(IfOpensubPacket(165, 10, 320, true))
    player.write(IfOpensubPacket(165, 11, 629, true))
    player.write(IfOpensubPacket(629, 33, 399, true))
    player.write(IfOpensubPacket(165, 12, 149, true))
    player.write(IfOpensubPacket(165, 13, 387, true))
    player.write(IfOpensubPacket(165, 14, 541, true))
    player.write(IfOpensubPacket(165, 15, 218, true))
    player.write(IfOpensubPacket(165, 18, 429, true))

    player.write(IfOpensubPacket(165, 17, 109, true))
    player.write(IfOpensubPacket(165, 19, 182, true))
    player.write(IfOpensubPacket(165, 20, 261, true))
    player.write(IfOpensubPacket(165, 21, 216, true))
    player.write(IfOpensubPacket(165, 22, 239, true))
    player.write(IfOpensubPacket(165, 16, 7, true))
    player.write(IfOpensubPacket(165, 9, 593, true))
    player.write(IfOpensubPacket(165, 25, 160, true))

    EventBus.scheduleEvent(StartMapSyncEvent(pZone), world, player)
    EventBus.scheduleEvent(StartPlayerSyncEvent(), world, player)
}