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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.cache.model

import io.guthix.buffer.readSmallSmart
import io.netty.buffer.ByteBuf
import kotlin.math.sqrt

class Model(var id: Int) {
    var vertexCount = 0
    var triangleCount = 0
    var textureTriangleCount = 0
    var renderPriority: Byte? = null

    var vertexPositionsX: IntArray? = null
    var vertexPositionsY: IntArray? = null
    var vertexPositionsZ: IntArray? = null
    var vertexSkins: IntArray? = null

    var triangleVertex1: IntArray? = null
    var triangleVertex2: IntArray? = null
    var triangleVertex3: IntArray? = null
    var triangleAlphas: ByteArray? = null
    var triangleColors: IntArray? = null
    var triangleRenderPriorities: ByteArray? = null
    var triangleRenderTypes: ByteArray? = null
    var triangleTextures: IntArray? = null
    var triangleSkins: IntArray? = null

    var textureTriangleVertex1: IntArray? = null
    var textureTriangleVertex2: IntArray? = null
    var textureTriangleVertex3: IntArray? = null
    var textureCoordinates: ByteArray? = null
    var textureRenderTypes: ByteArray? = null

    var vertexNormals: Array<VertexNormal>? = null
    var faceNormals: Array<FaceNormal?>? = null

    var triangleTextUCo: Array<FloatArray?>? = null
    var triangleTextVCo: Array<FloatArray?>? = null

    fun computeNormals() {
        vertexNormals = Array(vertexCount) { VertexNormal() }
        for(i in 0 until triangleCount) {
            val vertexA = triangleVertex1!![i]
            val vertexB = triangleVertex2!![i]
            val vertexC = triangleVertex3!![i]

            // get 2 line vectors
            val xAB = vertexPositionsX!![vertexB] - vertexPositionsX!![vertexA]
            val yAB = vertexPositionsY!![vertexB] - vertexPositionsY!![vertexA]
            val zAB = vertexPositionsZ!![vertexB] - vertexPositionsZ!![vertexA]
            val xAC = vertexPositionsX!![vertexC] - vertexPositionsX!![vertexA]
            val yAC = vertexPositionsY!![vertexC] - vertexPositionsY!![vertexA]
            val zAC = vertexPositionsZ!![vertexC] - vertexPositionsZ!![vertexA]

            // compute cross product
            var xN = yAB * zAC - zAB * yAC
            var yN = zAB * xAC - xAB * zAC
            var zN = xAB * yAC - yAB * xAC

            while (xN > 8192 || yN > 8192 || zN > 8192 || xN < -8192 || yN < -8192 || zN < -8192) {
                xN = xN shr 1
                yN = yN shr 1
                zN = zN shr 1
            }

            var vectorLength = sqrt((xN * xN + yN * yN + zN * zN).toDouble()).toInt()
            if (vectorLength <= 0) {
                vectorLength = 1
            }
            xN = xN * 256 / vectorLength
            yN = yN * 256 / vectorLength
            zN = zN * 256 / vectorLength

            val renderType = if(triangleRenderTypes == null) 0 else triangleRenderTypes!![i].toInt()
            if (renderType == 0) {
                var vertexNormal = vertexNormals!![vertexA]
                vertexNormal.x += xN
                vertexNormal.y += yN
                vertexNormal.z += zN
                vertexNormal.magnitude++

                vertexNormal = vertexNormals!![vertexB]
                vertexNormal.x += xN
                vertexNormal.y += yN
                vertexNormal.z += zN
                vertexNormal.magnitude++

                vertexNormal = vertexNormals!![vertexC]
                vertexNormal.x += xN
                vertexNormal.y += yN
                vertexNormal.z += zN
                vertexNormal.magnitude++
            } else if (renderType == 1) {
                if (faceNormals == null) {
                    faceNormals = arrayOfNulls(triangleCount)
                }
                faceNormals!![i] = FaceNormal(xN, yN, zN)
            }
        }
    }

