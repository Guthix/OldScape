package io.guthix.oldscape.server.pathing

import io.guthix.oldscape.server.event.imp.LocationClickEvent
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import io.guthix.oldscape.server.event.EventBus

on(LocationClickEvent::class).then {
    val loc = world.getLocation(event.id, player.position.floor, event.x, event.y) ?: throw IllegalStateException(
        "Could not find location at ${Tile(player.position.floor, event.x, event.y)}."
    )
    val destination = DestinationLocation(loc, world)
    val path = breadthFirstSearch(player.position, destination, player.size, true, world)
    player.startWalkingOn(path) {
        player.turnTo(loc)
        EventBus.schedule(LocationReachedEvent(loc), world, player)
    }
}