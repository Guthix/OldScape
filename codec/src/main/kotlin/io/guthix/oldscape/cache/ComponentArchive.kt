/*
 * Copyright (C) 2019 Guthix
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.guthix.oldscape.cache

import io.guthix.cache.js5.Js5Archive
import io.guthix.oldscape.cache.plane.RsComponent

class ComponentArchive(val components: List<RsComponent>) {
    companion object {
        const val id = 3

        @ExperimentalUnsignedTypes
        fun load(archive: Js5Archive): ComponentArchive {
            val components = mutableListOf<RsComponent>()
            archive.groupSettings.forEach { (groupId, _) ->
                val group = archive.readGroup(groupId)
                group.files.forEach { (fileId, file) ->
                    val widgetId = (groupId shl 16) + fileId
                    components.add(RsComponent.decode(widgetId, file.data))
                }
            }
            return ComponentArchive(components)
        }
    }
}