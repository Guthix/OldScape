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
package io.guthix.oldscape.server.world.entity

import io.guthix.oldscape.server.api.ObjectBlueprints
import io.guthix.oldscape.server.blueprints.ObjectBlueprint

open class Obj(id: Int, var quantity: Int) {
    protected open val blueprint: ObjectBlueprint = ObjectBlueprints[id]

    val id: Int get() = blueprint.id
    val name: String get() = blueprint.name
    val weight: Float get() = blueprint.weight
    val examines: String get() = blueprint.examines
    val isStackable: Boolean get() = blueprint.isStackable
    val isTradable: Boolean get() = blueprint.isTradable
    val notedId: Int? get() = blueprint.notedId
    val isNoted: Boolean get() = blueprint.isNoted
    val placeHolderId: Int? get() = blueprint.placeHolderId
    val isPlaceHolder: Boolean get() = blueprint.isPlaceHolder
    val interfaceOperations: Array<String?> get() = blueprint.interfaceOperations
    val groundOperations: Array<String?> get() = blueprint.groundOperations
}