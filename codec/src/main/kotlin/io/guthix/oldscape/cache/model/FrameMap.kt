/*
 * Copyright (C) 2019 Guthix
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.guthix.oldscape.cache.model

import io.netty.buffer.ByteBuf

class FrameMap(val types: ShortArray, val frameMaps: Array<ShortArray>) {
    companion object {
        fun decode(data: ByteBuf): FrameMap {
            val length = data.readUnsignedByte().toInt()
            val types = ShortArray(length) {
                data.readUnsignedByte()
            }
            val frameMaps = Array(length) {
                ShortArray(data.readUnsignedByte().toInt())
            }
            frameMaps[0].map { data.readUnsignedByte() }

            frameMaps.forEach { frameMap ->
                for(i in frameMap.indices) {
                    frameMap[i] = data.readUnsignedByte()
                }
            }
            return FrameMap(types, frameMaps)
        }
    }
}