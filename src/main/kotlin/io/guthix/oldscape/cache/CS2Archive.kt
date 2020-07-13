/*
 * This file is part of Guthix OldScape-Cache.
 *
 * Guthix OldScape-Cache is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape-Cache is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Guthix OldScape-Cache. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.cache

import io.guthix.cache.js5.Js5Archive
import io.guthix.oldscape.cache.script.MachineScript

public class CS2Archive(public val scripts: Map<Int, MachineScript>) {
    public companion object  {
        public const val id: Int = 12

        public fun load(archive: Js5Archive): CS2Archive {
            val scripts = mutableMapOf<Int, MachineScript>()
            archive.groupSettings.forEach { (groupId, _) ->
                val group = archive.readGroup(groupId)
                scripts[groupId] = MachineScript.decode(groupId, group.files[0]!!.data)
            }
            return CS2Archive(scripts)
        }
    }
}