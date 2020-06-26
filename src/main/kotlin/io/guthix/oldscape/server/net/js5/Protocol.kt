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
package io.guthix.oldscape.server.net.js5

import io.netty.buffer.ByteBuf
import io.netty.buffer.DefaultByteBufHolder

enum class Js5Type(val opcode: Int) {
    NORMAL_CONTAINER_REQUEST(0),
    URGENT_CONTAINER_REQUEST(1),
    CLIENT_LOGGED_IN(2),
    CLIENT_LOGGED_OUT(3),
    ENCRYPTION_KEY_UPDATE(4);
}

data class Js5ContainerRequest(val isUrgent: Boolean, val indexFileId: Int, val containerId: Int)

class Js5ContainerResponse(
    val indexFileId: Int,
    val containerId: Int,
    val compressionType: Int,
    val compressedSize: Int,
    val data: ByteBuf
) : DefaultByteBufHolder(data)