package io.guthix.oldscape.server.world

data class ClientSettings(
        val resizable: Boolean,
        val lowMemory: Boolean,
        val width: Int,
        val height: Int
)