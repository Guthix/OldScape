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
package io.guthix.oldscape.cache

import io.guthix.js5.Js5Archive
import io.guthix.oldscape.cache.plane.SpriteSet

public class SpriteArchive(public val spriteSets: List<SpriteSet>) {
    public companion object {
        public const val ID: Int = 8

        public fun load(archive: Js5Archive): SpriteArchive {
            val sprites = mutableListOf<SpriteSet>()
            archive.groupSettings.forEach { (groupId, _) ->
                val group = archive.readGroup(groupId)
                group.files.forEach { (_, file) ->
                    sprites.add(SpriteSet.decode(groupId, file.data))
                }
            }
            return SpriteArchive(sprites)
        }
    }
}