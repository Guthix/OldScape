package io.guthix.oldscape.location.tutorialisland

import io.guthix.oldscape.server.world.mapsquare.MapSquareUnit

fun onTutorialIsland(mSquareX: MapSquareUnit, mSquareY: MapSquareUnit) =
    ((mSquareX.value == 48 || mSquareX.value == 49) && mSquareY.value == 48)
        || (mSquareX.value == 48 && mSquareX.value == 148)