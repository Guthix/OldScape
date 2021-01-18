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
package io.guthix.oldscape.server.world.entity

import io.guthix.oldscape.server.PropertyHolder
import io.guthix.oldscape.server.ServerContext
import io.guthix.oldscape.server.template.ObjTemplate
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.reflect.KProperty

@Serializable
data class Obj(val id: Int, var quantity: Int) : PropertyHolder {
    val template: ObjTemplate by lazy { ServerContext.objTemplates[id] }
    val name: String get() = template.name
    val isStackable: Boolean get() = template.isStackable
    val isTradable: Boolean get() = template.isTradable
    val notedId: Int? get() = template.notedId
    val isNoted: Boolean get() = template.isNoted
    val placeHolderId: Int? get() = template.placeHolderId
    val isPlaceHolder: Boolean get() = template.isPlaceHolder
    val interfaceOperations: Array<String?> get() = template.interfaceOperations
    val groundOperations: Array<String?> get() = template.groundOperations

    @Transient
    override val properties: MutableMap<KProperty<*>, Any?> = mutableMapOf()
}