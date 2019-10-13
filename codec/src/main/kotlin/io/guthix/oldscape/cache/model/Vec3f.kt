package io.guthix.oldscape.cache.model

data class Vec3f(var x: Float, var y: Float, var z: Float) {
    fun rotate180yz(): Vec3f {
        y *= -1f
        z *= -1f
        return this
    }
}