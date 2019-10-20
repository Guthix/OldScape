package io.guthix.oldscape.server.net.state.js5

import io.guthix.oldscape.server.net.IncPacket

enum class Js5Type(val opcode: Int) {
    NORMAL_CONTAINER_REQUEST(0),
    PRIORITY_CONTAINER_REQUEST(1),
    CLIENT_LOGGED_IN(2),
    CLIENT_LOGGED_OUT(3),
    ENCRYPTION_KEY_UPDATE(4);
}

class Js5FileRequest(val isPriority: Boolean, val indexFileId: Int, val containerId: Int) : IncPacket