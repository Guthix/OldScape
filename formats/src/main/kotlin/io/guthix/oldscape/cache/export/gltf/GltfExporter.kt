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
package io.guthix.oldscape.cache.export.gltf

import de.javagl.jgltf.impl.v2.*
import de.javagl.jgltf.model.io.GltfWriter
import io.guthix.oldscape.cache.export.rs2hsbToColor
import io.guthix.oldscape.cache.model.Model
import java.awt.Color
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.max
import kotlin.math.min

const val floatInVertex = 3

const val vertexInTriangle = 3

const val floatSize = 4

const val triangleSize = vertexInTriangle * floatSize * floatInVertex

const val indexHeaderSize = 4

@ExperimentalUnsignedTypes
fun gltfExport(id: Int, model: Model) {
    model.computeNormals()
    model.computeTextureUVCoordinates()
    val gltf = GlTF()
    gltf.asset = jagexAsset
    val textureCount = if(model.triangleTextures == null) 0 else model.triangleTextures!!.count { it != -1 }
    val buffer = ByteBuffer.allocate(
        indexHeaderSize + (model.triangleCount * vertexInTriangle * floatInVertex * floatSize) * 2 + 2 * vertexInTriangle * floatSize * textureCount
    )
    buffer.order(ByteOrder.LITTLE_ENDIAN)
    buffer.put(0)
    buffer.put(1)
    buffer.put(2)
    buffer.put(0) // fill
    for(triangleId in 0 until model.triangleCount) {
        val vertices = listOf(
            model.triangleVertex1!![triangleId],
            model.triangleVertex2!![triangleId],
            model.triangleVertex3!![triangleId]
        )
        vertices.forEach {
            val normalVector = model.vertexNormals!![it].normalize()
            buffer.putFloat(normalVector.x)
            buffer.putFloat(normalVector.y * -1)
            buffer.putFloat(normalVector.z * -1)
        }
        vertices.forEach {
            buffer.putFloat(model.vertexPositionsX!![it].toFloat())
            buffer.putFloat((model.vertexPositionsY!![it] * -1).toFloat())
            buffer.putFloat((model.vertexPositionsZ!![it] * -1).toFloat())
        }
        val textureId = if(model.triangleTextures == null) -1 else model.triangleTextures!![triangleId].toInt()
        if(textureId != -1) { // write UV coordinates
            repeat(vertexInTriangle) {
                buffer.putFloat(model.triangleTextUCo!![triangleId]!![it])
                buffer.putFloat(model.triangleTextVCo!![triangleId]!![it])
            }
        }
    }
    gltf.addBuffers(createBuffer(id.toString(), buffer))
    createTriangleBufferView(gltf, model)
    val textureMap = createTextures(gltf, model)
    val materialMap = createMaterials(gltf, model, textureMap)
    createVertexAccessor(gltf, model)
    gltf.addNodes(singleMeshNode)
    gltf.addScenes(singleNodeScene)
    gltf.addMeshes(createMesh(model, materialMap))
    GltfWriter().write(gltf,  FileOutputStream(File("./resources/gltf/$id.gltf")))
    FileOutputStream(File("./resources/gltf/$id.bin")).write(buffer.array())
}

@ExperimentalUnsignedTypes
private fun createBuffer(bufferName: String, byteBuffer: ByteBuffer) = Buffer().apply {
    uri = "$bufferName.bin"
    byteLength = byteBuffer.limit()
}

@ExperimentalUnsignedTypes
private fun createTriangleBufferView(gltf: GlTF, model: Model) {
    var totalTextureSize = 0
    for(triangleId in 0 until model.triangleCount) {
        val vertxBufferView = BufferView().apply {
            buffer = 0
            byteOffset = indexHeaderSize + triangleId * 2 * triangleSize + totalTextureSize
            byteLength = 2 * triangleSize
            byteStride = 12
            target = GltfBufferType.ARRAY_BUFFER.opcode
        }
        gltf.addBufferViews(vertxBufferView)
        val textureId = if(model.triangleTextures == null) -1 else model.triangleTextures!![triangleId].toInt()
        if(textureId != -1) {
            val uvSize = 2 * vertexInTriangle * floatSize
            val uvBufferView = BufferView().apply {
                buffer = 0
                byteOffset = indexHeaderSize + triangleId * 2 * triangleSize + totalTextureSize + 2 * triangleSize
                byteLength = 2 * vertexInTriangle * floatSize
                byteStride = 8
                target = GltfBufferType.ARRAY_BUFFER.opcode
            }
            gltf.addBufferViews(uvBufferView)
            totalTextureSize += uvSize
        }
    }
}

