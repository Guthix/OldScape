/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.interest

import io.guthix.oldscape.server.api.Xtea
import io.guthix.oldscape.server.world.mapsquare.MapSquareUnit
import io.guthix.oldscape.server.world.mapsquare.zone.Zone
import io.guthix.oldscape.server.world.mapsquare.zone.ZoneUnit
import io.guthix.oldscape.server.world.mapsquare.zone.tile.tile
import io.guthix.oldscape.server.world.mapsquare.zone.zones

object MapInterestConstants {
    val ZONE_INTEREST = 13.zones

    val ENTITY_INTEREST = 32.tile

    val ZONE_INTEREST_RANGE = ZONE_INTEREST / 2.zones

    val ENTITY_INTEREST_RANGE = ENTITY_INTEREST / 2.tile

    val ZONE_INTEREST_UPDATE = ZONE_INTEREST_RANGE - ENTITY_INTEREST_RANGE.inZones
}

fun onTutorialIsland(mSquareX: MapSquareUnit, mSquareY: MapSquareUnit) =
    ((mSquareX.value == 48 || mSquareX.value == 49) && mSquareY.value == 48)
        || (mSquareX.value == 48 && mSquareX.value == 148)

val ZoneUnit.startMapInterest get() = (this - MapInterestConstants.ZONE_INTEREST_RANGE).inMapSquares

val ZoneUnit.endMapInterest get() = (this + MapInterestConstants.ZONE_INTEREST_RANGE).inMapSquares

fun getInterestedXTEAS(zone: Zone): List<IntArray> {
    val xteas = mutableListOf<IntArray>()
    for(mSquareX in zone.x.startMapInterest..zone.x.endMapInterest) {
        for(mSquareY in zone.y.startMapInterest..zone.y.endMapInterest) {
            if(onTutorialIsland(mSquareX, mSquareY)) continue
            val id = (mSquareX.value shl 8) or mSquareY.value
            val xtea = Xtea.key[id] ?: throw IllegalStateException(
                "Could not find XTEA for id $id."
            )
            xteas.add(xtea)
        }
    }
    return xteas
}

