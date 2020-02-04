package io.guthix.oldscape.server.pathing

import io.guthix.oldscape.server.event.imp.MapClickEvent
import io.guthix.oldscape.server.event.imp.MiniMapClickEvent
import io.guthix.oldscape.server.world.entity.character.player.Player
import io.guthix.oldscape.server.world.mapsquare.zone.tile.TileUnit
import io.guthix.oldscape.server.world.mapsquare.FloorUnit
import io.guthix.oldscape.server.world.WorldMap

on(MiniMapClickEvent::class).then {
    player.startWalkingToTile(player.position.floor, event.x, event.y, world.map)
}

on(MapClickEvent::class).then {
    player.startWalkingToTile(player.position.floor, event.x, event.y, world.map)
}

fun Player.startWalkingToTile(floor: FloorUnit, x: TileUnit, y: TileUnit, map: WorldMap) {
    val destination = DestinationTile(floor, x, y)
    val path = breadthFirstSearch(position, destination, size, true, map)
    startWalkingOn(path)
}