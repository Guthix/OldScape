/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.pathing

import io.guthix.oldscape.server.net.state.game.outp.PlayerInfoPacket
import io.guthix.oldscape.server.routine.NormalAction
import io.guthix.oldscape.server.world.entity.character.Character
import io.guthix.oldscape.server.world.entity.character.player.Player
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import io.guthix.oldscape.server.world.mapsquare.zone.tile.tiles

fun Player.startWalkingOn(path: MutableList<Tile>, postAction: Player.() -> Unit= { }) {
    addRoutine(NormalAction) {
        while(true) {
            if(path.isEmpty()) {
                player.movementType = Character.MovementUpdateType.STAY
                break
            } else {
                player.step(path)
            }
            wait(1)
        }
        player.postAction()
    }
}

fun Player.step(path: MutableList<Tile>) {
    lastPostion = position
    position = when {
        inRunMode -> when {
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