package io.guthix.oldscape.server.options

import io.guthix.oldscape.server.event.imp.CloseModalEvent

on(CloseModalEvent::class).then {
    player.topInterface.closeModal()
}