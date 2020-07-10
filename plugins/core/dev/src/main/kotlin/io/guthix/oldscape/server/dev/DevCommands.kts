/**
 * This file is part of Guthix OldScape-Server.
 *
 * Guthix OldScape-Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape-Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.dev

import io.guthix.oldscape.server.blueprints.SpotAnimation
import io.guthix.oldscape.server.dimensions.floors
import io.guthix.oldscape.server.dimensions.tiles
import io.guthix.oldscape.server.event.ClientCheatEvent
import io.guthix.oldscape.server.event.PublicMessageEvent
import io.guthix.oldscape.server.world.entity.*
import io.guthix.oldscape.server.world.map.Tile

on(ClientCheatEvent::class).where { string == "drop" }.then {
    world.map.addObject(
        Tile(player.pos.floor, player.pos.x + 1.tiles, player.pos.y + 1.tiles),
        Obj(1163, 1)
    )
}

on(ClientCheatEvent::class).where { string == "locadd" }.then {
    world.map.addDynamicLoc(
        Loc(
            id = 4,
            type = 0,
            pos = Tile(player.pos.floor, player.pos.x + 2.tiles, player.pos.y + 2.tiles),
            orientation = 0
        )
    )
}

on(ClientCheatEvent::class).where { string == "locremove" }.then {
    world.map.removeDynamicLoc(
        Loc(
            id = 4,
            type = 0,
            pos = Tile(player.pos.floor, player.pos.x + 2.tiles, player.pos.y + 2.tiles),
            orientation = 0
        )
    )
}

on(ClientCheatEvent::class).where { string == "shoot" }.then {
    player.animate(Sequence(id = 424))
}

on(ClientCheatEvent::class).where { string == "rangeeq" }.then {
    val bow = TwoHandEquipment(861, 1)
    val arrows = AmmunitionEquipment(882, 20)
    player.topInterface.inventory.add(bow)
    player.topInterface.inventory.add(arrows)
}

on(ClientCheatEvent::class).where { string == "invent" }.then {
    val bow = TwoHandEquipment(1319, 1)
    player.topInterface.inventory.add(bow)
}


on(ClientCheatEvent::class).where { string == "shout" }.then {
    player.shout("testing!")
}

on(ClientCheatEvent::class).where { string == "animation" }.then {
    player.animate(Sequence(id = 1162))
    player.spotAnimate(SpotAnimation(id = 99, height = 92))
}

on(ClientCheatEvent::class).where { string == "clear" }.then {
    player.clearMap()
}

on(ClientCheatEvent::class).where { string.startsWith("tp") }.then {
    val values = string.split(" ")
    player.teleport(Tile(values[1].toInt().floors, values[2].toInt().tiles, values[3].toInt().tiles))
}

on(ClientCheatEvent::class).where { string == "pos" }.then {
    player.talk(PublicMessageEvent(0, 0, "Position ${player.pos}", player, world))
}

on(ClientCheatEvent::class).where { string == "npc" }.then {
    world.addNpc(42, player.pos.copy(x = player.pos.x + 2.tiles))
}