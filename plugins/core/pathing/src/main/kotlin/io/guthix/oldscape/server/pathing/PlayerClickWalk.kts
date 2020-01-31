package io.guthix.oldscape.server.pathing

import io.guthix.oldscape.server.event.imp.MapClickEvent
import io.guthix.oldscape.server.event.imp.MiniMapClickEvent
import io.guthix.oldscape.server.net.state.game.outp.PlayerInfoPacket
import io.guthix.oldscape.server.routine.WeakAction
import io.guthix.oldscape.server.world.mapsquare.zone.tile.tiles
import io.guthix.oldscape.server.world.entity.character.player.Player
import io.guthix.oldscape.server.world.mapsquare.zone.tile.TileUnit
import io.guthix.oldscape.server.world.mapsquare.FloorUnit
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.character.Character

on(MiniMapClickEvent::class).then {
    player.startWalkingTo(player.position.floor, event.x, event.y, world)
}

on(MapClickEvent::class).then {
    player.startWalkingTo(player.position.floor, event.x, event.y, world)
}

fun Player.startWalkingTo(floor: FloorUnit, x: TileUnit, y: TileUnit, world: World) {
    val destination = DestinationTile(floor, x, y)
    val path = breadthFirstSearch(position, destination, size, true, world)
    addRoutine(WeakAction) {
        while(true) {
            if(path.isEmpty()) {
                player.movementType = Character.MovementUpdateType.STAY
                break
            } else {
                player.lastPostion = player.position
                player.position = when {
                    player.inRunMode -> when {
                        path.size == 1 -> {
                            movementType = Character.MovementUpdateType.WALK
                            updateFlags.add(PlayerInfoPacket.movementTemporary)
                            orientation = getOrientation(lastPostion, path[0])
                            path.removeAt(0)
                        }
                        position.withInDistanceOf(path[1], 1.tiles) -> { // running around corners
                            movementType = Character.MovementUpdateType.WALK
                            orientation = getOrientation(lastPostion, path[0])
                            path.removeAt(0)
                        }
                        else -> {
                            movementType = Character.MovementUpdateType.RUN
                            orientation = getOrientation(lastPostion, path[1])
                            path.removeAt(0)
                            path.removeAt(0)
                        }
                    }
                    else -> {
                        movementType = Character.MovementUpdateType.WALK
                        orientation = getOrientation(lastPostion, path[0])
                        path.removeAt(0)
                    }
                }
            }
            wait(1)
        }
    }
}