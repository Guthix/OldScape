package io.guthix.oldscape.server.logout

import io.guthix.oldscape.server.event.ButtonClickEvent

on(ButtonClickEvent::class).where { event.interfaceId == 182 && event.buttonId == 8 }.then {
    world.stagePlayerLogout(player )
}