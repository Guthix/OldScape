/*
 * Copyright 2018-2020 Guthix
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
package io.guthix.oldscape.server.blueprints

import io.guthix.oldscape.cache.config.ObjectConfig

open class ExtraObjectConfig(
    open val ids: List<Int>,
    open val weight: Float,
    open val examine: String
)

open class ObjectBlueprint(
    private val cacheConfig: ObjectConfig,
    protected open val extraConfig: ExtraObjectConfig
) {
    val id: Int get() = cacheConfig.id
    val name: String get() = cacheConfig.name
    val weight: Float get() = extraConfig.weight
    val examines: String get() = extraConfig.examine
    val isStackable: Boolean get() = cacheConfig.stackable
    val isTradable: Boolean get() = cacheConfig.tradable
    val notedId: Int? get() = cacheConfig.notedId
    val isNoted: Boolean get() = cacheConfig.isNoted
    val placeHolderId: Int? get() = cacheConfig.placeholderId
    val isPlaceHolder: Boolean get() = cacheConfig.isPlaceHolder
    val interfaceOperations: Array<String?> get() = cacheConfig.iop
    val groundOperations: Array<String?> get() = cacheConfig.groundActions
}