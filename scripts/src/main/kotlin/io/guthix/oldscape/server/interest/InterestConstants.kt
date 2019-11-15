package io.guthix.oldscape.server.interest

import io.guthix.oldscape.server.world.mapsquare.zone.ZoneDim
import io.guthix.oldscape.server.world.mapsquare.zone.tile.TileDim

object InterestConstants {
    val ZONE_INTEREST = ZoneDim(13)

    val ENTITY_INTEREST = TileDim(32)

    val ZONE_INTEREST_RANGE = ZONE_INTEREST / ZoneDim(2)

    val ENTITY_INTEREST_RANGE = ENTITY_INTEREST / TileDim(2)

    val ZONE_INTEREST_UPDATE = ZONE_INTEREST / ZoneDim(2) - (ENTITY_INTEREST / TileDim(2)).zone
}

