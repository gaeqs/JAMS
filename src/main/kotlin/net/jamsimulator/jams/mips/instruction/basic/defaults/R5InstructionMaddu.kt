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
import net.jamsimulator.jams.utils.NumericUtils

class R5InstructionMaddu : BasicRInstruction<R5InstructionMaddu.Assembled>(
    MNEMONIC, PARAMETER_TYPES, ALU_TYPE, OPERATION_CODE, FUNCTION_CODE
) {

    companion object {
        const val MNEMONIC = "maddu"
        val ALU_TYPE = ALUType.INTEGER
        const val OPERATION_CODE = 0b01100
        const val FUNCTION_CODE = 0b00001

        val PARAMETER_TYPES = InstructionParameterTypes(
            ParameterType.REGISTER, ParameterType.REGISTER
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
    ) = Assembled(parameters[0].register, parameters[1].register, origin, this)

    class Assembled : AssembledRInstruction {
        constructor(value: Int, origin: Instruction?, basicOrigin: BasicInstruction<*>?) : super(
            value,
            origin,
            basicOrigin
        )

        constructor(
            sourceRegister: Int,
            targetRegister: Int,
            origin: Instruction?,
            basicOrigin: BasicInstruction<*>?
        ) : super(
            OPERATION_CODE,
            sourceRegister,
            targetRegister,
            0,
            0,
            FUNCTION_CODE,
            origin,
            basicOrigin
        )

        override fun parametersToString(registersStart: String): String {
            return "$registersStart$sourceRegister, $registersStart$targetRegister"
        }

    }

    class SingleCycle(
        simulation: MIPSSimulation<SingleCycleArchitecture>, instruction: Assembled, address: Int
    ) : SingleCycleExecution<Assembled>(simulation, instruction, address) {

        override fun execute() {
            val lo = register(MIPS32Registers.LO)
            val hi = register(MIPS32Registers.HI)

            val hilo = NumericUtils.intsToLong(lo.value, hi.value)

            val rs = register(instruction.sourceRegister).value.toLong()
            val rt = register(instruction.targetRegister).value.toLong()
            val result = hilo + rs * rt
            NumericUtils.longToInts(result, lo, hi)
        }

    }

    class MultiCycle(
        simulation: MIPSSimulation<out MultiCycleArchitecture>, instruction: Assembled, address: Int
    ) : MultiCycleExecution<MultiCycleArchitecture, Assembled>(
        simulation, instruction, address, false, true
    ) {
        private var result = IntArray(2)

        override fun decode() {
            requires(MIPS32Registers.LO, false)
            requires(MIPS32Registers.HI, false)
            requires(instruction.sourceRegister, false)
            requires(instruction.targetRegister, false)
            lock(MIPS32Registers.LO)
            lock(MIPS32Registers.HI)
        }

        override fun execute() {
            val lo = register(MIPS32Registers.LO)
            val hi = register(MIPS32Registers.HI)

            val hilo = NumericUtils.intsToLong(lo.value, hi.value)

            val rs = register(instruction.sourceRegister).value.toLong()
            val rt = register(instruction.targetRegister).value.toLong()
            val long = hilo + rs * rt
            NumericUtils.longToInts(long, result)
            forward(MIPS32Registers.LO, result[0])
            forward(MIPS32Registers.HI, result[1])
        }

        override fun memory() {
        }

        override fun writeBack() {
            setAndUnlock(MIPS32Registers.LO, result[0])
            setAndUnlock(MIPS32Registers.HI, result[1])
        }
    }

}