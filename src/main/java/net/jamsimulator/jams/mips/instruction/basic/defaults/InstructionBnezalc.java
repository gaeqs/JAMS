/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
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

package net.jamsimulator.jams.mips.instruction.basic.defaults;

import net.jamsimulator.jams.mips.architecture.MultiALUPipelinedArchitecture;
import net.jamsimulator.jams.mips.architecture.MultiCycleArchitecture;
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.alu.ALUType;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledI16Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.ControlTransferInstruction;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution;
import net.jamsimulator.jams.mips.parameter.InstructionParameterTypes;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.utils.StringUtils;

public class InstructionBnezalc extends BasicInstruction<InstructionBnezalc.Assembled> implements ControlTransferInstruction {

    public static final String MNEMONIC = "bnezalc";
    public static final ALUType ALU_TYPE = ALUType.INTEGER;
    public static final int OPERATION_CODE = 0b011000;

    public static final InstructionParameterTypes PARAMETER_TYPES = new InstructionParameterTypes(
            ParameterType.REGISTER,
            ParameterType.SIGNED_16_BIT
    );

    public InstructionBnezalc() {
        super(MNEMONIC, PARAMETER_TYPES, ALU_TYPE, OPERATION_CODE);
        addExecutionBuilder(SingleCycleArchitecture.INSTANCE, SingleCycle::new);
        addExecutionBuilder(MultiCycleArchitecture.INSTANCE, MultiCycle::new);
        addExecutionBuilder(MultiALUPipelinedArchitecture.INSTANCE, Pipelined::new);
    }

    @Override
    public AssembledInstruction assembleBasic(ParameterParseResult[] parameters, Instruction origin) {
        return new Assembled(parameters[0].getRegister(), parameters[1].getImmediate(), origin, this);
    }

    @Override
    public AssembledInstruction assembleFromCode(int instructionCode) {
        return new Assembled(instructionCode, this, this);
    }

    @Override
    public boolean match(int instructionCode) {
        int rs = instructionCode >> AssembledI16Instruction.SOURCE_REGISTER_SHIFT & AssembledI16Instruction.SOURCE_REGISTER_MASK;
        int rt = instructionCode >> AssembledI16Instruction.TARGET_REGISTER_SHIFT & AssembledI16Instruction.TARGET_REGISTER_MASK;
        return super.match(instructionCode) && rs == 0 && rt != 0;
    }

    @Override
    public boolean isCompact() {
        return true;
    }

    public static class Assembled extends AssembledI16Instruction {

        public Assembled(int targetRegister, int offset, Instruction origin, BasicInstruction<Assembled> basicOrigin) {
            super(OPERATION_CODE, 0, targetRegister, offset, origin, basicOrigin);
        }

        public Assembled(int instructionCode, Instruction origin, BasicInstruction<Assembled> basicOrigin) {
            super(instructionCode, origin, basicOrigin);
        }

        @Override
        public String parametersToString(String registersStart) {
            return registersStart + getTargetRegister()
                    + ", 0x" + StringUtils.addZeros(Integer.toHexString(getImmediate()), 4);
        }
    }

    public static class SingleCycle extends SingleCycleExecution<Assembled> {

        public SingleCycle(MIPSSimulation<SingleCycleArchitecture> simulation, Assembled instruction, int address) {
            super(simulation, instruction, address);
        }

        @Override
        public void execute() {
            if (value(instruction.getTargetRegister()) == 0) return;
            var pc = pc();
            register(31).setValue(pc.getValue());
            pc.setValue(pc.getValue() + (instruction.getImmediateAsSigned() << 2));
        }
    }

    public static class MultiCycle extends MultiCycleExecution<MultiCycleArchitecture, Assembled> {
        private boolean jumped;

        public MultiCycle(MIPSSimulation<? extends MultiCycleArchitecture> simulation, Assembled instruction, int address) {
            super(simulation, instruction, address, false, true);
        }

        @Override
        public void decode() {
            requires(instruction.getTargetRegister(), false);
            lock(pc());
            lock(31);
        }

        @Override
        public void execute() {
            jumped = value(instruction.getTargetRegister()) != 0;
            if (jumped) {
                jump(getAddress() + 4 + (instruction.getImmediateAsSigned() << 2));
            } else {
                unlock(pc());
            }
        }

        @Override
        public void memory() {
        }

        @Override
        public void writeBack() {
            if (jumped) {
                setAndUnlock(31, getAddress() + 4);
            } else {
                unlock(31);
            }
        }
    }

    public static class Pipelined extends MultiCycleExecution<MultiALUPipelinedArchitecture, Assembled> {

        private boolean jumped;

        public Pipelined(MIPSSimulation<? extends MultiALUPipelinedArchitecture> simulation, Assembled instruction, int address) {
            super(simulation, instruction, address, true, true);
        }

        @Override
        public void decode() {
            requires(instruction.getTargetRegister(), false);
            lock(pc());
            lock(31);

            if (solveBranchOnDecode()) {
                jumped = value(instruction.getTargetRegister()) != 0;
                if (jumped) {
                    jump(getAddress() + 4 + (instruction.getImmediateAsSigned() << 2));
                } else {
                    unlock(pc());
                }
            }
        }

        @Override
        public void execute() {
            if (solveBranchOnDecode() && jumped) {
                forward(31, getAddress() + 4);
            }
        }

        @Override
        public void memory() {
            if (!solveBranchOnDecode()) {
                jumped = value(instruction.getTargetRegister()) != 0;
                if (jumped) {
                    jump(getAddress() + 4 + (instruction.getImmediateAsSigned() << 2));
                    forward(31, getAddress() + 4);
                } else {
                    unlock(pc());
                }
            } else if(jumped) {
                forward(31, getAddress() + 4);
            }
        }

        @Override
        public void writeBack() {
            if (jumped) {
                setAndUnlock(31, getAddress() + 4);
            } else {
                unlock(31);
            }
        }
    }
}
