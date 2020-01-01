package io.guthix.oldscape.server.gameframe

import io.guthix.oldscape.server.event.imp.ButtonClickEvent

on(ButtonClickEvent::class).where { event.interfaceId == 378 && event.buttonId == 81 }.then {
    player.gameframe.switchTo(player, GameFrame.FIXED)
}