@ExperimentalUnsignedTypes
private fun createVertexAccessor(gltf: GlTF, model: Model) {
    var textureBufferViews = 0
    for(triangleId in 0 until model.triangleCount) {
        val vertexA = model.triangleVertex1!![triangleId]
        val vertexB = model.triangleVertex2!![triangleId]
        val vertexC = model.triangleVertex3!![triangleId]

        val nVerA = model.vertexNormals?.get(vertexA)!!.normalize()
        val nVerB = model.vertexNormals?.get(vertexB)!!.normalize()
        val nVerC = model.vertexNormals?.get(vertexC)!!.normalize()
        val normalAccessor = Accessor().apply {
            bufferView = 1 + triangleId + textureBufferViews
            byteOffset = 0
            count = vertexInTriangle
            type = "VEC3"
            componentType = GltfDataType.FLOAT.opcode
            max = arrayOf(
                max(max(nVerA.x, nVerB.x), nVerC.x),
                max(max(nVerA.y * -1, nVerB.y * -1), nVerC.y * -1),
                max(max(nVerA.z * -1, nVerB.z * -1), nVerC.z * -1)
            )
            min = arrayOf(
                min(min(nVerA.x, nVerB.x), nVerC.x),
                min(min(nVerA.y * -1, nVerB.y * -1), nVerC.y * -1),
                min(min(nVerA.z * -1, nVerB.z * -1), nVerC.z * -1)
            )
        }
        gltf.addAccessors(normalAccessor)

        val xVert = listOf(
            model.vertexPositionsX!![vertexA].toFloat(),
            model.vertexPositionsX!![vertexB].toFloat(),
            model.vertexPositionsX!![vertexC].toFloat()
        )
        val yVert = listOf(
            model.vertexPositionsY!![vertexA].toFloat() * -1,
            model.vertexPositionsY!![vertexB].toFloat() * -1,
            model.vertexPositionsY!![vertexC].toFloat() * -1
        )
        val zVert = listOf(
            model.vertexPositionsZ!![vertexA].toFloat() * -1,
            model.vertexPositionsZ!![vertexB].toFloat() * -1,
            model.vertexPositionsZ!![vertexC].toFloat() * -1
        )
        val vertexAccessor = Accessor().apply {
            bufferView = 1 + triangleId + textureBufferViews
            byteOffset = vertexInTriangle * floatInVertex * floatSize
            count = vertexInTriangle
            type = "VEC3"
            componentType = GltfDataType.FLOAT.opcode
            max = arrayOf(xVert.max(), yVert.max(), zVert.max())
            min = arrayOf(xVert.min(), yVert.min(), zVert.min())
        }
        gltf.addAccessors(vertexAccessor)

        val textureId = if(model.triangleTextures == null) -1 else model.triangleTextures!![triangleId].toInt()
        if(textureId != -1) {
            val uvAccessor = Accessor().apply {
                bufferView = 2 + triangleId + textureBufferViews
                byteOffset = 0
                count = vertexInTriangle
                type = "VEC2"
                componentType = GltfDataType.FLOAT.opcode
                max = arrayOf(model.triangleTextUCo!![triangleId]!!.max(), model.triangleTextVCo!![triangleId]!!.max())
                min = arrayOf(model.triangleTextUCo!![triangleId]!!.min(), model.triangleTextVCo!![triangleId]!!.min())
            }
            gltf.addAccessors(uvAccessor)
            textureBufferViews++
        }
    }
}

