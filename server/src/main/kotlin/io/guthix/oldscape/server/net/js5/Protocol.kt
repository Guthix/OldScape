/*
 * Copyright 2018-2020 Guthix
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