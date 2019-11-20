package io.guthix.oldscape.server.world.entity

import kotlin.reflect.KProperty

class EntityAttribute<T : Any?> {
    @Suppress("UNCHECKED_CAST")
    operator fun getValue(thisRef: Entity, property: KProperty<*>): T {
        return thisRef.attributes[property] as T
    }

    operator fun setValue(thisRef: Entity, property: KProperty<*>, value: T) {
        thisRef.attributes[property] = value
    }
}