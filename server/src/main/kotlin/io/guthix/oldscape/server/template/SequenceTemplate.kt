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
package io.guthix.oldscape.server.template

import io.guthix.oldscape.cache.config.SequenceConfig
import kotlin.math.ceil

data class SequenceTemplate(private val config: SequenceConfig) : BaseTemplate(config) {
    val id: Int get() = config.id
    val tickDuration: Int? by lazy(
        ceil(config.frameDuration?.filter { it < 100 }?.sum()?.toDouble()?.div(30) ?: 0.toDouble())::toInt
    )
}