package io.guthix.oldscape.server.gameframe

import io.guthix.oldscape.server.event.imp.ButtonClickEvent
import io.guthix.oldscape.server.net.state.game.outp.PlayerInfoPacket

on(ButtonClickEvent::class).where { event.interfaceId == 160 && event.buttonId == 22 }.then {
    player.inRunMode = !player.inRunMode
    player.updateFlags.add(PlayerInfoPacket.movementCached)
}