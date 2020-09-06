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

import io.guthix.oldscape.server.content.*
import io.guthix.oldscape.server.world.map.dim.tiles
import io.guthix.oldscape.server.event.ClientCheatEvent
import io.guthix.oldscape.server.event.PublicMessageEvent
import io.guthix.oldscape.server.template.*
import io.guthix.oldscape.server.world.map.Tile
import io.guthix.oldscape.server.world.map.dim.floors

on(ClientCheatEvent::class).where { string == "drop" }.then {
    world.map.addObject(
        ObjTemplates.RUNEFULLHELM_1163,
        amount = 1,
        Tile(player.pos.floor, player.pos.x + 1.tiles, player.pos.y + 1.tiles)
    )
}

on(ClientCheatEvent::class).where { string == "locadd" }.then {
    world.map.addDynamicLoc(
        LocTemplates.DOOR_4,
        type = 0,
        orientation = 0,
        Tile(player.pos.floor, player.pos.x + 2.tiles, player.pos.y + 2.tiles)
    )
}

on(ClientCheatEvent::class).where { string == "shoot" }.then {
    player.animate(SequenceTemplates[424])
}

on(ClientCheatEvent::class).where { string == "rangeq" }.then {
    player.topInterface.itemBag.add(ObjTemplates.MAGICSHORTBOW_861, amount = 1)
    player.topInterface.itemBag.add(ObjTemplates.BRONZEARROW_882, amount = 5)
}

on(ClientCheatEvent::class).where { string == "shout" }.then {
    player.shout("testing!")
}

on(ClientCheatEvent::class).where { string == "animation" }.then {
    player.animate(SequenceTemplates[1162])
    player.spotAnimate(SpotAnimTemplates[99], height = 92)
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
    world.addNpc(NpcTemplates[42], player.pos.copy(x = player.pos.x + 2.tiles))
}