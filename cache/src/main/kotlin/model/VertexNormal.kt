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
package io.guthix.oldscape.cache.model

import kotlin.math.sqrt

public class VertexNormal {
    public var x: Int = 0
    public var y: Int = 0
    public var z: Int = 0
    public var magnitude: Int = 0

    public fun normalize(): Vec3f {
        var length = sqrt((x * x + y * y + z * z).toDouble())
        if (length == 0.toDouble()) {
            length = 1.toDouble()
        }
        return Vec3f((x / length).toFloat(), (y / length).toFloat(), (z / length).toFloat())
    }
}