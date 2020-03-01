package io.guthix.oldscape.server.pathing

import io.guthix.oldscape.server.event.imp.LocationClickEvent
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.pathing.type.DestinationLocation
import io.guthix.oldscape.server.pathing.type.imp.breadthFirstSearch
import io.guthix.oldscape.server.routine.NormalAction

on(LocationClickEvent::class).then {
    val loc = world.map.getLoc(event.id, player.position.floor, event.x, event.y) ?: throw IllegalStateException(
        "Could not find location at ${Tile(player.position.floor, event.x, event.y)}."
    )
    val destination = DestinationLocation(loc, world.map)
    player.path = breadthFirstSearch(player.position, destination, player.size, true, world.map)
    val end = player.path.last()
    player.setMapFlag(end.x, end.y)
    player.addRoutine(NormalAction) {
        wait{ destination.reached(player.position.x, player.position.y, player.size) }
        player.turnTo(loc)
        EventBus.schedule(LocationReachedEvent(loc), world, player)
    }
}