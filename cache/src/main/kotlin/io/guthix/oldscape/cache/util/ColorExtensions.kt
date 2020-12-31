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
package io.guthix.oldscape.cache.util

import java.awt.Color
import kotlin.math.max
import kotlin.math.min

internal fun Color.getHSLComponents(): IntArray {
    val r = red / 256.0
    val g = green / 256.0
    val b = blue / 256.0
    val min = min(min(g, r), b)
    val max = max(max(g, r), b)
    val luminace= (max + min) / 2.0
    var saturation = (if(min != max) {
        if(luminace < 0.5) {
            (max - min) / (max + min)
        } else {
            (max - min) / (2.0 - max - min)
        }
    } else {
        0.toDouble()
    } * 256.0).toInt()
    val hue = (if(min != max) {
        when (max) {
            r -> (g - b) / (max - min)
            g -> (b - r) / (max - min) + 2.0
            b -> (r - g) / (max - min) + 4.0
            else -> throw IllegalStateException("Max color value should be either red, green or blue.")
        }
    } else {
        0.toDouble()
    } / 6.0 * 256.0).toInt()
    var lightness = (luminace * 256.0).toInt()
    if (saturation < 0) {
        saturation = 0
    } else if (saturation > 255) {
        saturation = 255
    }
    if (lightness < 0) {
        lightness = 0
    } else if (lightness > 255) {
        lightness = 255
    }
    return intArrayOf(hue, saturation, lightness)
}