@ExperimentalUnsignedTypes
private fun createTextures(gltf: GlTF, model: Model): Map<Int, Int> {
    val textureMap = mutableMapOf<Int, Int>()
    val gltfTextureMap =  mutableMapOf<Int, Int>()
    var localIndex = 0
    model.triangleTextures!!.forEachIndexed { triangleId, textureId ->
        if(textureId.toInt() != -1) {
            val textureIndices = textureMap.filterValues { it == textureId.toInt() }.keys
            if(textureIndices.isEmpty()) { // new texture
                val image = Image().apply {
                    uri = "$textureId.png"
                }
                gltf.addImages(image)
                val texture = Texture().apply {
                    source = localIndex
                }
                gltf.addTextures(texture)
                textureMap[localIndex] = textureId.toInt()
                gltfTextureMap[triangleId] = localIndex
                localIndex++
            } else { // already existing texture
                val locTextureIndex = textureIndices.first()
                gltfTextureMap[triangleId] = locTextureIndex
            }
        }
    }
    return gltfTextureMap.toMap()
}

@ExperimentalUnsignedTypes
private fun createMaterials(gltf: GlTF, model: Model, textures: Map<Int, Int>): Map<Int, Int> {
    val colorMap = mutableMapOf<Int, Int>()
    val gltfMaterialMap = mutableMapOf<Int, Int>()
    var localIndex = 0
    for(triangleId in 0 until model.triangleCount) {
        val textureId = if(model.triangleTextures == null) -1 else model.triangleTextures!![triangleId].toInt()
        if (textureId == -1) { // Triangle has color
            val colorBitPack = model.triangleColors!![triangleId].toInt()
            val colorIndices = colorMap.filterValues { it == colorBitPack }.keys
            if(colorIndices.isEmpty()) { // new color
                val alpha = if(model.triangleAlphas != null) {
                    ((model.triangleAlphas!![triangleId].toInt() and 0xFF) / 255).toFloat()
                } else 1f
                val material = createColorMaterial(
                    rs2hsbToColor(colorBitPack), alpha
                )
                gltf.addMaterials(material)
                colorMap[localIndex] = colorBitPack
                gltfMaterialMap[triangleId] = localIndex
                localIndex++
            } else { // already existing color
                val locColorIndex = colorIndices.first()
                gltfMaterialMap[triangleId] = locColorIndex
            }
        } else { // triangle has texture
            val texturIndex = textures[triangleId]
            val material = Material().apply {
                pbrMetallicRoughness = MaterialPbrMetallicRoughness().apply {
                    baseColorTexture = TextureInfo().apply {
                        index = texturIndex
                    }
                    alphaMode = "MASK"
                }
            }
            gltf.addMaterials(material)
            gltfMaterialMap[triangleId] = localIndex
            localIndex++
        }
    }
    return gltfMaterialMap.toMap()
}

@ExperimentalUnsignedTypes
private fun createMesh(model: Model, materialMap: Map<Int, Int>): Mesh {
    val mesh = Mesh()
    var textureOffset = 0
    for(triangleId in 0 until model.triangleCount) {
        val primitive = MeshPrimitive().apply {
            indices = 0
            material = materialMap[triangleId]
            addAttributes("NORMAL", 1 + triangleId * 2 + textureOffset)
            addAttributes("POSITION", 2 + triangleId * 2 + textureOffset)
            val textureId = if(model.triangleTextures == null) -1 else model.triangleTextures!![triangleId].toInt()
            if(textureId != -1) {
                addAttributes("TEXCOORD_0", 3 + triangleId * 2 + textureOffset)
                textureOffset++
            }
        }
        mesh.addPrimitives(primitive)
    }
    return mesh
}

private fun createColorMaterial(color: Color, alpha: Float) = Material().apply {
    pbrMetallicRoughness = MaterialPbrMetallicRoughness().apply {
        val colors = color.getComponents(FloatArray(4))
        colors[colors.lastIndex] = alpha
        baseColorFactor = colors
        metallicFactor = 0.0f
    }
}