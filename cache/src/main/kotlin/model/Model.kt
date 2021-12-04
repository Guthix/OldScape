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

import io.guthix.buffer.readSmallSmart
import io.netty.buffer.ByteBuf
import kotlin.math.sqrt

public class Model(public var id: Int) {
    public var vertexCount: Int = 0
    public var triangleCount: Int = 0
    public var textureTriangleCount: Int = 0
    public var renderPriority: Byte? = null

    public var vertexPositionsX: IntArray? = null
    public var vertexPositionsY: IntArray? = null
    public var vertexPositionsZ: IntArray? = null
    public var vertexSkins: IntArray? = null

    public var triangleVertex1: IntArray? = null
    public var triangleVertex2: IntArray? = null
    public var triangleVertex3: IntArray? = null
    public var triangleAlphas: ByteArray? = null
    public var triangleColors: IntArray? = null
    public var triangleRenderPriorities: ByteArray? = null
    public var triangleRenderTypes: ByteArray? = null
    public var triangleTextures: IntArray? = null
    public var triangleSkins: IntArray? = null

    public var textureTriangleVertex1: IntArray? = null
    public var textureTriangleVertex2: IntArray? = null
    public var textureTriangleVertex3: IntArray? = null
    public var textureCoordinates: ByteArray? = null
    public var textureRenderTypes: ByteArray? = null

    public var vertexNormals: Array<VertexNormal>? = null
    public var faceNormals: Array<FaceNormal?>? = null

    public var triangleTextUCo: Array<FloatArray?>? = null
    public var triangleTextVCo: Array<FloatArray?>? = null

