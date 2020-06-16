/**
 * This file is part of Guthix OldScape-Server.
 *
 * Guthix OldScape-Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape-Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
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