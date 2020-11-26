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

import io.guthix.oldscape.server.PropertyHolder
import io.guthix.oldscape.server.task.Task
import io.guthix.oldscape.server.task.TaskHolder
import io.guthix.oldscape.server.task.TaskType
import io.guthix.oldscape.server.world.map.Tile
import io.guthix.oldscape.server.world.map.dim.TileUnit
import kotlin.reflect.KProperty

interface Entity : TaskHolder, PropertyHolder {
    val pos: Tile

    val sizeX: TileUnit

    val sizeY: TileUnit

    var orientation: Int

    override val tasks: MutableMap<TaskType, MutableSet<Task>>

    override val properties: MutableMap<KProperty<*>, Any?>
}