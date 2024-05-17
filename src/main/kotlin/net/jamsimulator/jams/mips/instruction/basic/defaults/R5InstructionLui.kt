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
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution
import net.jamsimulator.jams.mips.parameter.InstructionParameterTypes
import net.jamsimulator.jams.mips.parameter.ParameterType
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult
import net.jamsimulator.jams.mips.simulation.MIPSSimulation

class R5InstructionLui : BasicInstruction<AssembledI16Instruction>(
    MNEMONIC, PARAMETER_TYPES, ALU_TYPE, OPERATION_CODE
) {

    companion object {
        const val MNEMONIC = "lui"
        const val OPERATION_CODE = 0b001111

        @JvmField
        val ALU_TYPE = ALUType.INTEGER

        @JvmField
        val PARAMETER_TYPES = InstructionParameterTypes(
            ParameterType.REGISTER, ParameterType.SIGNED_16_BIT
        )
    }

    init {
        addExecutionBuilder(SingleCycleArchitecture.INSTANCE) { s, i, a -> SingleCycle(s, i, a) }
        addExecutionBuilder(MultiCycleArchitecture.INSTANCE) { s, i, a -> MultiCycle(s, i, a) }
        addExecutionBuilder<MultiCycleArchitecture>(MultiALUPipelinedArchitecture.INSTANCE) { s, i, a ->
            MultiCycle(s, i, a)
        }
    }

    override fun assembleFromCode(instructionCode: Int) = AssembledI16Instruction(instructionCode, this, this)

    override fun assembleBasic(
        parameters: Array<ParameterParseResult>, origin: Instruction
    ) = AssembledI16Instruction(
        OPERATION_CODE, 0, parameters[0].register, parameters[1].immediate, origin, this
    )

    class SingleCycle(
        simulation: MIPSSimulation<SingleCycleArchitecture>, instruction: AssembledI16Instruction, address: Int
    ) : SingleCycleExecution<AssembledI16Instruction>(simulation, instruction, address) {

        override fun execute() {
            register(instruction.targetRegister).value = instruction.immediate shl 16
        }

    }

    class MultiCycle(
        simulation: MIPSSimulation<out MultiCycleArchitecture>, instruction: AssembledI16Instruction, address: Int
    ) : MultiCycleExecution<MultiCycleArchitecture, AssembledI16Instruction>(
        simulation, instruction, address, false, true
    ) {
        private var result = 0

        override fun decode() {
            lock(instruction.targetRegister)
        }

        override fun execute() {
            result = instruction.immediateAsSigned shl 16
            forward(instruction.targetRegister, result)
        }

        override fun memory() = forward(instruction.targetRegister, result)
        override fun writeBack() = setAndUnlock(instruction.targetRegister, result)
    }

}