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
package io.guthix.oldscape.cache.xtea

public data class MapXtea(val id: Int, val key: IntArray) {
    val x: Int get() = id shr 8

    val y: Int get() = id and 0xFF

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as MapXtea
        if (id != other.id) return false
        if (!key.contentEquals(other.key)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + key.contentHashCode()
        return result
    }
}