/*
 * Copyright 2018-2020 Guthix
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.guthix.oldscape.server.dev

import io.guthix.oldscape.dim.floors
import io.guthix.oldscape.dim.tiles
import io.guthix.oldscape.server.damage.hit
import io.guthix.oldscape.server.event.ClientCheatEvent
import io.guthix.oldscape.server.event.PublicMessageEvent
import io.guthix.oldscape.server.template.LocIds
import io.guthix.oldscape.server.template.ObjIds
import io.guthix.oldscape.server.world.entity.Loc
import io.guthix.oldscape.server.world.map.Tile

on(ClientCheatEvent::class).where { string == "drop" }.then {
    world.addObject(
        ObjIds.RUNE_FULL_HELM_1163,
        amount = 1,
        Tile(player.pos.floor, player.pos.x + 1.tiles, player.pos.y + 1.tiles)
    )
}

on(ClientCheatEvent::class).where { string == "locadd" }.then {
    world.addLoc(
        Loc(
            LocIds.TREE_STUMP_1342,
            type = 10,
            Tile(player.pos.floor, player.pos.x + 2.tiles, player.pos.y + 2.tiles),
            orientation = 0
        )
    )
}

on(ClientCheatEvent::class).where { string == "hit" }.then {
    player.hit(world, 99)
}

on(ClientCheatEvent::class).where { string == "locdel" }.then {
    world.delLoc(
        Loc(
            LocIds.TREE_STUMP_1342,
            type = 10,
            Tile(player.pos.floor, player.pos.x + 2.tiles, player.pos.y + 2.tiles),
            orientation = 0
        )
    )
}

on(ClientCheatEvent::class).where { string == "rangeq" }.then {
    player.itemBag.add(ObjIds.MAGIC_SHORTBOW_861, amount = 1)
    player.itemBag.add(ObjIds.BRONZE_ARROW_882, amount = 5)
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

on(ClientCheatEvent::class).where { string.startsWith("npc") }.then {
    val args = string.split(" ")
    val first = args[1].toInt()
    world.createNpc(first, player.pos.copy(x = player.pos.x + 2.tiles))
}