    fun computeTextureUVCoordinates() {
        triangleTextUCo = arrayOfNulls(triangleCount)
        triangleTextVCo = arrayOfNulls(triangleCount)

        for (i in 0 until triangleCount) {
            val textureCoordinate = if (textureCoordinates == null) -1 else textureCoordinates!![i].toInt()
            val textureId = if (triangleTextures == null) -1 else (triangleTextures!![i] and 0xFFFF)
            if (textureId != -1) {
                val u = FloatArray(3)
                val v = FloatArray(3)
                if (textureCoordinate == -1) {
                    u[0] = 0.0f
                    v[0] = 1.0f
                    u[1] = 1.0f
                    v[1] = 1.0f
                    u[2] = 0.0f
                    v[2] = 0.0f
                } else {
                    val textureRenderType = if (textureRenderTypes == null) {
                        0
                    } else {
                        textureRenderTypes!![textureCoordinate]
                    }
                    if (textureRenderType.toInt() == 0) {
                        val vertexId1 = triangleVertex1!![i]
                        val vertexId2 = triangleVertex2!![i]
                        val vertexId3 = triangleVertex3!![i]

                        val textureVertexId1 = textureTriangleVertex1!![textureCoordinate]
                        val textureVertexId2 = textureTriangleVertex2!![textureCoordinate]
                        val textureVertexId3 = textureTriangleVertex3!![textureCoordinate]

                        val triangleX = vertexPositionsX!![textureVertexId1].toFloat()
                        val triangleY = vertexPositionsY!![textureVertexId1].toFloat()
                        val triangleZ = vertexPositionsZ!![textureVertexId1].toFloat()

                        val f_882_ = vertexPositionsX!![textureVertexId2].toFloat() - triangleX
                        val f_883_ = vertexPositionsY!![textureVertexId2].toFloat() - triangleY
                        val f_884_ = vertexPositionsZ!![textureVertexId2].toFloat() - triangleZ
                        val f_885_ = vertexPositionsX!![textureVertexId3].toFloat() - triangleX
                        val f_886_ = vertexPositionsY!![textureVertexId3].toFloat() - triangleY
                        val f_887_ = vertexPositionsZ!![textureVertexId3].toFloat() - triangleZ
                        val f_888_ = vertexPositionsX!![vertexId1].toFloat() - triangleX
                        val f_889_ = vertexPositionsY!![vertexId1].toFloat() - triangleY
                        val f_890_ = vertexPositionsZ!![vertexId1].toFloat() - triangleZ
                        val f_891_ = vertexPositionsX!![vertexId2].toFloat() - triangleX
                        val f_892_ = vertexPositionsY!![vertexId2].toFloat() - triangleY
                        val f_893_ = vertexPositionsZ!![vertexId2].toFloat() - triangleZ
                        val f_894_ = vertexPositionsX!![vertexId3].toFloat() - triangleX
                        val f_895_ = vertexPositionsY!![vertexId3].toFloat() - triangleY
                        val f_896_ = vertexPositionsZ!![vertexId3].toFloat() - triangleZ

                        val f_897_ = f_883_ * f_887_ - f_884_ * f_886_
                        val f_898_ = f_884_ * f_885_ - f_882_ * f_887_
                        val f_899_ = f_882_ * f_886_ - f_883_ * f_885_
                        var f_900_ = f_886_ * f_899_ - f_887_ * f_898_
                        var f_901_ = f_887_ * f_897_ - f_885_ * f_899_
                        var f_902_ = f_885_ * f_898_ - f_886_ * f_897_
                        var f_903_ = 1.0f / (f_900_ * f_882_ + f_901_ * f_883_ + f_902_ * f_884_)

                        u[0] = (f_900_ * f_888_ + f_901_ * f_889_ + f_902_ * f_890_) * f_903_
                        u[1] = (f_900_ * f_891_ + f_901_ * f_892_ + f_902_ * f_893_) * f_903_
                        u[2] = (f_900_ * f_894_ + f_901_ * f_895_ + f_902_ * f_896_) * f_903_

                        f_900_ = f_883_ * f_899_ - f_884_ * f_898_
                        f_901_ = f_884_ * f_897_ - f_882_ * f_899_
                        f_902_ = f_882_ * f_898_ - f_883_ * f_897_
                        f_903_ = 1.0f / (f_900_ * f_885_ + f_901_ * f_886_ + f_902_ * f_887_)

                        v[0] = (f_900_ * f_888_ + f_901_ * f_889_ + f_902_ * f_890_) * f_903_
                        v[1] = (f_900_ * f_891_ + f_901_ * f_892_ + f_902_ * f_893_) * f_903_
                        v[2] = (f_900_ * f_894_ + f_901_ * f_895_ + f_902_ * f_896_) * f_903_
                    }
                }
                triangleTextUCo!![i] = u
                triangleTextVCo!![i] = v
            }
        }
    }

