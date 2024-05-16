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
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRInstruction
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction
import net.jamsimulator.jams.mips.instruction.basic.BasicRInstruction
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution
import net.jamsimulator.jams.mips.parameter.InstructionParameterTypes
import net.jamsimulator.jams.mips.parameter.ParameterType
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult
import net.jamsimulator.jams.mips.register.MIPS32Registers
import net.jamsimulator.jams.mips.simulation.MIPSSimulation

class R5InstructionMflo : BasicRInstruction<R5InstructionMflo.Assembled>(
    MNEMONIC, PARAMETER_TYPES, ALU_TYPE, OPERATION_CODE, FUNCTION_CODE
) {

    companion object {
        const val MNEMONIC = "mflo"
        val ALU_TYPE = ALUType.INTEGER
        const val OPERATION_CODE = 0b000000
        const val FUNCTION_CODE = 0b010010

        val PARAMETER_TYPES = InstructionParameterTypes(
            ParameterType.REGISTER
        )
    }

    init {
        addExecutionBuilder(SingleCycleArchitecture.INSTANCE) { s, i, a -> SingleCycle(s, i, a) }
        addExecutionBuilder(MultiCycleArchitecture.INSTANCE) { s, i, a -> MultiCycle(s, i, a) }
        addExecutionBuilder<MultiCycleArchitecture>(MultiALUPipelinedArchitecture.INSTANCE) { s, i, a ->
            MultiCycle(s, i, a)
        }
    }

    override fun assembleFromCode(instructionCode: Int) = Assembled(instructionCode, this, this)

    override fun assembleBasic(
        parameters: Array<ParameterParseResult>, origin: Instruction
    ) = Assembled(parameters[0].register, 0, origin, this)

    class Assembled : AssembledRInstruction {
        constructor(value: Int, origin: Instruction?, basicOrigin: BasicInstruction<*>?) : super(
            value,
            origin,
            basicOrigin
        )

        constructor(
            destinationRegister: Int,
            shift: Int,
            origin: Instruction?,
            basicOrigin: BasicInstruction<*>?
        ) : super(
            OPERATION_CODE,
            0,
            0,
            destinationRegister,
            shift,
            FUNCTION_CODE,
            origin,
            basicOrigin
        )

        override fun parametersToString(registersStart: String): String {
            return "$registersStart$destinationRegister"
        }

    }

    class SingleCycle(
        simulation: MIPSSimulation<SingleCycleArchitecture>, instruction: Assembled, address: Int
    ) : SingleCycleExecution<Assembled>(simulation, instruction, address) {

        override fun execute() {
            register(instruction.destinationRegister).value = value(MIPS32Registers.HI)
        }

    }

    class MultiCycle(
        simulation: MIPSSimulation<out MultiCycleArchitecture>, instruction: Assembled, address: Int
    ) : MultiCycleExecution<MultiCycleArchitecture, Assembled>(
        simulation, instruction, address, false, true
    ) {
        private var result = 0

        override fun decode() {
            requires(MIPS32Registers.LO, false)
            lock(instruction.destinationRegister)
        }

        override fun execute() {
            result = value(MIPS32Registers.LO)
            forward(instruction.destinationRegister, result)
        }

        override fun memory() {
        }

        override fun writeBack() {
            setAndUnlock(instruction.destinationRegister, result)
        }
    }

}