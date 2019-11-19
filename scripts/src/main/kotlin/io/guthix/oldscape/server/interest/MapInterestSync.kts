package io.guthix.oldscape.server.interest

import io.guthix.oldscape.server.event.imp.LoginEvent
import io.guthix.oldscape.server.interest.InterestConstants.ZONE_INTEREST_UPDATE
import io.guthix.oldscape.server.interest.InterestConstants.ZONE_INTEREST_RANGE
import io.guthix.oldscape.server.world.mapsquare.MapSquareUnit
import io.guthix.oldscape.server.world.mapsquare.floor
import io.guthix.oldscape.server.world.mapsquare.zone.Zone
import io.guthix.oldscape.server.world.mapsquare.zone.zones
import io.guthix.oldscape.server.world.mapsquare.zone.ZoneUnit
import io.guthix.oldscape.server.net.state.game.outp.RebuildNormalPacket
import io.guthix.oldscape.server.XTEA

val ZoneUnit.startMapInterest get() = (this - ZONE_INTEREST_RANGE).inMapSquares

val ZoneUnit.endMapInterest get() = (this + ZONE_INTEREST_RANGE).inMapSquares

fun onTutorialIsland(mSquareX: MapSquareUnit, mSquareY: MapSquareUnit) =
    ((mSquareX.value == 48 || mSquareX.value == 49) && mSquareY.value == 48)
        || (mSquareX.value == 48 && mSquareX.value == 148)

on(LoginEvent::class).then {
    var lastUpdatedZone = Zone(0.floor, 0.zones, 0.zones)
    while(true) {
        if(lastUpdatedZone.withInDistanceOf(player.position.inZone, ZONE_INTEREST_UPDATE)) {
            println("Not sending new update")
            wait(ticks = 1)
            continue
        }
        println("Sending new update")
        val xteas = mutableListOf<IntArray>()
        val pZone = player.position.inZone
        for(mSquareX in pZone.x.startMapInterest..pZone.x.endMapInterest) {
            for(mSquareY in pZone.y.startMapInterest..pZone.y.endMapInterest) {
                if(onTutorialIsland(mSquareX, mSquareY)) continue
                val id = (mSquareX.value shl 8) or mSquareY.value
                val xtea = XTEA.key[id] ?: throw IllegalStateException(
                    "Could not find XTEA for id $id."
                )
                xteas.add(xtea)
            }
        }
        player.write(RebuildNormalPacket(xteas, player.position.inZone))
        lastUpdatedZone = player.position.inZone
        wait(ticks = 1)
    }
}

