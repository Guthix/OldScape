/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.world.entity

import io.guthix.oldscape.server.util.WeakIdentityHashMap
import kotlin.reflect.KProperty

class EntityAttribute<R : Entity, T : Any>(
    private val initializer: R.() -> T = { error("Not initialized.") }
) {
    private val map = WeakIdentityHashMap<R, T>()

    operator fun getValue(thisRef: R, property: KProperty<*>): T =
        map[thisRef] ?: setValue(thisRef, property, initializer(thisRef))

    operator fun setValue(thisRef: R, property: KProperty<*>, value: T): T {
        map[thisRef] = value
        return value
    }
}

class NullableEntityAttribute<R : Entity, T>(val initializer: R.() -> T? = { null }) {
    private val map = WeakIdentityHashMap<R, T?>()

    operator fun getValue(thisRef: R, property: KProperty<*>): T? =
        if (thisRef in map) map[thisRef] else setValue(thisRef, property, initializer(thisRef))

    operator fun setValue(thisRef: R, property: KProperty<*>, value: T?): T? {
        map[thisRef] = value
        return value
    }
}
