/**
 * This file is part of Guthix OldScape-Server.
 *
 * Guthix OldScape-Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape-Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.net.login

import io.guthix.oldscape.server.net.IsaacRandomPair
import io.guthix.oldscape.server.world.entity.ClientSettings
import io.guthix.oldscape.server.world.entity.MachineSettings
import io.netty.channel.ChannelHandlerContext

data class LoginRequest(
    val loginType: LoginType,
    val clientRevision: Int,
    val authType: Int,
    val sessionId: Long,
    val uniqueId: ByteArray,
    val username: String,
    val password: String,
    val clientSettings: ClientSettings,
    val machineInfo: MachineSettings,
    val crcs: IntArray,
    val isaacPair: IsaacRandomPair,
    var ctx: ChannelHandlerContext
) {
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
        if (clientSettings != other.clientSettings) return false
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
        result = 31 * result + clientSettings.hashCode()
        result = 31 * result + machineInfo.hashCode()
        result = 31 * result + crcs.contentHashCode()
        result = 31 * result + isaacPair.hashCode()
        return result
    }
}