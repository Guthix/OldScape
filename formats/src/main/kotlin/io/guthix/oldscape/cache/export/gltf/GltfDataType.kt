/**
 * This file is part of Guthix OldScape-Cache.
 *
 * Guthix OldScape-Cache is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape-Cache is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.cache.export.gltf

enum class GltfDataType(val opcode: Int, val size: Int) {
    BYTE(5120, 1),
    UNSIGNED_BYTE(5121, BYTE.size),
    SHORT(5122, 2),
    UNSIGNED_SHORT(5123, SHORT.size),
    UNSIGNED_INT(5125, 4),
    FLOAT(5126, 4)
}