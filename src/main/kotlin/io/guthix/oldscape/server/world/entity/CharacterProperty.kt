package io.guthix.oldscape.server.world.entity

import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
class CharacterProperty<T : Any>(
    val initializer: Character.() -> T = { throw IllegalStateException("Not initialized.") }
) {
    operator fun getValue(thisRef: Character, property: KProperty<*>): T = thisRef.properties.getOrPut(property) {
        initializer(thisRef)
    } as T

    operator fun setValue(thisRef: Character, property: KProperty<*>, value: T): T {
        thisRef.properties[property] = value
        return value
    }
}