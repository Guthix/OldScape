package io.guthix.oldscape.server.pathing

import io.guthix.oldscape.server.event.imp.MapClickEvent
import io.guthix.oldscape.server.event.imp.MiniMapClickEvent
import io.guthix.oldscape.server.pathing.type.DestinationTile
import io.guthix.oldscape.server.pathing.type.imp.breadthFirstSearch
import io.guthix.oldscape.server.world.entity.character.player.Player
import io.guthix.oldscape.server.world.mapsquare.zone.tile.TileUnit
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import io.guthix.oldscape.server.world.mapsquare.FloorUnit
import io.guthix.oldscape.server.world.WorldMap
import io.guthix.oldscape.server.routine.NormalAction

on(MiniMapClickEvent::class).then {
    player.startWalkingToTile(player.position.floor, event.x, event.y, world.map)
}

on(MapClickEvent::class).then {
    player.startWalkingToTile(player.position.floor, event.x, event.y, world.map)
}

fun Player.startWalkingToTile(floor: FloorUnit, x: TileUnit, y: TileUnit, map: WorldMap) {
    path = breadthFirstSearch(position, DestinationTile(floor, x, y), size, true, map)
    val end = path.last()
    if(end != Tile(floor, x, y)) {
        setMapFlag(end.x, end.y)
    }
    turnToLock(null) // TODO make this better
    routines.remove(NormalAction)
}