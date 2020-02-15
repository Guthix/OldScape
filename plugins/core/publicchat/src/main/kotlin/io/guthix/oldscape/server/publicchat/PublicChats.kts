package io.guthix.oldscape.server.publicchat

import io.guthix.oldscape.server.event.imp.PublicMessageEvent

on(PublicMessageEvent::class).then{
    player.publicMessage = event
}