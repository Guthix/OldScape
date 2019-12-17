package io.guthix.oldscape.server.event.imp

import io.guthix.oldscape.server.event.GameEvent

data class WindowStatusEvent(val isResized: Boolean, val width: Int, val height: Int) : GameEvent