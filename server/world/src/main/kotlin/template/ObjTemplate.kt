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
package io.guthix.oldscape.server.template

import io.guthix.oldscape.cache.config.ObjConfig

data class ObjTemplate(private val config: ObjConfig) : BaseTemplate() {
    val id: Int get() = config.id
    val name: String get() = config.name
    val isStackable: Boolean get() = config.stackable
    val isTradable: Boolean get() = config.tradable
    val notedId: Int? get() = config.notedId
    val isNoted: Boolean get() = config.isNoted
    val placeHolderId: Int? get() = config.placeholderId
    val isPlaceHolder: Boolean get() = config.isPlaceHolder
    val interfaceOperations: Array<String?> get() = config.iop
    val groundOperations: Array<String?> get() = config.groundActions
    val params: MutableMap<Int, Any> get() = config.params ?: throw TemplateNotFoundException(id, ::params)
}