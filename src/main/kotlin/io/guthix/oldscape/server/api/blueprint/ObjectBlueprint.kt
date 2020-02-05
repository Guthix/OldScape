/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.api.blueprint

import io.guthix.cache.js5.Js5Archive
import io.guthix.oldscape.cache.config.ObjectConfig
import mu.KotlinLogging
import java.io.IOException

private val logger = KotlinLogging.logger {  }

object ObjectBlueprints {
    private lateinit var blueprints: Map<Int, ObjectBlueprint>

    operator fun get(index: Int): ObjectBlueprint {
        return blueprints[index] ?: throw IOException("Could not find blueprint $index.")
    }

    fun load(archive: Js5Archive) {
        val locConfigs = ObjectConfig.load(archive.readGroup(ObjectConfig.id))
        val tempLocs = mutableMapOf<Int, ObjectBlueprint>()
        locConfigs.forEach { (id, config) ->
            tempLocs[id] = ObjectBlueprint.create(config)
        }
        blueprints = tempLocs.toMap()
        logger.info { "Loaded ${blueprints.size} object blueprints" }
    }
}

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