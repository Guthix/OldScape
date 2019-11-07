/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.cache.export

import io.guthix.oldscape.cache.TextureArchive
import io.guthix.oldscape.cache.model.Model
import java.awt.Color
import java.io.PrintWriter

fun exportObj(model: Model, textureArchive: TextureArchive, objWriter: PrintWriter, mtlWriter: PrintWriter) {
    model.computeNormals()
    model.computeTextureUVCoordinates()

    objWriter.println("mtllib " + model.id + ".mtl")
    objWriter.println("o rsmodel")
    for (i in 0 until model.vertexCount) {
        objWriter.println("v " + model.vertexPositionsX!![i]
                    + " " + model.vertexPositionsY!![i] * -1
                    + " " + model.vertexPositionsZ!![i] * -1
        )
    }
    if (model.triangleTextures != null) {
        val u = model.triangleTextUCo!!
        val v = model.triangleTextVCo!!

        for (i in 0 until model.triangleCount) {
            objWriter.println("vt " + u[i]!![0] + " " + v[i]!![0])
            objWriter.println("vt " + u[i]!![1] + " " + v[i]!![1])
            objWriter.println("vt " + u[i]!![2] + " " + v[i]!![2])
        }
    }
    for (normal in model.vertexNormals!!) {
        objWriter.println("vn " + normal.x + " " + normal.y * -1 + " " + normal.z * -1)
    }

    for (i in 0 until model.triangleCount) {
        val x = model.triangleVertex1!![i] + 1
        val y = model.triangleVertex2!![i] + 1
        val z = model.triangleVertex3!![i] + 1

        objWriter.println("usemtl m$i")
        if(model.triangleTextures != null) {
            objWriter.println(("f " + x + "/" + (i * 3 + 1) + " " + y + "/" + (i * 3 + 2) + " " + z + "/" + (i * 3 + 3)))
        } else {
            objWriter.println("f $x $y $z")
        }
        objWriter.println("")
    }

    for (i in 0 until model.triangleCount) {
        val textureId = if(model.triangleTextures == null) -1 else model.triangleTextures!![i]
        mtlWriter.println("newmtl m$i")
        if (textureId == -1) {
            val color = rs2hsbToColor(model.triangleColors!![i])
            val r = color.red / 255.0
            val g = color.green / 255.0
            val b = color.blue / 255.0
            mtlWriter.println("Kd $r $g $b")
        } else {
            val texture = textureArchive.textures.first { it.id == textureId }
            mtlWriter.println("map_Kd " + texture.id + ".png")
        }

        var alpha = 0

        if (model.triangleAlphas != null) {
            alpha = model.triangleAlphas!![i].toInt() and 0xFF
        }

        if (alpha != 0) {
            mtlWriter.println("d " + alpha / 255.0)
        }
    }
}

fun rs2hsbToColor(hsb: Int): Color {
    val decode_hue = hsb shr 10 and 0x3f
    val decode_saturation = hsb shr 7 and 0x07
    val decode_brightness = hsb and 0x7f
    return Color.getHSBColor(
        decode_hue.toFloat() / 63,
        decode_saturation.toFloat() / 7,
        decode_brightness.toFloat() / 127
    )
}