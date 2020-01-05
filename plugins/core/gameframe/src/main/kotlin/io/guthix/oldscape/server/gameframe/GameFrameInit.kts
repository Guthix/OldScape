package io.guthix.oldscape.server.gameframe

import io.guthix.oldscape.server.event.imp.ButtonClickEvent
import io.guthix.oldscape.server.event.imp.WindowStatusEvent

on(WindowStatusEvent::class).then {
    player.clientSettings.width = event.width
    player.clientSettings.height = event.height
    if(player.clientSettings.resizable != event.isResized) {
        player.clientSettings.resizable = event.isResized
        if(player.clientSettings.resizable) {
            player.changeGameFrame(GameFrame.RESIZABLE_BOX)
        } else {
            player.changeGameFrame(GameFrame.FIXED)
        }
    }
}

on(ButtonClickEvent::class).where { event.interfaceId == 378 && event.buttonId == 81 }.then {
    player.closeSubInterface(165, 28)
    if(player.clientSettings.resizable) {
        player.changeGameFrame(GameFrame.RESIZABLE_BOX)
    } else {
        player.changeGameFrame(GameFrame.FIXED)
    }
}