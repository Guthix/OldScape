import io.guthix.oldscape.server.event.imp.LoginEvent
import io.guthix.oldscape.server.world.mapsquare.zone.ZoneDim

val ZONE_INTEREST = ZoneDim(13)

val ZONE_INTEREST_RANGE = ZONE_INTEREST / ZoneDim(2)

on(LoginEvent::class).then {
    var lastUpdatedZone = player.position.zone
    while(true) {
        if(lastUpdatedZone.withInDistanceOf(player.position.zone, ZONE_INTEREST_RANGE)) continue
        val mapSquare = player.position.zone.mapSquare

        lastUpdatedZone = player.position.zone
        wait(ticks = 1)
    }
}