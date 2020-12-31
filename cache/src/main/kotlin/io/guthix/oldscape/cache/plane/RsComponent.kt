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
package io.guthix.oldscape.cache.plane

import io.guthix.buffer.readStringCP1252
import io.netty.buffer.ByteBuf

public data class RsComponent(val id: Int) {
    var hasScript: Boolean = false
    var menuType: Int = 0
    var contentType: Int = 0
    var originalX: Short = 0
    var originalY: Short = 0
    var originalWidth: Int = 0
    var originalHeight: Int = 0
    var opacity: Short = 0
    var parentId: Int? = null
    var hoveredSiblingId: Int? = null
    var cs1InstructionCount: Array<Array<Int?>>? = null
    var scrollHeight: Int = 0
    var scrollWidth: Int = 0
    var isHidden: Boolean = false
    var itemIds: IntArray? = null
    var itemQuantities: IntArray? = null
    var clickMask: Int = 0
    var xPitch: Short = 0
    var yPitch: Short = 0
    var xOffsets: ShortArray? = null
    var yOffsets: ShortArray? = null
    var sprites: Array<Int?>? = null
    var configActions: Array<String?>? = null
    var isFilled: Boolean = false
    var xTextAlignment: Short = 0
    var yTextAlignment: Short = 0
    var lineHeight: Short = 0
    var fontId: Int? = null
    var textIsShadowed: Boolean = false
    var text: String = ""
    var alternateText: String = ""
    var textColor: Int = 0
    var alternateTextColor: Int = 0
    var hoveredTextColor: Int = 0
    var alternateHoveredTextColor: Int = 0
    var spriteId: Int? = null
    var alternateSpriteId: Int? = null
    var modelType: Int = 1
    var modelId: Int? = null
    var alternateModelId: Int? = null
    var animationId: Int? = null
    var alternateAnimationId: Int? = null
    var modelZoom: Int = 100
    var rotationX: Int = 0
    var rotationY: Int = 0
    var rotationZ: Int = 0
    var targetVerb: String = ""
    var spellName: String = ""
    var tooltip: String = "Ok"
    var dynamicWidth: Byte = 0
    var buttonType: Byte = 0
    var dynamicX: Byte = 0
    var dynamicY: Byte = 0
    var noClickThrough: Boolean = false
    var textureId: Int = 0
    var spriteTiling: Boolean = false
    var borderThickness: Short = 0
    var sprite2: Int = 0
    var flippedVertically: Boolean? = null
    var flippedHorizontally: Boolean? = null
    var offsetX2d: Short = 0
    var offsetY2d: Short = 0
    var orthogonal: Boolean = false
    var modelHeightOverride: Int = 0
    var lineWidth: Short = 0
    var lineDirection: Boolean = false
    var opBase: String = ""
    var actions: Array<String?>? = null
    var dragDeadZone: Short = 0
    var dragDeadTime: Short = 0
    var dragRenderBehavior: Boolean = false
    var onLoadListener: Array<Any?>? = null
    var onMouseOverListener: Array<Any?>? = null
    var onMouseLeaveListener: Array<Any?>? = null
    var onTargetLeaveListener: Array<Any?>? = null
    var onTargetEnterListener: Array<Any?>? = null
    var onVarTransmitListener: Array<Any?>? = null
    var onInvTransmitListener: Array<Any?>? = null
    var onStatTransmitListener: Array<Any?>? = null
    var onTimerListener: Array<Any?>? = null
    var onOpListener: Array<Any?>? = null
    var onMouseRepeatListener: Array<Any?>? = null
    var onClickListener: Array<Any?>? = null
    var onClickRepeatListener: Array<Any?>? = null
    var onReleaseListener: Array<Any?>? = null
    var onHoldListener: Array<Any?>? = null
    var onDragListener: Array<Any?>? = null
    var onDragCompleteListener: Array<Any?>? = null
    var onScrollWheelListener: Array<Any?>? = null
    var varTransmitTriggers: IntArray? = null
    var invTransmitTriggers: IntArray? = null
    var statTransmitTriggers: IntArray? = null

