package io.guthix.oldscape.server.event.imp

import io.guthix.oldscape.server.event.GameEvent

data class MouseClickEvent(
    val isLeftClick: Boolean,
    val presDuration: Int,
    val mouseX: Int,
    val mouseY: Int
) : GameEvent