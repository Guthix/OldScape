package io.guthix.oldscape.server.event

import io.guthix.oldscape.server.api.GameEvent
import io.guthix.oldscape.server.world.mapsquare.zone.tile.TileUnit

class ObjectReachedEvent(val id: Int, val x: TileUnit, val y: TileUnit) : GameEvent