    public companion object {
        public fun decode(id: Int, data: ByteBuf): RsComponent {
            return when(data.getByte(data.readerIndex() + 1).toInt()) {
                -1 -> decodeIf3(id, data)
                else -> decodeIf1(id, data)
            }
        }

        private fun decodeIf1(id: Int, data: ByteBuf): RsComponent {
            val iFace = RsComponent(id)
            iFace.hasScript = false
            val type = data.readUnsignedByte().toInt()
            iFace.menuType = data.readUnsignedByte().toInt()
            iFace.contentType = data.readUnsignedShort()
            iFace.originalX = data.readShort()
            iFace.originalY = data.readShort()
            iFace.originalWidth = data.readUnsignedShort()
            iFace.originalHeight = data.readUnsignedShort()
            iFace.opacity = data.readUnsignedByte()
            iFace.parentId = data.readUnsignedShort()
            iFace.parentId = if(iFace.parentId == 0xFFF) {
                null
            } else {
                iFace.parentId!! + (id and 0xFFFF.inv())
            }
            iFace.hoveredSiblingId = data.readUnsignedShort()
            if(iFace.hoveredSiblingId  == 0xFFFF) iFace.hoveredSiblingId = null
            val operatorSize = data.readUnsignedByte().toInt()
            if (operatorSize > 0) {
                val alternateOperators = ShortArray(operatorSize)
                val alternateRhs = IntArray(operatorSize)
                for(i in 0 until operatorSize) {
                    alternateOperators[i] = data.readUnsignedByte()
                    alternateRhs[i] = data.readUnsignedShort()
                }
            }
            val cs1InstructionCount = data.readUnsignedByte().toInt()
            iFace.cs1InstructionCount = if (cs1InstructionCount > 0) {
                Array(cs1InstructionCount) {
                    val byteCodeSize = data.readUnsignedShort()
                    val instructions = Array(byteCodeSize) {
                        var byteCode: Int? = data.readUnsignedShort()
                        if(byteCode == 0xFFFF) byteCode = null
                        byteCode
                    }
                    instructions
                }
            } else null
            if(type == 0) {
                iFace.scrollHeight = data.readUnsignedShort()
                iFace.isHidden = data.readUnsignedByte().toInt() == 1
            }
            if(type == 1) {
                data.readUnsignedShort()
                data.readUnsignedByte()
            }
            if (type == 2) {
                iFace.itemIds = IntArray(iFace.originalX * iFace.originalY)
                iFace.itemQuantities = IntArray(iFace.originalX * iFace.originalY)
                if (data.readUnsignedByte().toInt() == 1) iFace.clickMask = iFace.clickMask or 0x10000000
                if (data.readUnsignedByte().toInt() == 1) iFace.clickMask = iFace.clickMask or 0x40000000
                if (data.readUnsignedByte().toInt() == 1) iFace.clickMask = iFace.clickMask or -0x80000000
                if (data.readUnsignedByte().toInt() == 1) iFace.clickMask = iFace.clickMask or 0x20000000
                iFace.xPitch =data.readUnsignedByte()
                iFace.yPitch = data.readUnsignedByte()
                iFace.xOffsets = ShortArray(20)
                iFace.yOffsets = ShortArray(20)
                iFace.sprites = arrayOfNulls(20)
                for(i in 0 until 20) {
                    if (data.readUnsignedByte().toInt() == 1) {
                        iFace.xOffsets!![i] = data.readShort()
                        iFace.yOffsets!![i] = data.readShort()
                        iFace.sprites!![i] = data.readInt()
                    } else {
                        iFace.sprites!![i] = null
                    }
                }
                iFace.configActions = arrayOfNulls(5)
                for(i in 0 until 5) {
                    val configAction = data.readStringCP1252()
                    if (configAction.isNotEmpty()) {
                        iFace.configActions!![i] = configAction
                        iFace.clickMask = iFace.clickMask or (1 shl (i + 23))
                    }
                }
            }
            iFace.isFilled = if(type == 3) data.readUnsignedByte().toInt() == 1 else false
            if (type == 4 || type == 1) {
                iFace.xTextAlignment  = data.readUnsignedByte()
                iFace.yTextAlignment  = data.readUnsignedByte()
                iFace.lineHeight  = data.readUnsignedByte()
                iFace.fontId = data.readUnsignedShort()
                if (iFace.fontId == 0xFFFF) iFace.fontId = null
                iFace.textIsShadowed = data.readUnsignedByte().toInt() == 1
            }
            if (type == 4) {
                iFace.text = data.readStringCP1252()
                iFace.alternateText  = data.readStringCP1252()
            }
            if (type == 1 || type == 3 || type == 4) {
                iFace.textColor = data.readInt()
            }
            if (type == 3 || type == 4) {
                iFace.alternateTextColor = data.readInt()
                iFace.hoveredTextColor = data.readInt()
                iFace.alternateHoveredTextColor = data.readInt()
            }
            if (type == 5) {
                iFace.spriteId = data.readInt()
                iFace.alternateSpriteId = data.readInt()
            }
            if (type == 6) {
                iFace.modelType = 1
                iFace.modelId = data.readUnsignedShort()
                if (iFace.modelId == 0xFFF) iFace.modelId = null
                iFace.alternateModelId = data.readUnsignedShort()
                if (iFace.alternateModelId == 0xFFF) iFace.alternateModelId = null
                iFace.animationId = data.readUnsignedShort()
                if (iFace.animationId == 0xFFF) iFace.animationId = null
                iFace.alternateAnimationId = data.readUnsignedShort()
                if (iFace.alternateAnimationId == 0xFFF) iFace.alternateAnimationId = null
                iFace.modelZoom = data.readUnsignedShort()
                iFace.rotationX = data.readUnsignedShort()
                iFace.rotationZ = data.readUnsignedShort()
            }
            if (type == 7) {
                iFace.itemIds = IntArray(iFace.originalHeight * iFace.originalWidth)
                iFace.itemQuantities = IntArray(iFace.originalWidth * iFace.originalHeight)
                iFace.xTextAlignment = data.readUnsignedByte()
                iFace.fontId = data.readUnsignedShort()
                if (iFace.fontId == 0xFFFF) iFace.fontId = null
                iFace.textIsShadowed = data.readUnsignedByte().toInt() == 1
                iFace.textColor = data.readInt()
                iFace.xPitch = data.readShort()
                iFace.yPitch = data.readShort()
                if (data.readUnsignedByte().toInt() == 1) iFace.clickMask = iFace.clickMask or 0x40000000
                iFace.configActions = arrayOfNulls(5)
                for(i in 0 until 5) {
                    val configAction = data.readStringCP1252()
                    if (configAction.isNotEmpty()) {
                        iFace.configActions!![i] = configAction
                        iFace.clickMask = iFace.clickMask or (1 shl (i + 23))
                    }
                }
            }
            if (type == 8) iFace.text = data.readStringCP1252()
            if (iFace.menuType == 2 || type == 2) {
                iFace.targetVerb = data.readStringCP1252()
                iFace.spellName = data.readStringCP1252()
                val upperMasks = data.readUnsignedShort() and 0x3F
                iFace.clickMask = (upperMasks shl 11) or iFace.clickMask
            }
            if (iFace.menuType == 1 || iFace.menuType == 4 || iFace.menuType == 5 || iFace.menuType == 6) {
                iFace.tooltip = data.readStringCP1252()
                if (iFace.tooltip.isEmpty()) {
                    if (iFace.menuType == 1) iFace.tooltip = "Ok"
                    if (iFace.menuType == 4) iFace.tooltip = "Select"
                    if (iFace.menuType == 5) iFace .tooltip = "Select"
                    if (iFace.menuType == 6) iFace.tooltip = "Continue"
                }
            }
            if (iFace.menuType == 1 || iFace.menuType == 4 || iFace.menuType == 5) {
                iFace.clickMask = iFace.clickMask or 0x400000
            }
            if (iFace.menuType == 6) iFace.clickMask = iFace.clickMask or 0x1
            return iFace
        }

        private fun decodeIf3(id: Int, data: ByteBuf): RsComponent {
            val iFace = RsComponent(id)
            data.readUnsignedByte()
            iFace.hasScript = true
            val type = data.readUnsignedByte().toInt()
            iFace.contentType = data.readUnsignedShort()
            iFace.originalX = data.readShort()
            iFace.originalY = data.readShort()
            iFace.originalWidth = data.readUnsignedShort()
            if (type == 9) {
                iFace.originalHeight = data.readShort().toInt()
            } else {
                iFace.originalHeight = data.readUnsignedShort()
            }
            iFace.dynamicWidth = data.readByte()
            iFace.buttonType = data.readByte()
            iFace.dynamicX = data.readByte()
            iFace.dynamicY = data.readByte()
            iFace.parentId = data.readUnsignedShort()
            iFace.parentId = if(iFace.parentId == 0xFFFF) {
                null
            } else {
                iFace.parentId!! + (id and -0x10000)
            }
            iFace.isHidden = data.readUnsignedByte().toInt() == 1
            if (type == 0) {
                iFace.scrollWidth = data.readUnsignedShort()
                iFace.scrollHeight = data.readUnsignedShort()
                iFace.noClickThrough = data.readUnsignedByte().toInt() == 1
            }
            if (type == 5) {
                iFace.spriteId = data.readInt()
                iFace.textureId = data.readUnsignedShort()
                iFace.spriteTiling = data.readUnsignedByte().toInt() == 1
                iFace.opacity = data.readUnsignedByte()
                iFace.borderThickness = data.readUnsignedByte()
                iFace.sprite2 = data.readInt()
                iFace.flippedVertically = data.readUnsignedByte().toInt() == 1
                iFace.flippedHorizontally = data.readUnsignedByte().toInt() == 1
            }
            if (type == 6) {
                iFace.modelType = 1
                iFace.modelId = data.readUnsignedShort()
                if (iFace.modelId == 0xFFFF) iFace.modelId = null
                iFace.offsetX2d =data.readShort()
                iFace.offsetY2d = data.readShort()
                iFace.rotationX = data.readUnsignedShort()
                iFace.rotationZ = data.readUnsignedShort()
                iFace.rotationY = data.readUnsignedShort()
                iFace.modelZoom = data.readUnsignedShort()
                iFace.animationId = data.readUnsignedShort()
                if (iFace.animationId == 0xFFFF) iFace.animationId = null
                iFace.orthogonal = data.readUnsignedByte().toInt() == 1
                data.readUnsignedShort()
                if (iFace.dynamicWidth.toInt() != 0) iFace.modelHeightOverride = data.readUnsignedShort()
                if (iFace.buttonType.toInt() != 0) data.readUnsignedShort()
            }
            if (type == 4) {
                iFace.fontId = data.readUnsignedShort()
                if (iFace.fontId == 0xFFFF) iFace.fontId = null
                iFace.text = data.readStringCP1252()
                iFace.lineHeight = data.readUnsignedByte()
                iFace.xTextAlignment = data.readUnsignedByte()
                iFace.yTextAlignment = data.readUnsignedByte()
                iFace.textIsShadowed = data.readUnsignedByte().toInt() == 1
                iFace.textColor = data.readInt()
            }
            if (type == 3) {
                iFace.textColor = data.readInt()
                iFace.isFilled = data.readUnsignedByte().toInt() == 1
                iFace.opacity = data.readUnsignedByte()
            }
            if (type == 9) {
                iFace.lineWidth = data.readUnsignedByte()
                iFace.textColor = data.readInt()
                iFace.lineDirection = data.readUnsignedByte().toInt() == 1
            }

            iFace.clickMask = data.readUnsignedMedium()
            iFace.opBase = data.readStringCP1252()
            val actionCount = data.readUnsignedByte().toInt()
            if (actionCount > 0) {
                iFace.actions = arrayOfNulls(actionCount)
                for (int_1 in 0 until actionCount) {
                    iFace.actions!![int_1] = data.readStringCP1252()
                }
            }
            iFace.dragDeadZone = data.readUnsignedByte()
            iFace.dragDeadTime = data.readUnsignedByte()
            iFace.dragRenderBehavior = data.readUnsignedByte().toInt() == 1
            iFace.targetVerb = data.readStringCP1252()
            iFace.onLoadListener = decodeListener(data)
            iFace.onMouseOverListener = decodeListener(data)
            iFace.onMouseLeaveListener = decodeListener(data)
            iFace.onTargetLeaveListener = decodeListener(data)
            iFace.onTargetEnterListener = decodeListener(data)
            iFace.onVarTransmitListener = decodeListener(data)
            iFace.onInvTransmitListener = decodeListener(data)
            iFace.onStatTransmitListener = decodeListener(data)
            iFace.onTimerListener = decodeListener(data)
            iFace.onOpListener = decodeListener(data)
            iFace.onMouseRepeatListener = decodeListener(data)
            iFace.onClickListener = decodeListener(data)
            iFace.onClickRepeatListener = decodeListener(data)
            iFace.onReleaseListener = decodeListener(data)
            iFace.onHoldListener = decodeListener(data)
            iFace.onDragListener = decodeListener(data)
            iFace.onDragCompleteListener = decodeListener(data)
            iFace.onScrollWheelListener = decodeListener(data)
            iFace.varTransmitTriggers = decodeTriggers(data)
            iFace.invTransmitTriggers = decodeTriggers(data)
            iFace.statTransmitTriggers = decodeTriggers(data)
            return iFace
        }

        private fun decodeListener(data: ByteBuf): Array<Any?>? {
            val size = data.readUnsignedByte().toInt()
            return if (size == 0) {
                null
            } else {
                val objects = arrayOfNulls<Any?>(size)
                for (i in 0 until size) {
                    val opcode = data.readUnsignedByte().toInt()
                    if (opcode == 0) {
                        objects[i] = data.readInt()
                    } else if (opcode == 1) {
                        objects[i] = data.readStringCP1252()
                    }
                }
                objects
            }
        }

        private fun decodeTriggers(data: ByteBuf): IntArray? {
            val size = data.readUnsignedByte().toInt()
            return if (size == 0) {
                null
            } else {
                val triggers = IntArray(size)
                for (i in 0 until size) {
                    triggers[i] = data.readInt()
                }
                triggers
            }
        }
    }
}