package io.guthix.oldscape.server.combat

import io.guthix.oldscape.server.event.NpcClickEvent
import io.guthix.oldscape.server.event.script.Routine
import io.guthix.oldscape.server.pathing.algo.DesinationNpc
import io.guthix.oldscape.server.pathing.algo.imp.breadthFirstSearch

on(NpcClickEvent::class).where { event.option == "Attack" }.then(Routine.Type.Normal, replace = true) {
    val destination = DesinationNpc(event.npc, world.map)
    player.turnToLock(event.npc)
    player.path = breadthFirstSearch(player.pos, destination, player.size, true, world.map)
}