package io.guthix.oldscape.server.world.entity

import kotlin.reflect.KProperty

abstract class Entity(open val attributes: MutableMap<KProperty<*>, Any?>)