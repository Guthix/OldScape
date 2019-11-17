import io.guthix.oldscape.server.event.imp.LoginEvent
import io.guthix.oldscape.server.interest.InterestConstants.ZONE_INTEREST_UPDATE
import io.guthix.oldscape.server.interest.InterestConstants.ZONE_INTEREST_RANGE
import io.guthix.oldscape.server.world.mapsquare.zone.ZoneDim

on(LoginEvent::class).then {
    var lastUpdatedZone = player.position.zone
    while(true) {
        if(lastUpdatedZone.withInDistanceOf(player.position.zone, ZONE_INTEREST_UPDATE)) continue
        val curZone = player.position.zone
        lastUpdatedZone = player.position.zone
        wait(ticks = 1)
    }
}

fun endMapSquare(zone: ZoneDim) = (zone + ZONE_INTEREST_RANGE).md

fun startMapSquare(zone: ZoneDim) = (zone - ZONE_INTEREST_RANGE).md