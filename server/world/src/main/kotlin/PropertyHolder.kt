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
package io.guthix.oldscape.server

import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField

interface PropertyHolder {
    val properties: MutableMap<KProperty<*>, Any?>
}

interface PersistentPropertyHolder {
    val persistentProperties: MutableMap<String, Any>
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

internal val KProperty<*>.persistentName get() = "${javaField?.declaringClass?.name}.${name}"

class PersistentProperty<in C : PersistentPropertyHolder, T : Any>(
    val initializer: C.() -> T = { throw IllegalStateException("Not initialized.") }
) {
    @Suppress("UNCHECKED_CAST")
    operator fun getValue(thisRef: C, property: KProperty<*>): T {
        return thisRef.persistentProperties.getOrPut(property.persistentName) {
            initializer(thisRef)
        } as T
    }

    operator fun setValue(thisRef: C, property: KProperty<*>, value: T): T {
        val key = "${property.javaField?.declaringClass?.name}.${property.name}"
        thisRef.persistentProperties[key] = value
        return value
    }
}