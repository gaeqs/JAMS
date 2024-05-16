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
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRFPUInstruction
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction
import net.jamsimulator.jams.mips.instruction.basic.BasicRInstruction
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution
import net.jamsimulator.jams.mips.instruction.execution.NumericMultiCycleExecution
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution
import net.jamsimulator.jams.mips.parameter.InstructionParameterTypes
import net.jamsimulator.jams.mips.parameter.ParameterType
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult
import net.jamsimulator.jams.mips.simulation.MIPSSimulation
import net.jamsimulator.jams.utils.NumericUtils

class R5InstructionMaddSingle : BasicRInstruction<R5InstructionMaddSingle.Assembled>(
    MNEMONIC,
    PARAMETER_TYPES,
    ALU_TYPE,
    OPERATION_CODE,
    FUNCTION_CODE + FMT
) {

    companion object {
        const val MNEMONIC = "madd.s"
        val ALU_TYPE = ALUType.FLOAT_MULTIPLICATION
        const val OPERATION_CODE = 0b010011
        const val FMT = 0b000
        const val FUNCTION_CODE = 0b100000

        val PARAMETER_TYPES = InstructionParameterTypes(
            ParameterType.FLOAT_REGISTER,
            ParameterType.FLOAT_REGISTER,
            ParameterType.FLOAT_REGISTER,
            ParameterType.FLOAT_REGISTER
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
    ) = Assembled(
        parameters[1].register,
        parameters[3].register,
        parameters[2].register,
        parameters[0].register,
        origin, this
    )


    class Assembled : AssembledRFPUInstruction {

        constructor(value: Int, origin: Instruction, basicOrigin: BasicInstruction<*>) : super(
            value,
            origin,
            basicOrigin
        )

        constructor(
            extraRegister: Int,
            targetRegister: Int,
            sourceRegister: Int,
            destinationRegister: Int,
            origin: Instruction,
            basicOrigin: BasicInstruction<*>
        ) : super(
            OPERATION_CODE,
            extraRegister,
            targetRegister,
            sourceRegister,
            destinationRegister,
            FUNCTION_CODE + FMT,
            origin,
            basicOrigin
        )

        val extraRegister: Int
            get() = fmt

        override fun parametersToString(r: String?): String {
            return "$r$destinationRegister, $r$extraRegister, $r$sourceRegister, $r$targetRegister"
        }

    }

    class SingleCycle(
        simulation: MIPSSimulation<SingleCycleArchitecture>, instruction: Assembled, address: Int
    ) : SingleCycleExecution<Assembled>(simulation, instruction, address) {

        override fun execute() {
            val fs = floatCOP1(instruction.sourceRegister)
            val ft = floatCOP1(instruction.targetRegister)
            val fr = floatCOP1(instruction.extraRegister)

            val result = fs * ft + fr
            registerCOP1(instruction.destinationRegister).setValue(result)
        }

    }

    class MultiCycle(
        simulation: MIPSSimulation<out MultiCycleArchitecture>, instruction: Assembled, address: Int
    ) : MultiCycleExecution<MultiCycleArchitecture, Assembled>(
        simulation, instruction, address, false, true
    ) {

        var result = 0.0f

        override fun decode() {
            requiresCOP1(instruction.sourceRegister, false)
            requiresCOP1(instruction.targetRegister, false)
            requiresCOP1(instruction.extraRegister, false)
            lockCOP1(instruction.destinationRegister)
        }

        override fun execute() {
            val fs = floatCOP1(instruction.sourceRegister)
            val ft = floatCOP1(instruction.targetRegister)
            val fr = floatCOP1(instruction.extraRegister)

            result = fs * ft + fr
            forwardCOP1(instruction.destinationRegister, result)
        }

        override fun memory() {
        }

        override fun writeBack() {
            setAndUnlockCOP1(instruction.destinationRegister, result)
        }
    }

}