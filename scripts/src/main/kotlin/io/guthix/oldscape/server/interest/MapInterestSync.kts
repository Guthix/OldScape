import io.guthix.oldscape.server.event.imp.LoginEvent
import io.guthix.oldscape.server.interest.InterestConstants.ZONE_INTEREST_UPDATE
import io.guthix.oldscape.server.interest.InterestConstants.ZONE_INTEREST_RANGE
import io.guthix.oldscape.server.world.mapsquare.MapSquareDim
import io.guthix.oldscape.server.world.mapsquare.md
import io.guthix.oldscape.server.world.mapsquare.zone.ZoneDim
import io.guthix.oldscape.server.net.state.game.outp.RebuildNormalPacket
import io.guthix.oldscape.server.XTEA

val ZoneDim.startMapInterest get() = (this - ZONE_INTEREST_RANGE).md

val ZoneDim.endMapInterest get() = (this + ZONE_INTEREST_RANGE).md

fun onTutorialIsland(mSquareX: MapSquareDim, mSquareY: MapSquareDim) =
    ((mSquareX == 48.md || mSquareX == 49.md) && mSquareY == 48.md) || (mSquareX == 48.md && mSquareX == 148.md)

on(LoginEvent::class).then {
    var lastUpdatedZone = player.position.zone
    while(true) {
        if(lastUpdatedZone.withInDistanceOf(player.position.zone, ZONE_INTEREST_UPDATE)) continue
        val xteas = mutableListOf<IntArray>()
        val pZone = player.position.zone
        for(mSquareX in pZone.x.startMapInterest..pZone.x.endMapInterest) {
            for(mSquareY in pZone.y.startMapInterest..pZone.y.endMapInterest) {
                if(onTutorialIsland(mSquareX, mSquareY)) continue
                val id = (mSquareX.dim shl 8) or mSquareY.dim
                val xtea = XTEA.key[id] ?: throw IllegalStateException(
                    "Could not find XTEA for id $id."
                )
                xteas.add(xtea)
            }
        }
        player.write(RebuildNormalPacket(xteas, player.position.zone))
        lastUpdatedZone = player.position.zone
        wait(ticks = 1)
    }
}

