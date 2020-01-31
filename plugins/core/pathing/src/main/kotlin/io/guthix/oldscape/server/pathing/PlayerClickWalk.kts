package io.guthix.oldscape.server.pathing

import io.guthix.oldscape.server.event.imp.MapClickEvent
import io.guthix.oldscape.server.net.state.game.outp.PlayerInfoPacket
import io.guthix.oldscape.server.routine.WeakAction
import io.guthix.oldscape.server.world.mapsquare.zone.tile.tiles
import io.guthix.oldscape.server.world.entity.character.Character

on(MapClickEvent::class).then {
    val destination = DestinationTile(player.position.floor, event.x, event.y)
    val path = breadthFirstSearch(player.position, destination, player.size, true, world)
    player.addRoutine(WeakAction) {
        while(true) {
            if(path.isEmpty()) {
                player.movementType = Character.MovementUpdateType.STAY
                break
            } else {
                player.lastPostion = player.position
                player.position = when {
                    player.isRunning -> when {
                        path.size == 1 -> {
                            player.movementType = Character.MovementUpdateType.WALK
                            player.updateFlags.add(PlayerInfoPacket.movementTemporary)
                            player.orientation = player.getOrientation(player.lastPostion, path[0])
                            path.removeAt(0)
                        }
                        player.position.withInDistanceOf(path[1], 1.tiles) -> { // running around corners
                            player.movementType = Character.MovementUpdateType.WALK
                            player.orientation = player.getOrientation(player.lastPostion, path[0])
                            path.removeAt(0)
                        }
                        else -> {
                            player.movementType = Character.MovementUpdateType.RUN
                            player.orientation = player.getOrientation(player.lastPostion, path[1])
                            path.removeAt(0)
                            path.removeAt(0)
                        }
                    }
                    else -> {
                        player.movementType = Character.MovementUpdateType.WALK
                        player.orientation = player.getOrientation(player.lastPostion, path[0])
                        path.removeAt(0)
                    }
                }
            }
            wait(1)
        }

    }
}