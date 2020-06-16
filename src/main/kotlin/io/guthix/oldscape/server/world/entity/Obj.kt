/**
 * This file is part of Guthix OldScape-Server.
 *
 * Guthix OldScape-Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape-Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
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