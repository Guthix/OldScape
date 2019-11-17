package io.guthix.oldscape.server.interest

import io.guthix.oldscape.server.world.mapsquare.zone.tile.td
import io.guthix.oldscape.server.world.mapsquare.zone.zd

object InterestConstants {
    val ZONE_INTEREST = 13.zd

    val ENTITY_INTEREST = 32.td

    val ZONE_INTEREST_RANGE = ZONE_INTEREST / 2.zd

    val ENTITY_INTEREST_RANGE = ENTITY_INTEREST / 2.td

    val ZONE_INTEREST_UPDATE = ZONE_INTEREST_RANGE - ENTITY_INTEREST_RANGE.zd
}