    companion object {
        fun decode(id: Int, data: ByteBuf): Model {
            val model = Model(id)
            return if (data.getByte(data.writerIndex() - 1).toInt() == -1 &&
                data.getByte(data.writerIndex() - 2).toInt() == -1
            ) {
                decodeNew(model, data)
            } else {
                decodeOld(model, data)
            }
        }

        fun decodeNew(model: Model, data: ByteBuf): Model {
            val buf1 = data.duplicate()
            val buf2 = data.duplicate()
            val buf3 = data.duplicate()
            val buf4 = data.duplicate()
            val buf5 = data.duplicate()
            val buf6 = data.duplicate()
            val buf7 = data.duplicate()
            buf1.readerIndex(buf1.writerIndex() - 23)
            val vertexCount = buf1.readUnsignedShort()
            val triangleCount = buf1.readUnsignedShort()
            val textureTriangleCount = buf1.readUnsignedByte().toInt()
            val hasFaceRenderTypes = buf1.readUnsignedByte().toInt()
            val modelPriority = buf1.readUnsignedByte().toInt()
            val hasFaceAlphas = buf1.readUnsignedByte().toInt()
            val hasFaceSkins = buf1.readUnsignedByte().toInt()
            val hasTexture = buf1.readUnsignedByte().toInt()
            val hasVertexSkins = buf1.readUnsignedByte().toInt()
            val var20 = buf1.readUnsignedShort()
            val var21 = buf1.readUnsignedShort()
            val var42 = buf1.readUnsignedShort()
            val var22 = buf1.readUnsignedShort()
            val var38 = buf1.readUnsignedShort()
            var textureAmount = 0
            var var23 = 0
            var var29 = 0
            if (textureTriangleCount > 0) {
                model.textureRenderTypes = ByteArray(textureTriangleCount)
                buf1.readerIndex(0)
                for (i in 0 until textureTriangleCount) {
                    model.textureRenderTypes!![i] = buf1.readByte()
                    val renderType = model.textureRenderTypes!![i]
                    if (renderType.toInt() == 0) {
                        textureAmount++
                    }
                    if (renderType in 1..3) {
                        var23++
                        if (renderType.toInt() == 2) {
                            var29++
                        }
                    }
                }
            }
            val renderTypePos = textureTriangleCount + vertexCount
            var position = renderTypePos
            if (hasFaceRenderTypes == 1) position += triangleCount
            val triangleTypePos = position
            position += triangleCount
            val priorityPos = position
            if (modelPriority == 0xFF) position += triangleCount
            val triangleSkinPos = position
            if (hasFaceSkins == 1) position += triangleCount
            val vertexSkinsPos = position
            if (hasVertexSkins == 1) position += vertexCount
            val alphaPos = position
            if (hasFaceAlphas == 1) position += triangleCount
            val vertexIdPos = position
            position += var22
            val texturePos = position
            if (hasTexture == 1) position += triangleCount * 2
            val textureCoordPos = position
            position += var38
            val colorPos = position
            position += triangleCount * 2
            val vertexXOffsetsPos = position
            position += var20
            val vertexYOffsetPos = position
            position += var21
            val vertexZOffsetPos = position
            position += var42
            val textureTriangleVertexPos = position
            position += textureAmount * 6
            position += var23 * 17
            position += var29 * 2
            model.vertexCount = vertexCount
            model.triangleCount = triangleCount
            model.textureTriangleCount = textureTriangleCount
            model.vertexPositionsX = IntArray(vertexCount)
            model.vertexPositionsY = IntArray(vertexCount)
            model.vertexPositionsZ = IntArray(vertexCount)
            model.triangleVertex1 = IntArray(triangleCount)
            model.triangleVertex2 = IntArray(triangleCount)
            model.triangleVertex3 = IntArray(triangleCount)
            model.triangleColors = IntArray(triangleCount)
            if (hasVertexSkins == 1) model.vertexSkins = IntArray(vertexCount)
            if (hasFaceRenderTypes == 1) model.triangleRenderTypes = ByteArray(triangleCount)
            if (hasFaceAlphas == 1) model.triangleAlphas = ByteArray(triangleCount)
            if (hasFaceSkins == 1) model.triangleSkins = IntArray(triangleCount)
            if (hasTexture == 1) model.triangleTextures = IntArray(triangleCount)
            if (hasTexture == 1 && textureTriangleCount > 0) model.textureCoordinates = ByteArray(triangleCount)
            if (modelPriority == 0xFF) {
                model.triangleRenderPriorities = ByteArray(triangleCount)
            } else {
                model.renderPriority = modelPriority.toByte()
            }
            if (textureTriangleCount > 0) {
                model.textureTriangleVertex1 = IntArray(textureTriangleCount)
                model.textureTriangleVertex2 = IntArray(textureTriangleCount)
                model.textureTriangleVertex3 = IntArray(textureTriangleCount)
            }
            decodeVertexPositions(
                model, hasVertexSkins, data, textureTriangleCount, vertexXOffsetsPos, vertexYOffsetPos,
                vertexZOffsetPos, vertexSkinsPos
            )
            buf1.readerIndex(colorPos)
            buf2.readerIndex(renderTypePos)
            buf3.readerIndex(priorityPos)
            buf4.readerIndex(alphaPos)
            buf5.readerIndex(triangleSkinPos)
            buf6.readerIndex(texturePos)
            buf7.readerIndex(textureCoordPos)
            for (i in 0 until triangleCount) {
                model.triangleColors!![i] = buf1.readUnsignedShort()
                if (modelPriority == 0xFF) model.triangleRenderPriorities!![i] = buf3.readByte()
                if (hasFaceRenderTypes == 1) model.triangleRenderTypes!![i] = buf2.readByte()
                if (hasFaceAlphas == 1) model.triangleAlphas!![i] = buf4.readByte()
                if (hasFaceSkins == 1) model.triangleSkins!![i] = buf5.readUnsignedByte().toInt()
                if (hasTexture == 1)  model.triangleTextures!![i] = buf6.readUnsignedShort() - 1
                if (model.textureCoordinates != null && model.triangleTextures!![i] != -1) {
                    model.textureCoordinates!![i] = (buf7.readUnsignedByte().toInt() - 1).toByte()
                }
            }
            decodeTriangles(model, data, vertexIdPos, triangleTypePos)
            decodeTextureVertexPositionsNew(
                model,
                textureTriangleCount,
                data,
                textureTriangleVertexPos
            )
            return model
        }

        fun decodeOld(model: Model, data: ByteBuf): Model {
            var hasFaceRenderTypes = false
            var hasFaceTextures = false
            val buf1 = data.duplicate()
            val buf2 = data.duplicate()
            val buf3 = data.duplicate()
            val buf4 = data.duplicate()
            val buf5 = data.duplicate()
            buf1.readerIndex(data.writerIndex() - 18)
            val verticeCount = buf1.readUnsignedShort()
            val triangleCount = buf1.readUnsignedShort()
            val textureTriangleCount = buf1.readUnsignedByte().toInt()
            val hasTextures = buf1.readUnsignedByte().toInt()
            val modelPriority = buf1.readUnsignedByte().toInt()
            val hasFaceAlphas = buf1.readUnsignedByte().toInt()
            val hasFaceSkins = buf1.readUnsignedByte().toInt()
            val hasVertexSkins = buf1.readUnsignedByte().toInt()
            val var27 = buf1.readUnsignedShort()
            val var20 = buf1.readUnsignedShort()
            buf1.readUnsignedShort()
            val var23 = buf1.readUnsignedShort()
            var vertexZOffsetPos = verticeCount
            vertexZOffsetPos += triangleCount
            val var25 = vertexZOffsetPos
            if (modelPriority == 0xFF) vertexZOffsetPos += triangleCount
            val triangleSkinPos = vertexZOffsetPos
            if (hasFaceSkins == 1) vertexZOffsetPos += triangleCount
            val var42 = vertexZOffsetPos
            if (hasTextures == 1) vertexZOffsetPos += triangleCount
            val vertexSkinsPos = vertexZOffsetPos
            if (hasVertexSkins == 1) vertexZOffsetPos += verticeCount
            val alphaPos = vertexZOffsetPos
            if (hasFaceAlphas == 1) vertexZOffsetPos += triangleCount
            val vertexIdPos = vertexZOffsetPos
            vertexZOffsetPos += var23
            val colorPos = vertexZOffsetPos
            vertexZOffsetPos += triangleCount * 2
            val textureTriangleVertexPos = vertexZOffsetPos
            vertexZOffsetPos += textureTriangleCount * 6
            val vertexXOffsetsPos = vertexZOffsetPos
            vertexZOffsetPos += var27
            val vertexYOffsetPos = vertexZOffsetPos
            vertexZOffsetPos += var20
            model.vertexCount = verticeCount
            model.triangleCount = triangleCount
            model.textureTriangleCount = textureTriangleCount
            model.vertexPositionsX = IntArray(verticeCount)
            model.vertexPositionsY = IntArray(verticeCount)
            model.vertexPositionsZ = IntArray(verticeCount)
            model.triangleVertex1 = IntArray(triangleCount)
            model.triangleVertex2 = IntArray(triangleCount)
            model.triangleVertex3 = IntArray(triangleCount)
            model.triangleColors = IntArray(triangleCount)
            if (hasVertexSkins == 1) model.vertexSkins = IntArray(verticeCount)
            if (hasFaceAlphas == 1) model.triangleAlphas = ByteArray(triangleCount)
            if (hasFaceSkins == 1) model.triangleSkins = IntArray(triangleCount)
            if (textureTriangleCount > 0) {
                model.textureRenderTypes = ByteArray(textureTriangleCount)
                model.textureTriangleVertex1 = IntArray(textureTriangleCount)
                model.textureTriangleVertex2 = IntArray(textureTriangleCount)
                model.textureTriangleVertex3 = IntArray(textureTriangleCount)
            }
            if (hasTextures == 1) {
                model.triangleRenderTypes = ByteArray(triangleCount)
                model.textureCoordinates = ByteArray(triangleCount)
                model.triangleTextures = IntArray(triangleCount)
            }
            if (modelPriority == 0xFF) {
                model.triangleRenderPriorities = ByteArray(triangleCount)
            } else {
                model.renderPriority = modelPriority.toByte()
            }

            decodeVertexPositions(
                model, hasVertexSkins, data, 0, vertexXOffsetsPos, vertexYOffsetPos,
                vertexZOffsetPos, vertexSkinsPos
            )
            buf1.readerIndex(colorPos)
            buf2.readerIndex(var42)
            buf3.readerIndex(var25)
            buf4.readerIndex(alphaPos)
            buf5.readerIndex(triangleSkinPos)
            for (i in 0 until triangleCount) {
                model.triangleColors!![i] = buf1.readUnsignedShort()
                if (hasTextures == 1) {
                    val trianglePointY = buf2.readUnsignedByte().toInt()
                    if (trianglePointY and 1 == 1) {
                        model.triangleRenderTypes!![i] = 1
                        hasFaceRenderTypes = true
                    } else {
                        model.triangleRenderTypes!![i] = 0
                    }

                    if (trianglePointY and 2 == 2) {
                        model.textureCoordinates!![i] = (trianglePointY shr 2).toByte()
                        model.triangleTextures!![i] = model.triangleColors!![i]
                        model.triangleColors!![i] = 127
                        if (model.triangleTextures!![i] != -1) {
                            hasFaceTextures = true
                        }
                    } else {
                        model.textureCoordinates!![i] = -1
                        model.triangleTextures!![i] = -1
                    }
                }
                if (modelPriority == 0xFF) {
                    model.triangleRenderPriorities!![i] = buf3.readByte()
                }
                if (hasFaceAlphas == 1) {
                    model.triangleAlphas!![i] = buf4.readByte()
                }
                if (hasFaceSkins == 1) {
                    model.triangleSkins!![i] = buf5.readUnsignedByte().toInt()
                }
            }
            decodeTriangles(model, data, vertexIdPos, verticeCount)
            decodeTextureVertexPositionsOld(
                model,
                textureTriangleCount,
                data,
                textureTriangleVertexPos
            )
            if (model.textureCoordinates != null) {
                var hasTextureCoordinates = false
                for (i in 0 until triangleCount) {
                    if (model.textureCoordinates!![i].toInt() and 255 != 255) {
                        val var21 = model.textureCoordinates!![i].toInt()
                        if (model.textureTriangleVertex1!![var21] and '\uffff'.toInt() ==
                            model.triangleVertex1!![i]
                            && model.textureTriangleVertex2!![var21] and '\uffff'.toInt() ==
                            model.triangleVertex2!![i]
                            && model.textureTriangleVertex3!![var21] and '\uffff'.toInt() ==
                            model.triangleVertex3!![i]
                        ) {
                            model.textureCoordinates!![i] = -1
                        } else {
                            hasTextureCoordinates = true
                        }
                    }
                }
                if (!hasTextureCoordinates) {
                    model.textureCoordinates = null
                }
            }
            if (!hasFaceTextures) {
                model.triangleTextures = null
            }
            if (!hasFaceRenderTypes) {
                model.triangleRenderTypes = null
            }
            return model
        }

        fun decodeTriangles(model: Model, data: ByteBuf, vertexIdPos: Int, triangleTypePos: Int) {
            val vertexIdBuffer = data.duplicate().readerIndex(vertexIdPos)
            val triangleTypeBuffer = data.duplicate().readerIndex(triangleTypePos)
            var vertexId1 = 0
            var vertexId2 = 0
            var vertexId3 = 0
            var lastVertexId = 0
            for (triangleId in 0 until model.triangleCount) {
                when (triangleTypeBuffer.readUnsignedByte().toInt()) {
                    1 -> { // read unconnected triangle
                        vertexId1 = vertexIdBuffer.readSmallSmart() + lastVertexId
                        vertexId2 = vertexIdBuffer.readSmallSmart() + vertexId1
                        vertexId3 = vertexIdBuffer.readSmallSmart()  + vertexId2
                        lastVertexId = vertexId3
                        model.triangleVertex1!![triangleId] = vertexId1
                        model.triangleVertex2!![triangleId] = vertexId2
                        model.triangleVertex3!![triangleId] = vertexId3
                    }
                    2 -> { // read triangle connected to previously read vertices
                        vertexId2 = vertexId3
                        vertexId3 = vertexIdBuffer.readSmallSmart()  + lastVertexId
                        lastVertexId = vertexId3
                        model.triangleVertex1!![triangleId] = vertexId1
                        model.triangleVertex2!![triangleId] = vertexId2
                        model.triangleVertex3!![triangleId] = vertexId3
                    }
                    3 -> { // read triangle connected to previously read vertices
                        vertexId1 = vertexId3
                        vertexId3 = vertexIdBuffer.readSmallSmart()  + lastVertexId
                        lastVertexId = vertexId3
                        model.triangleVertex1!![triangleId] = vertexId1
                        model.triangleVertex2!![triangleId] = vertexId2
                        model.triangleVertex3!![triangleId] = vertexId3
                    }
                    4 -> { // read triangle connected to previously read vertices
                        val resVertexId = vertexId1
                        vertexId1 = vertexId2
                        vertexId2 = resVertexId
                        vertexId3 = vertexIdBuffer.readSmallSmart()  + lastVertexId
                        lastVertexId = vertexId3
                        model.triangleVertex1!![triangleId] = vertexId1
                        model.triangleVertex2!![triangleId] = resVertexId
                        model.triangleVertex3!![triangleId] = vertexId3
                    }
                }
            }
        }

        fun decodeVertexPositions(
            model: Model,
            hasVertexSkins: Int,
            data: ByteBuf,
            vertexFlagsPos: Int,
            vertexXOffsetsPos: Int,
            vertexYOffsetsPos: Int,
            vertexZOffsetsPos: Int,
            vertexSkinsPos: Int
        ) {
            val vertexFlagBuffer = data.duplicate().readerIndex(vertexFlagsPos)
            val vertexXOffsetBuffer = data.duplicate().readerIndex(vertexXOffsetsPos)
            val vertexYOffsetBuffer = data.duplicate().readerIndex(vertexYOffsetsPos)
            val vertexZOffsetsBuffer = data.duplicate().readerIndex(vertexZOffsetsPos)
            val vertexSkinsBuffer = data.duplicate().readerIndex(vertexSkinsPos)
            var vX = 0
            var vY = 0
            var vZ = 0
            for (i in 0 until model.vertexCount) {
                val vertexFlags = vertexFlagBuffer.readUnsignedByte().toInt()
                model.vertexPositionsX!![i] = vX + if (vertexFlags and 1 != 0) {
                    vertexXOffsetBuffer.readSmallSmart()
                } else 0
                model.vertexPositionsY!![i] = vY + if (vertexFlags and 2 != 0) {
                    vertexYOffsetBuffer.readSmallSmart()
                } else 0
                model.vertexPositionsZ!![i] = vZ + if (vertexFlags and 4 != 0) {
                    vertexZOffsetsBuffer.readSmallSmart()
                } else 0
                vX = model.vertexPositionsX!![i]
                vY = model.vertexPositionsY!![i]
                vZ = model.vertexPositionsZ!![i]
                if (hasVertexSkins == 1) {
                    model.vertexSkins!![i] = vertexSkinsBuffer.readUnsignedByte().toInt()
                }
            }
        }

        fun decodeTextureVertexPositionsNew(
            model: Model,
            textureTriangleCount: Int,
            data: ByteBuf,
            textureTriangleVertexPos: Int
        ) {
            val textureTriangleVertexBuffer = data.duplicate().readerIndex(textureTriangleVertexPos)
            for (i in 0 until textureTriangleCount) {
                if(model.textureRenderTypes!![i].toInt() and 255 == 0) {
                    model.textureTriangleVertex1!![i] = textureTriangleVertexBuffer.readUnsignedShort()
                    model.textureTriangleVertex2!![i] = textureTriangleVertexBuffer.readUnsignedShort()
                    model.textureTriangleVertex3!![i] = textureTriangleVertexBuffer.readUnsignedShort()
                }
            }
        }

        fun decodeTextureVertexPositionsOld(
            model: Model,
            textureTriangleCount: Int,
            data: ByteBuf,
            textureTriangleVertexPos: Int
        ) {
            val textureTriangleVertexBuffer = data.duplicate().readerIndex(textureTriangleVertexPos)
            for (i in 0 until textureTriangleCount) {
                model.textureRenderTypes!![i] = 0
                model.textureTriangleVertex1!![i] = textureTriangleVertexBuffer.readUnsignedShort()
                model.textureTriangleVertex2!![i] = textureTriangleVertexBuffer.readUnsignedShort()
                model.textureTriangleVertex3!![i] = textureTriangleVertexBuffer.readUnsignedShort()
            }
        }
    }
}