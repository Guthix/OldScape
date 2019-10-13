/*
 * Copyright (C) 2019 Guthix
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.guthix.oldscape.cache.util

import java.awt.Color
import java.lang.IllegalStateException
import kotlin.math.max
import kotlin.math.min

fun Color.getHSLComponents(): IntArray {
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