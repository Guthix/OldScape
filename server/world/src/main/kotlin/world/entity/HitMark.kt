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
package io.guthix.oldscape.server.world.entity

class HitMark(val color: Color, val damage: Int, val delay: Int) {
    enum class Color(val id: Int) {
        GREEN(2),
        DARK_YELLOW(3),
        DARK_YELLOW_SPLAT(4),
        DARK_GREEN(5),
        MAGENTA(6),
        BLUE(12),
        BLUE_TINTED(13),
        RED(16),
        RED_TINTED(17),
        LIGHT_GREEN(18),
        ORANGE(20),
        YELLOW(22),
        YELLOW_TINTED(23),
        GREY(24),
        BLACK(25),
    }
}

