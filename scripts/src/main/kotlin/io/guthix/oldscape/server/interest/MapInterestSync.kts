import io.guthix.oldscape.server.event.imp.LoginEvent
import io.guthix.oldscape.server.interest.InterestConstants.ZONE_INTEREST_UPDATE

on(LoginEvent::class).then {
    var lastUpdatedZone = player.position.zone
    while(true) {
        if(lastUpdatedZone.withInDistanceOf(player.position.zone, ZONE_INTEREST_UPDATE)) continue
        val mapSquare = player.position.zone.mapSquare

        lastUpdatedZone = player.position.zone
        wait(ticks = 1)
    }
}