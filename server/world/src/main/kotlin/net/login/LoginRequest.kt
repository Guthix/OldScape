/*
 * Copyright 2018-2021 Guthix
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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