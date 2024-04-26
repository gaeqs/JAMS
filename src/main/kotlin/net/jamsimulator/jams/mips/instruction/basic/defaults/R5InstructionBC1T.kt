/*
 *  MIT License
 *
 *  Copyright (c) 2024 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.mips.instruction.basic.defaults

import net.jamsimulator.jams.mips.architecture.MultiALUPipelinedArchitecture
import net.jamsimulator.jams.mips.architecture.MultiCycleArchitecture
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture
import net.jamsimulator.jams.mips.instruction.Instruction
import net.jamsimulator.jams.mips.instruction.alu.ALUType
import net.jamsimulator.jams.mips.instruction.assembled.AssembledI16Instruction
import net.jamsimulator.jams.mips.instruction.basic.BasicIFPUInstruction
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction
import net.jamsimulator.jams.mips.instruction.basic.ControlTransferInstruction
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution
import net.jamsimulator.jams.mips.parameter.InstructionParameterTypes
import net.jamsimulator.jams.mips.parameter.ParameterType
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult
import net.jamsimulator.jams.mips.simulation.MIPSSimulation
import net.jamsimulator.jams.utils.StringUtils

class R5InstructionBC1T : BasicIFPUInstruction<R5InstructionBC1T.Assembled>(
    MNEMONIC, PARAMETER_TYPES, ALU_TYPE, OPERATION_CODE, SUBCODE
), ControlTransferInstruction {

    companion object {
        const val MNEMONIC = "bc1t"
        val ALU_TYPE = ALUType.INTEGER
        const val OPERATION_CODE = 0b010001
        const val SUBCODE = 0b01000

        val PARAMETER_TYPES = InstructionParameterTypes(
            ParameterType.UNSIGNED_3_BIT, ParameterType.SIGNED_16_BIT
        )
    }

    init {
        addExecutionBuilder(SingleCycleArchitecture.INSTANCE) { s, i, a -> SingleCycle(s, i, a) }
        addExecutionBuilder(MultiCycleArchitecture.INSTANCE) { s, i, a -> MultiCycle(s, i, a) }
        addExecutionBuilder(MultiALUPipelinedArchitecture.INSTANCE) { s, i, a -> Pipelined(s, i, a) }
    }

    override fun match(instructionCode: Int): Boolean {
        return super.match(instructionCode) && ((instructionCode shr 16) and 0b11) == 1
    }

    override fun assembleFromCode(instructionCode: Int) = Assembled(instructionCode, this, this)

    override fun assembleBasic(
        parameters: Array<ParameterParseResult>, origin: Instruction
    ) = Assembled(
        parameters[0].immediate, parameters[1].immediate, origin, this
    )

    override fun isCompact() = false

    class Assembled : AssembledI16Instruction {

        constructor(value: Int, origin: Instruction, basicOrigin: BasicInstruction<*>) : super(
            value,
            origin,
            basicOrigin
        )

        constructor(
            conditionalCode: Int, immediate: Int, origin: Instruction, basicOrigin: BasicInstruction<*>
        ) : super(
            OPERATION_CODE, SUBCODE, conditionalCode shl 2, immediate, origin, basicOrigin
        )

        val conditionalCode: Int
            get() = targetRegister ushr 2

        override fun parametersToString(registersStart: String?): String {
            return "$conditionalCode, ${StringUtils.addZeros(Integer.toHexString(immediate), 4)}"
        }

    }


    class SingleCycle(
        simulation: MIPSSimulation<SingleCycleArchitecture>, instruction: Assembled, address: Int
    ) : SingleCycleExecution<Assembled>(simulation, instruction, address) {

        override fun execute() {
            val value = valueCOP1(32 + instruction.conditionalCode) and 0b1
            if (value == 1) {
                pc().value += (instruction.immediateAsSigned shl 2)
            }
        }

    }

    class MultiCycle(
        simulation: MIPSSimulation<out MultiCycleArchitecture>, instruction: Assembled, address: Int
    ) : MultiCycleExecution<MultiCycleArchitecture, Assembled>(
        simulation, instruction, address, false, true
    ) {

        override fun decode() {
            requiresCOP1(32 + instruction.conditionalCode, false)
            lock(pc())
        }

        override fun execute() {
            val value = valueCOP1(32 + instruction.conditionalCode) and 0b1
            if (value == 0) {
                jump(getAddress() + 4 + (instruction.immediateAsSigned shl 2))
            } else {
                unlock(pc())
            }
        }

        override fun memory() {}
        override fun writeBack() {}
    }

    class Pipelined(
        simulation: MIPSSimulation<out MultiALUPipelinedArchitecture>, instruction: Assembled, address: Int
    ) : MultiCycleExecution<MultiALUPipelinedArchitecture, Assembled>(
        simulation, instruction, address, true, false
    ) {

        override fun decode() {
            requiresCOP1(32 + instruction.conditionalCode, false)
            lock(pc())

            if (solveBranchOnDecode()) {
                solve()
            }
        }

        override fun execute() {}

        override fun memory() {
            if (!solveBranchOnDecode()) {
                solve()
            }
        }

        override fun writeBack() {}


        private fun solve() {
            val value = valueCOP1(32 + instruction.conditionalCode) and 0b1
            if (value == 1) {
                jump(getAddress() + 4 + (instruction.immediateAsSigned shl 2))
            } else {
                unlock(pc())
            }
        }
    }
}