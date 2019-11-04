package io.guthix.oldscape.server.net.state.login

enum class LoginType(val opcode: Int) {
    UNKOWN_LOGIN(14),
    UNKOWN_LOGIN2(15),
    NEW_LOGIN_CONNECTION(16),
    RECONNECT_LOGIN_CONNECTION(18);

    companion object {
        fun find(opcode: Int) = LoginType.values().first{ it.opcode == opcode}
    }
}