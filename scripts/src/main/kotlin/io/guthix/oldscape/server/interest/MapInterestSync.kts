package io.guthix.oldscape.server.interest

import io.guthix.oldscape.server.interest.MapInterestConstants.ZONE_INTEREST_UPDATE
import io.guthix.oldscape.server.net.state.game.outp.RebuildNormalPacket

on(StartMapSyncEvent::class).then {
    println("Map update sync")
//    var lastZone = event.lastUpdatedZone
//    while(true) {
//        val pZone = player.position.inZones
//        if(lastZone.withInDistanceOf(pZone, ZONE_INTEREST_UPDATE)) {
//            wait(ticks = 1)
//            continue
//        }
//        val xteas = getInterestedXTEAS(pZone)
//        player.write(RebuildNormalPacket(xteas, player.position.inZones))
//        lastZone = player.position.inZones
//        wait(ticks = 1)
//    }
}

