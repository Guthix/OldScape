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

data class ClientSettings(
    var resizable: Boolean,
    var lowMemory: Boolean,
    var width: Int,
    var height: Int
)

data class MachineSettings(
    val operatingSystem: OperatingSystem,
    val is64Bit: Boolean,
    val osVersion: String,
    val javaVendor: JavaVendor,
    val javaVersionMajor: Int,
    val javaVersionMinor: Int,
    val javaVersionPatch: Int,
    val maxMemory: Int,
    val availableProcessors: Int
) {
    enum class OperatingSystem(val opcode: Int) {
        WINDOWS(1),
        OSX(2),
        LINUX(3),
        OTHER(4);

        companion object {
            fun get(opcode: Int): OperatingSystem = values().first { it.opcode == opcode }
        }
    }

    enum class JavaVendor(val opcode: Int) {
        SUN(1),
        MICROSOFT(2),
        APPLE(3),
        OTHER(4),
        ORACLE(5);

        companion object {
            fun get(opcode: Int): JavaVendor = values().first { it.opcode == opcode }
        }
    }
}