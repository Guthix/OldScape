package io.guthix.oldscape.server.event.imp

import io.guthix.oldscape.server.event.GameEvent

class ButtonClickEvent(
    val interfaceId: Int,
    val buttonId: Int,
    val componentId: Int,
    val slotId: Int,
    val option: Int
) : GameEvent