package io.guthix.oldscape.server.event.imp

import io.guthix.oldscape.server.event.GameEvent
import io.guthix.oldscape.server.world.mapsquare.zone.tile.TileUnit

class MiniMapClickEvent(
    val x: TileUnit,
    val y: TileUnit,
    val type: Int,
    val mouseDx: Int,
    val mouseDy: Int,
    val angle: Int,
    val playerX: TileUnit,
    val playerY: TileUnit
) : GameEvent