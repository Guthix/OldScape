/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
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
    val ids: List<Int>,
    val weight: Float,
    val examine: String
)

open class ObjectBlueprint(
    private val cacheConfig: ObjectConfig,
    protected open val extraConfig: ExtraObjectConfig
) {
    val id get() = cacheConfig.id
    val name get() = cacheConfig.name
    val weight get() = extraConfig.weight
    val examines get() = extraConfig.examine
    val isStackable get() = cacheConfig.stackable
    val isTradable get() = cacheConfig.tradable
    val notedId get() = cacheConfig.notedId
    val isNoted get() = cacheConfig.isNoted
    val placeHolderId get() = cacheConfig.placeholderId
    val isPlaceHolder get() = cacheConfig.isPlaceHolder
    val interfaceOperations get() = cacheConfig.iop
    val groundOperations get() = cacheConfig.groundActions
}