package io.guthix.oldscape.cache.model

import kotlin.math.sqrt

class VertexNormal {
    var x: Int = 0
    var y: Int = 0
    var z: Int = 0
    var magnitude: Int = 0

    fun normalize(): Vec3f {
        var length = sqrt((x * x + y * y + z * z).toDouble())
        if (length == 0.toDouble()) {
            length = 1.toDouble()
        }
        return Vec3f((x / length).toFloat(), (y / length).toFloat(), (z / length).toFloat())
    }
}