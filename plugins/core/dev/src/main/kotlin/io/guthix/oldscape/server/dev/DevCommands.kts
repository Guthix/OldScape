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

import io.guthix.oldscape.server.blueprints.SpotAnimBlueprint
import io.guthix.oldscape.server.world.map.dim.floors
import io.guthix.oldscape.server.world.map.dim.tiles
import io.guthix.oldscape.server.event.ClientCheatEvent
import io.guthix.oldscape.server.event.PublicMessageEvent
import io.guthix.oldscape.server.loc.DOOR_4
import io.guthix.oldscape.server.obj.BRONZEARROW_882
import io.guthix.oldscape.server.obj.MAGICSHORTBOW_861
import io.guthix.oldscape.server.obj.RUNE2HSWORD_1319
import io.guthix.oldscape.server.obj.RUNEFULLHELM_1163
import io.guthix.oldscape.server.world.entity.Loc
import io.guthix.oldscape.server.world.entity.Obj
import io.guthix.oldscape.server.world.entity.create
import io.guthix.oldscape.server.world.map.Tile

on(ClientCheatEvent::class).where { string == "drop" }.then {
    world.map.addObject(
        Tile(player.pos.floor, player.pos.x + 1.tiles, player.pos.y + 1.tiles),
        Obj.RUNEFULLHELM_1163.create(amount = 1)
    )
}

on(ClientCheatEvent::class).where { string == "locadd" }.then {
    world.map.addDynamicLoc(
        Loc.DOOR_4.create(0, Tile(player.pos.floor, player.pos.x + 2.tiles, player.pos.y + 2.tiles), 0)
    )
}

on(ClientCheatEvent::class).where { string == "locremove" }.then {
    world.map.removeDynamicLoc(
        Loc.DOOR_4.create(0, Tile(player.pos.floor, player.pos.x + 2.tiles, player.pos.y + 2.tiles), 0)
    )
}

on(ClientCheatEvent::class).where { string == "shoot" }.then {
    player.animate(424)
}

on(ClientCheatEvent::class).where { string == "rangeq" }.then {
    player.topInterface.inventory.add(Obj.MAGICSHORTBOW_861.create(1))
    player.topInterface.inventory.add(Obj.BRONZEARROW_882.create(5))
}

on(ClientCheatEvent::class).where { string == "invent" }.then {
    val bow = Obj.RUNE2HSWORD_1319.create(1)
    player.topInterface.inventory.add(bow)
}


on(ClientCheatEvent::class).where { string == "shout" }.then {
    player.shout("testing!")
}

on(ClientCheatEvent::class).where { string == "animation" }.then {
    player.animate(1162)
    player.spotAnimate(SpotAnimBlueprint(id = 99, height = 92))
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