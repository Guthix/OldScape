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
package io.guthix.oldscape.server

import kotlin.reflect.KProperty

interface PropertyHolder {
    val properties: MutableMap<KProperty<*>, Any?>
}

class Property<in C : PropertyHolder, T : Any?>(
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

