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
import net.jamsimulator.jams.mips.instruction.basic.BasicRFPUInstruction
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution
import net.jamsimulator.jams.mips.interrupt.InterruptCause
import net.jamsimulator.jams.mips.parameter.InstructionParameterTypes
import net.jamsimulator.jams.mips.parameter.ParameterType
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult
import net.jamsimulator.jams.mips.simulation.MIPSSimulation

class R5InstructionCCondD(val condition: R5CCondCondition) : BasicRFPUInstruction<R5InstructionCCondD.Assembled>(
    MNEMONIC.replace(REPLACEMENT, condition.mnemonic),
    PARAMETER_TYPES,
    ALU_TYPE,
    OPERATION_CODE,
    FUNCTION_CODE + condition.ordinal,
    FMT
) {

    companion object {
        const val REPLACEMENT = "{COND}"
        const val MNEMONIC = "c.$REPLACEMENT.d"
        val ALU_TYPE = ALUType.FLOAT_ADDTION
        const val OPERATION_CODE = 0b010001
        const val FMT = 0b10001
        const val FUNCTION_CODE = 0b110000

        val PARAMETER_TYPES = InstructionParameterTypes(
            ParameterType.UNSIGNED_3_BIT, ParameterType.EVEN_FLOAT_REGISTER, ParameterType.EVEN_FLOAT_REGISTER
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
        parameters[2].register, parameters[1].register, parameters[0].immediate, functionCode, origin, this
    )


    class Assembled : AssembledRFPUInstruction {

        constructor(value: Int, origin: Instruction, basicOrigin: BasicInstruction<*>) : super(
            value,
            origin,
            basicOrigin
        )

        constructor(
            targetRegister: Int,
            sourceRegiser: Int,
            conditionalCode: Int,
            condition: Int,
            origin: Instruction,
            basicOrigin: BasicInstruction<*>
        ) : super(
            OPERATION_CODE,
            FMT,
            targetRegister,
            sourceRegiser,
            conditionalCode shl 2,
            condition,
            origin,
            basicOrigin
        )

        val conditionalCode: Int
            get() = destinationRegister ushr 2

        val condition get() = R5CCondCondition.entries[functionCode ushr 2]

        override fun parametersToString(registersStart: String?): String {
            return "$conditionalCode, $registersStart$sourceRegister, $registersStart$targetRegister"
        }

    }

    class SingleCycle(
        simulation: MIPSSimulation<SingleCycleArchitecture>, instruction: Assembled, address: Int
    ) : SingleCycleExecution<Assembled>(simulation, instruction, address) {

        override fun execute() {
            val condition = instruction.condition
            val fs = doubleCOP1(instruction.sourceRegister)
            val ft = doubleCOP1(instruction.targetRegister)

            val less: Boolean
            val equal: Boolean
            val unordered: Boolean

            if (ft.isNaN() || fs.isNaN()) {
                if (condition.signal) {
                    error(InterruptCause.FLOATING_POINT_EXCEPTION)
                }
                less = false
                equal = false
                unordered = true
            } else {
                less = fs < ft
                equal = fs == ft
                unordered = false
            }
            val result = less && condition.less || equal && condition.equal || unordered && condition.unordered
            registerCOP1(32 + instruction.conditionalCode).value = if (result) 1 else 0
        }

    }

    class MultiCycle(
        simulation: MIPSSimulation<out MultiCycleArchitecture>, instruction: Assembled, address: Int
    ) : MultiCycleExecution<MultiCycleArchitecture, Assembled>(
        simulation, instruction, address, false, true
    ) {
        private var result = 0

        override fun decode() {
            requires(instruction.sourceRegister, false)
            requires(instruction.targetRegister, false)
            lock(32 + instruction.conditionalCode)
        }

        override fun execute() {
            val condition = instruction.condition
            val fs = doubleCOP1(instruction.sourceRegister)
            val ft = doubleCOP1(instruction.targetRegister)

            val less: Boolean
            val equal: Boolean
            val unordered: Boolean

            if (ft.isNaN() || fs.isNaN()) {
                if (condition.signal) {
                    error(InterruptCause.FLOATING_POINT_EXCEPTION)
                }
                less = false
                equal = false
                unordered = true
            } else {
                less = fs < ft
                equal = fs == ft
                unordered = false
            }
            val bool = less && condition.less || equal && condition.equal || unordered && condition.unordered
            result = if (bool) 1 else 0
            forwardCOP1(32 + instruction.conditionalCode, result)
        }

        override fun memory() = forwardCOP1(32 + instruction.conditionalCode, result)
        override fun writeBack() = setAndUnlockCOP1(32 + instruction.conditionalCode, result)
    }

}