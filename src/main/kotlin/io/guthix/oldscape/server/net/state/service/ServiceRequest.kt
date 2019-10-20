package io.guthix.oldscape.server.net.state.service

import io.guthix.oldscape.server.net.IncPacket

enum class ServiceType(val opcode: Int) {
    GAME(14), JS5(15);
}

class GameConnectionRequest : IncPacket

class Js5ConnectionRequest(val revision: Int) : IncPacket