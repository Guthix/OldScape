package io.guthix.oldscape.server.pathing

import io.guthix.oldscape.server.event.imp.PlayerOpEvent
import io.guthix.oldscape.server.pathing.type.DestinationPlayer
import io.guthix.oldscape.server.pathing.type.DestinationTile
import io.guthix.oldscape.server.pathing.type.imp.breadthFirstSearch
import io.guthix.oldscape.server.pathing.type.imp.simplePathSearch
import io.guthix.oldscape.server.routine.NormalAction
import io.guthix.oldscape.server.world.mapsquare.zone.tile.tiles
import io.guthix.oldscape.server.world.mapsquare.zone.tile.abs
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import io.guthix.oldscape.server.world.mapsquare.zone.tile.TileUnit
import io.guthix.oldscape.server.world.WorldMap

fun conditionSimpleWalk(start: Tile, end: Tile, moverSize: TileUnit, map: WorldMap): List<Tile> {
    if(start.y == end.y || start.x == end.x || start.withInDistanceOf(end, 1.tiles)) {
        val simplePath = simplePathSearch(start, DestinationTile(end), moverSize, map)
        return if(simplePath.size == abs(start.x - end.x).value || simplePath.size == abs(start.y - end.y).value) {
            simplePath
        } else emptyList()
    }
    return emptyList()
}


on(PlayerOpEvent::class).where { player.contextMenu[event.option - 1] == "Follow" }.then {
    val followed = world.players[event.playerIndex] ?: throw IllegalStateException()
    val pDestination = DestinationPlayer(followed, world.map)
    player.path = if(conditionSimpleWalk(player.position, followed.position, player.size, world.map).isEmpty()) {
        val complexPath = breadthFirstSearch(player.position, pDestination, player.size, true, world.map)
        val totalPath = mutableListOf<Tile>()
        complexPath.forEachIndexed { i, tile ->
            if(conditionSimpleWalk(tile, followed.position, player.size, world.map).isNotEmpty()) {
                totalPath.addAll(complexPath.subList(0, i + 1))
                totalPath.addAll(simplePathSearch(tile, DestinationTile(followed.followPosition), player.size, world.map))
            }
        }
        totalPath
    } else { // no need to do bfs
        simplePathSearch(player.position, DestinationTile(followed.followPosition), player.size, world.map)
    }
    var currentTarget = player.followPosition
    player.turnToLock(followed)
    player.addRoutine(NormalAction) {
        while(true) {
            wait { currentTarget != followed.followPosition }
            println("${player.username} c: $currentTarget f: ${player.followPosition}")
            player.path = simplePathSearch(player.position, DestinationTile(followed.followPosition), player.size, world.map)
            currentTarget = followed.followPosition
        }
    }
}