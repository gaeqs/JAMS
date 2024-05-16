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
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRIInstruction
import net.jamsimulator.jams.mips.instruction.basic.BasicRIInstruction
import net.jamsimulator.jams.mips.instruction.basic.ControlTransferInstruction
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution
import net.jamsimulator.jams.mips.parameter.InstructionParameterTypes
import net.jamsimulator.jams.mips.parameter.ParameterType
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult
import net.jamsimulator.jams.mips.simulation.MIPSSimulation

class R5InstructionBltzal : BasicRIInstruction<AssembledRIInstruction>(
    MNEMONIC, PARAMETER_TYPES, ALU_TYPE, OPERATION_CODE, FUNCTION_CODE
), ControlTransferInstruction {

    companion object {
        const val MNEMONIC = "bltzal"
        val ALU_TYPE = ALUType.INTEGER
        const val OPERATION_CODE = 0b000001
        const val FUNCTION_CODE = 0b10000

        val PARAMETER_TYPES = InstructionParameterTypes(
            ParameterType.REGISTER, ParameterType.SIGNED_16_BIT
        )
    }

    init {
        addExecutionBuilder(SingleCycleArchitecture.INSTANCE) { s, i, a -> SingleCycle(s, i, a) }
        addExecutionBuilder(MultiCycleArchitecture.INSTANCE) { s, i, a -> MultiCycle(s, i, a) }
        addExecutionBuilder(MultiALUPipelinedArchitecture.INSTANCE) { s, i, a -> Pipelined(s, i, a) }
    }

    override fun assembleFromCode(instructionCode: Int) = AssembledRIInstruction(instructionCode, this, this)

    override fun assembleBasic(
        parameters: Array<ParameterParseResult>, origin: Instruction
    ) = AssembledRIInstruction(
        OPERATION_CODE, parameters[0].register, FUNCTION_CODE, parameters[1].immediate, origin, this
    )

    override fun isCompact() = false

    class SingleCycle(
        simulation: MIPSSimulation<SingleCycleArchitecture>, instruction: AssembledRIInstruction, address: Int
    ) : SingleCycleExecution<AssembledRIInstruction>(simulation, instruction, address) {

        override fun execute() {
            if (value(instruction.sourceRegister) >= 0) return
            val pc = pc()
            register(31).value = pc.value
            pc.value += (instruction.immediateAsSigned shl 2)
        }

    }

    class MultiCycle(
        simulation: MIPSSimulation<out MultiCycleArchitecture>, instruction: AssembledRIInstruction, address: Int
    ) : MultiCycleExecution<MultiCycleArchitecture, AssembledRIInstruction>(
        simulation, instruction, address, false, true
    ) {
        private var jumped = false

        override fun decode() {
            requires(instruction.sourceRegister, false)
            lock(pc())
            lock(31)
        }

        override fun execute() {
            jumped = value(instruction.sourceRegister) < 0
            if (jumped) {
                jump(getAddress() + 4 + (instruction.immediateAsSigned shl 2))
            } else {
                unlock(pc())
            }
        }

        override fun memory() {}
        override fun writeBack() {
            if (jumped) {
                setAndUnlock(31, getAddress() + 4)
            } else {
                unlock(31)
            }
        }
    }

    class Pipelined(
        simulation: MIPSSimulation<out MultiALUPipelinedArchitecture>, instruction: AssembledRIInstruction, address: Int
    ) : MultiCycleExecution<MultiALUPipelinedArchitecture, AssembledRIInstruction>(
        simulation, instruction, address, true, true
    ) {

        private var jumped = false

        override fun decode() {
            requires(instruction.sourceRegister, false)
            lock(pc())
            lock(31)

            if (solveBranchOnDecode()) {
                jumped = value(instruction.sourceRegister) < 0
                if (jumped) {
                    jump(getAddress() + 4 + (instruction.immediateAsSigned shl 2))
                } else {
                    unlock(pc())
                }
            }
        }

        override fun execute() {
            if (solveBranchOnDecode() && jumped) {
                forward(31, getAddress() + 4)
            }
        }

        override fun memory() {
            if (!solveBranchOnDecode()) {
                jumped = value(instruction.sourceRegister) < 0
                if (jumped) {
                    jump(getAddress() + 4 + (instruction.immediateAsSigned shl 2))
                    forward(31, getAddress() + 4)
                } else {
                    unlock(pc())
                }
            } else if (jumped) {
                forward(31, getAddress() + 4)
            }
        }

        override fun writeBack() {
            if (jumped) {
                setAndUnlock(31, getAddress() + 4)
            } else {
                unlock(31)
            }
        }
    }

}