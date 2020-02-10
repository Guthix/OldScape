package io.guthix.oldscape.server.inventory

import io.guthix.oldscape.server.event.imp.LoginEvent
import io.guthix.oldscape.server.world.entity.character.player.Inventory
import io.guthix.oldscape.server.routine.PostRoutine

on(LoginEvent::class).then {
    player.inventory = Inventory(player, 149, 0, 3, arrayOfNulls(28))
    player.addRoutine(PostRoutine) {
        while(true) {
            player.inventory.update()
            wait(1)
        }
    }
}