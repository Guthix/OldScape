package io.guthix.oldscape.server.interest

import io.guthix.oldscape.server.event.imp.LoginEvent

on(LoginEvent::class).then {
    println("Player interest routine")
}