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