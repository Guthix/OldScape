package io.guthix.oldscape.server.net.state.login

import io.guthix.oldscape.server.net.IncPacket
import io.guthix.oldscape.server.net.state.IsaacRandomPair
import io.guthix.oldscape.server.world.entity.player.ClientSettings
import io.guthix.oldscape.server.world.entity.player.MachineSettings

data class LoginRequest(
        val loginType: LoginType,
        val clientRevision: Int,
        val authType: Int,
        val sessionId: Long,
        val uniqueId: ByteArray,
        val username: String,
        val password: String,
        val clientInfo: ClientSettings,
        val machineInfo: MachineSettings,
        val crcs: IntArray,
        val isaacPair: IsaacRandomPair
) : IncPacket {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as LoginRequest
        if (loginType != other.loginType) return false
        if (clientRevision != other.clientRevision) return false
        if (authType != other.authType) return false
        if (sessionId != other.sessionId) return false
        if (!uniqueId.contentEquals(other.uniqueId)) return false
        if (username != other.username) return false
        if (password != other.password) return false
        if (clientInfo != other.clientInfo) return false
        if (machineInfo != other.machineInfo) return false
        if (!crcs.contentEquals(other.crcs)) return false
        if (isaacPair != other.isaacPair) return false
        return true
    }

    override fun hashCode(): Int {
        var result = loginType.hashCode()
        result = 31 * result + clientRevision
        result = 31 * result + authType
        result = 31 * result + sessionId.hashCode()
        result = 31 * result + uniqueId.contentHashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + clientInfo.hashCode()
        result = 31 * result + machineInfo.hashCode()
        result = 31 * result + crcs.contentHashCode()
        result = 31 * result + isaacPair.hashCode()
        return result
    }
}