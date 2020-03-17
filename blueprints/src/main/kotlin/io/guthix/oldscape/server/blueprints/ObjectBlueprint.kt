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

class ObjectBlueprint private constructor(
    val id: Int,
    val name: String,
    val stackable: Boolean,
    val tradable: Boolean,
    val notedId: Int?,
    val iop: Array<String?>,
    val groundActions: Array<String?>
) {
    companion object {
        fun create(config: ObjectConfig): ObjectBlueprint {
            return ObjectBlueprint(
                config.id,
                config.name,
                config.stackable,
                config.tradable,
                config.notedId,
                config.iop,
                config.groundActions
            )
        }
    }
}