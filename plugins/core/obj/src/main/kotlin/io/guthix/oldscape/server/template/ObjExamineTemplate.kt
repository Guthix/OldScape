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
package io.guthix.oldscape.server.template

import io.guthix.oldscape.server.Property
import io.guthix.oldscape.server.world.entity.Obj

val Obj.examine: String get() = examineTemplate.examine

private val Obj.examineTemplate: ObjExamineTemplate
    get() = template.examine ?: throw TemplateNotFoundException(id, ObjExamineTemplate::class)

internal val ObjTemplate.examine: ObjExamineTemplate? by Property { null }

data class ObjExamineTemplate(override val ids: List<Int>, val examine: String) : Template(ids)