    public fun computeNormals() {
        vertexNormals = Array(vertexCount) { VertexNormal() }
        for (i in 0 until triangleCount) {
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

            val renderType = if (triangleRenderTypes == null) 0 else triangleRenderTypes!![i].toInt()
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

    public fun computeTextureUVCoordinates() {
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

                        val triangleX1 = vertexPositionsX!![textureVertexId1].toFloat()
                        val triangleY1 = vertexPositionsY!![textureVertexId1].toFloat()
                        val triangleZ1 = vertexPositionsZ!![textureVertexId1].toFloat()
                        val triangleX2 = vertexPositionsX!![textureVertexId2].toFloat() - triangleX1
                        val triangleY2 = vertexPositionsY!![textureVertexId2].toFloat() - triangleY1
                        val triangleZ2 = vertexPositionsZ!![textureVertexId2].toFloat() - triangleZ1
                        val triangleX3 = vertexPositionsX!![textureVertexId3].toFloat() - triangleX1
                        val triangleY3 = vertexPositionsY!![textureVertexId3].toFloat() - triangleY1
                        val triangleZ3 = vertexPositionsZ!![textureVertexId3].toFloat() - triangleZ1
                        val x1 = vertexPositionsX!![vertexId1].toFloat() - triangleX1
                        val y1 = vertexPositionsY!![vertexId1].toFloat() - triangleY1
                        val z1 = vertexPositionsZ!![vertexId1].toFloat() - triangleZ1
                        val x2 = vertexPositionsX!![vertexId2].toFloat() - triangleX1
                        val y2 = vertexPositionsY!![vertexId2].toFloat() - triangleY1
                        val z2 = vertexPositionsZ!![vertexId2].toFloat() - triangleZ1
                        val x3 = vertexPositionsX!![vertexId3].toFloat() - triangleX1
                        val y3 = vertexPositionsY!![vertexId3].toFloat() - triangleY1
                        val z3 = vertexPositionsZ!![vertexId3].toFloat() - triangleZ1

                        val yz = triangleY2 * triangleZ3 - triangleZ2 * triangleY3
                        val zx = triangleZ2 * triangleX3 - triangleX2 * triangleZ3
                        val xy = triangleX2 * triangleY3 - triangleY2 * triangleX3
                        var xyz = triangleY3 * xy - triangleZ3 * zx
                        var yzx = triangleZ3 * yz - triangleX3 * xy
                        var zxy = triangleX3 * zx - triangleY3 * yz
                        var res = 1.0f / (xyz * triangleX2 + yzx * triangleY2 + zxy * triangleZ2)

                        u[0] = (xyz * x1 + yzx * y1 + zxy * z1) * res
                        u[1] = (xyz * x2 + yzx * y2 + zxy * z2) * res
                        u[2] = (xyz * x3 + yzx * y3 + zxy * z3) * res

                        xyz = triangleY2 * xy - triangleZ2 * zx
                        yzx = triangleZ2 * yz - triangleX2 * xy
                        zxy = triangleX2 * zx - triangleY2 * yz
                        res = 1.0f / (xyz * triangleX3 + yzx * triangleY3 + zxy * triangleZ3)

                        v[0] = (xyz * x1 + yzx * y1 + zxy * z1) * res
                        v[1] = (xyz * x2 + yzx * y2 + zxy * z2) * res
                        v[2] = (xyz * x3 + yzx * y3 + zxy * z3) * res
                    }
                }
                triangleTextUCo!![i] = u
                triangleTextVCo!![i] = v
            }
        }
    }

    public companion object {
        public fun decode(id: Int, data: ByteBuf): Model {
            val model = Model(id)
            return if (data.getByte(data.writerIndex() - 1).toInt() == -1 &&
                data.getByte(data.writerIndex() - 2).toInt() == -1
            ) {
                decodeNew(model, data)
            } else {
                decodeOld(model, data)
            }
        }

        private fun decodeNew(model: Model, data: ByteBuf): Model {
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
                if (hasTexture == 1) model.triangleTextures!![i] = buf6.readUnsignedShort() - 1
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

        private fun decodeOld(model: Model, data: ByteBuf): Model {
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
                        if (model.textureTriangleVertex1!![var21] and '\uffff'.code ==
                            model.triangleVertex1!![i]
                            && model.textureTriangleVertex2!![var21] and '\uffff'.code ==
                            model.triangleVertex2!![i]
                            && model.textureTriangleVertex3!![var21] and '\uffff'.code ==
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

        private fun decodeTriangles(model: Model, data: ByteBuf, vertexIdPos: Int, triangleTypePos: Int) {
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
                        vertexId3 = vertexIdBuffer.readSmallSmart() + vertexId2
                        lastVertexId = vertexId3
                        model.triangleVertex1!![triangleId] = vertexId1
                        model.triangleVertex2!![triangleId] = vertexId2
                        model.triangleVertex3!![triangleId] = vertexId3
                    }
                    2 -> { // read triangle connected to previously read vertices
                        vertexId2 = vertexId3
                        vertexId3 = vertexIdBuffer.readSmallSmart() + lastVertexId
                        lastVertexId = vertexId3
                        model.triangleVertex1!![triangleId] = vertexId1
                        model.triangleVertex2!![triangleId] = vertexId2
                        model.triangleVertex3!![triangleId] = vertexId3
                    }
                    3 -> { // read triangle connected to previously read vertices
                        vertexId1 = vertexId3
                        vertexId3 = vertexIdBuffer.readSmallSmart() + lastVertexId
                        lastVertexId = vertexId3
                        model.triangleVertex1!![triangleId] = vertexId1
                        model.triangleVertex2!![triangleId] = vertexId2
                        model.triangleVertex3!![triangleId] = vertexId3
                    }
                    4 -> { // read triangle connected to previously read vertices
                        val resVertexId = vertexId1
                        vertexId1 = vertexId2
                        vertexId2 = resVertexId
                        vertexId3 = vertexIdBuffer.readSmallSmart() + lastVertexId
                        lastVertexId = vertexId3
                        model.triangleVertex1!![triangleId] = vertexId1
                        model.triangleVertex2!![triangleId] = resVertexId
                        model.triangleVertex3!![triangleId] = vertexId3
                    }
                }
            }
        }

        private fun decodeVertexPositions(
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

        private fun decodeTextureVertexPositionsNew(
            model: Model,
            textureTriangleCount: Int,
            data: ByteBuf,
            textureTriangleVertexPos: Int
        ) {
            val textureTriangleVertexBuffer = data.duplicate().readerIndex(textureTriangleVertexPos)
            for (i in 0 until textureTriangleCount) {
                if (model.textureRenderTypes!![i].toInt() and 255 == 0) {
                    model.textureTriangleVertex1!![i] = textureTriangleVertexBuffer.readUnsignedShort()
                    model.textureTriangleVertex2!![i] = textureTriangleVertexBuffer.readUnsignedShort()
                    model.textureTriangleVertex3!![i] = textureTriangleVertexBuffer.readUnsignedShort()
                }
            }
        }

        private fun decodeTextureVertexPositionsOld(
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