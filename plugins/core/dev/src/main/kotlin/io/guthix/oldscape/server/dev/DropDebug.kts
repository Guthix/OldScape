/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.dev

import io.guthix.oldscape.server.event.ClientCheatEvent
import io.guthix.oldscape.server.world.entity.Obj
import io.guthix.oldscape.server.api.blueprint.ObjectBlueprints
import io.guthix.oldscape.server.api.blueprint.LocationBlueprints
import io.guthix.oldscape.server.world.entity.Loc
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import io.guthix.oldscape.server.dimensions.tiles
import io.guthix.oldscape.server.world.entity.character.SpotAnimation

on(ClientCheatEvent::class).where { event.string == "drop" }.then {
    world.map.addObject(
        Tile(
            player.position.floor, player.position.x + 1.tiles, player.position.y+ 1.tiles
        ),
        Obj(ObjectBlueprints[1163], 1)
    )
}

on(ClientCheatEvent::class).where { event.string == "locadd" }.then {
    world.map.addDynamicLoc(
        Loc(
            Tile(
                player.position.floor, player.position.x + 2.tiles, player.position.y+ 2.tiles
            ),
            LocationBlueprints[4],
            type = 0,
            orientation = 0
        )
    )
}

on(ClientCheatEvent::class).where { event.string == "locremove" }.then {
    world.map.removeDynamicLoc(
        Loc(
            Tile(
                player.position.floor, player.position.x + 2.tiles, player.position.y+ 2.tiles
            ),
            LocationBlueprints[4],
            type = 0,
            orientation = 0
        )
    )
}

on(ClientCheatEvent::class).where { event.string == "inv" }.then {
    player.inventory.addNextSlot(Obj(ObjectBlueprints[1163], 1))
}

on(ClientCheatEvent::class).where { event.string == "shout" }.then {
    player.shoutMessage = "testing!"
}

on(ClientCheatEvent::class).where { event.string == "sequence" }.then {
    player.startSequence(1162)
    player.startSpotAnimation(SpotAnimation(id = 99, height = 92))
}

on(ClientCheatEvent::class).where { event.string == "follow" }.then {
    val player2 = world.players[2]
    player.turnToLock(player2)
}

on(ClientCheatEvent::class).where { event.string == "clear" }.then {
    player.clearMap()
}

on(ClientCheatEvent::class).where { event.string == "pos" }.then {
    println("Position ${player.position}")
}