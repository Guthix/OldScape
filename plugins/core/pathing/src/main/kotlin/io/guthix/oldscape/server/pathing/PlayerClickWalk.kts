package io.guthix.oldscape.server.pathing

import io.guthix.oldscape.server.event.imp.MapClickEvent
import io.guthix.oldscape.server.event.imp.MiniMapClickEvent
import io.guthix.oldscape.server.world.entity.character.player.Player
import io.guthix.oldscape.server.world.mapsquare.zone.tile.TileUnit
import io.guthix.oldscape.server.world.mapsquare.FloorUnit
import io.guthix.oldscape.server.world.World

on(MiniMapClickEvent::class).then {
    player.startWalkingToTile(player.position.floor, event.x, event.y, world)
}

on(MapClickEvent::class).then {
    player.startWalkingToTile(player.position.floor, event.x, event.y, world)
}

fun Player.startWalkingToTile(floor: FloorUnit, x: TileUnit, y: TileUnit, world: World) {
    val destination = DestinationTile(floor, x, y)
    val path = breadthFirstSearch(position, destination, size, true, world)
    startWalkingOn(path)
}