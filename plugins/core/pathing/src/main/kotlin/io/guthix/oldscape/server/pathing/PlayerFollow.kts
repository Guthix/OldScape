package io.guthix.oldscape.server.pathing

import io.guthix.oldscape.server.event.imp.PlayerClickEvent
import io.guthix.oldscape.server.pathing.type.DestinationTile
import io.guthix.oldscape.server.pathing.type.imp.breadthFirstSearch
import io.guthix.oldscape.server.pathing.type.imp.simplePathSearch
import io.guthix.oldscape.server.routine.NormalAction


on(PlayerClickEvent::class).where { player.contextMenu[event.option - 1] == "Follow" }.then {
    val followed = world.players[event.playerIndex] ?: throw IllegalStateException()
    var dest = DestinationTile(followed.followPosition)
    player.path = breadthFirstSearch(player.position, dest, player.size, true, world.map)
    player.turnToLock(followed)
    player.addRoutine(NormalAction) {
        var currentTarget = player.followPosition
        while(true) {
            if(dest.reached(player.position.x, player.position.y, player.size)) break
            if(currentTarget != player.followPosition) {
                player.path = breadthFirstSearch(player.position, dest, player.size, true, world.map)
            }
            wait(1)
        }
        while(true) {
            wait { currentTarget != player.followPosition }
            dest = DestinationTile(followed.followPosition)
            player.path = simplePathSearch(player.position, dest, player.size, world.map)
            currentTarget = followed.followPosition
        }
    }.onCancel {
        player.turnToLock(null)
    }
}