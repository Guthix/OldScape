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
package io.guthix.oldscape.cache.script

public data class AssemblyScript(
    val id: Int,
    val instructions: Array<InstructionDefinition>,
    val localIntCount: Int,
    val localStringCount: Int,
    val intArgumentCount: Int,
    val stringArgumentCount: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AssemblyScript) return false

        if (!instructions.contentEquals(other.instructions)) return false
        if (localIntCount != other.localIntCount) return false
        if (localStringCount != other.localStringCount) return false
        if (intArgumentCount != other.intArgumentCount) return false
        if (stringArgumentCount != other.stringArgumentCount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = instructions.contentHashCode()
        result = 31 * result + localIntCount
        result = 31 * result + localStringCount
        result = 31 * result + intArgumentCount
        result = 31 * result + stringArgumentCount
        return result
    }

    override fun toString(): String {
        val strBuilder = StringBuilder()
        strBuilder.append("""
            // id: $id
            // localIntCount: $localIntCount
            // localStringCount: $localStringCount
            // intArgumentCount: $intArgumentCount
            // stringArgumentCount: $stringArgumentCount
        """.trimIndent())
        strBuilder.append("\n")
        val labels = mutableSetOf<Int>()
        instructions.forEach { instruction ->
            if(instruction is IntInstruction && instruction.isJump) {
                labels.add(instruction.operand)
            }
            if(instruction is SwitchInstruction) {
                instruction.operand.forEach { (_, jumpAddress) ->
                    labels.add(jumpAddress)
                }
            }
        }
        instructions.forEachIndexed { curLine, instruction ->
            if(labels.contains(curLine)) strBuilder.append("LABEL$curLine:\n")
            strBuilder.append(String.format("    %-22s", instruction.name))
            when (instruction) {
                is IntInstruction -> strBuilder.append("${instruction.operand}\n")
                is StringInstruction -> strBuilder.append("${instruction.operand}\n")
                is SwitchInstruction -> {
                    strBuilder.append("${instruction.operand.size}\n")
                    instruction.operand.forEach { (key, jumpTo) ->
                        strBuilder.append("        $key:$jumpTo\n")
                    }
                }
            }
        }
        return "$strBuilder"
    }

    public companion object {
        public fun disassemble(script: MachineScript): AssemblyScript {
            val instructions = Array(script.instructions.size) { curLine ->
                val machineInstr = script.instructions[curLine]
                if(machineInstr is IntInstruction && machineInstr.isJump) {
                    IntInstruction(
                        machineInstr.opcode,
                        machineInstr.name,
                        curLine + machineInstr.operand + 1
                    )
                }
                if(machineInstr is SwitchInstruction) {
                    val updatedMap = mutableMapOf<Int, Int>()
                    machineInstr.operand.forEach { value, jumpDelta ->
                        updatedMap[value] = curLine + jumpDelta + 1
                    }
                    SwitchInstruction(
                        machineInstr.opcode,
                        machineInstr.name,
                        updatedMap.toMap()
                    )
                }
                machineInstr
            }
            return AssemblyScript(script.id, instructions, script.localIntCount, script.localStringCount,
                script.intArgumentCount, script.stringArgumentCount
            )

        }
    }
}