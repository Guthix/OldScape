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
package io.guthix.oldscape.server.world.entity

import kotlin.reflect.KProperty

class CharacterProperty<in C : Character, T : Any?>(
    val initializer: C.() -> T = { throw IllegalStateException("Not initialized.") }
) {
    @Suppress("UNCHECKED_CAST")
    operator fun getValue(thisRef: C, property: KProperty<*>): T = thisRef.properties.getOrPut(property) {
        initializer(thisRef)
    } as T

    operator fun setValue(thisRef: C, property: KProperty<*>, value: T): T {
        thisRef.properties[property] = value
        return value
    }
}