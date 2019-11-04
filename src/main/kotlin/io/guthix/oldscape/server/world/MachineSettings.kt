package io.guthix.oldscape.server.world

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
            fun get(opcode: Int): OperatingSystem = OperatingSystem.values().first { it.opcode == opcode }
        }
    }

    enum class JavaVendor(val opcode: Int) {
        SUN(1),
        MICROSOFT(2),
        APPLE(3),
        OTHER(4),
        ORACLE(5);

        companion object {
            fun get(opcode: Int): JavaVendor = JavaVendor.values().first { it.opcode == opcode }
        }
    }
}