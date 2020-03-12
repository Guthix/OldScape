package io.guthix.oldscape.server.event

import io.guthix.oldscape.server.event.script.InGameEvent
import io.guthix.oldscape.server.net.state.game.ClientEvent
import io.guthix.oldscape.server.world.mapsquare.zone.tile.TileUnit

class ObjectReachedEvent(val id: Int, val x: TileUnit, val y: TileUnit) : InGameEvent