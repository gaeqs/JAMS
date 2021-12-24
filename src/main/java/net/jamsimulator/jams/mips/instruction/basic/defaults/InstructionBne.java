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

import net.jamsimulator.jams.mips.architecture.MultiCycleArchitecture;
import net.jamsimulator.jams.mips.architecture.PipelinedArchitecture;
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledI16Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.ControlTransferInstruction;
import net.jamsimulator.jams.mips.instruction.data.APUType;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution;
import net.jamsimulator.jams.mips.parameter.InstructionParameterTypes;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.utils.StringUtils;

public class InstructionBne extends BasicInstruction<InstructionBne.Assembled> implements ControlTransferInstruction {

    public static final String MNEMONIC = "bne";
    public static final APUType APU_TYPE = APUType.INTEGER;
    public static final int OPERATION_CODE = 0b000101;

    public static final InstructionParameterTypes PARAMETER_TYPES = new InstructionParameterTypes(ParameterType.REGISTER, ParameterType.REGISTER, ParameterType.SIGNED_16_BIT);

    public InstructionBne() {
        super(MNEMONIC, PARAMETER_TYPES, APU_TYPE, OPERATION_CODE);
        addExecutionBuilder(SingleCycleArchitecture.INSTANCE, SingleCycle::new);
        addExecutionBuilder(MultiCycleArchitecture.INSTANCE, MultiCycle::new);
        addExecutionBuilder(PipelinedArchitecture.INSTANCE, Pipelined::new);
    }

    @Override
    public AssembledInstruction assembleBasic(ParameterParseResult[] parameters, Instruction origin) {
        return new Assembled(parameters[0].getRegister(), parameters[1].getRegister(), parameters[2].getImmediate(), origin, this);
    }

    @Override
    public AssembledInstruction assembleFromCode(int instructionCode) {
        return new Assembled(instructionCode, this, this);
    }

    @Override
    public boolean isCompact() {
        return false;
    }

    public static class Assembled extends AssembledI16Instruction {

        public Assembled(int sourceRegister, int targetRegister, int offset, Instruction origin, BasicInstruction<Assembled> basicOrigin) {
            super(InstructionBne.OPERATION_CODE, sourceRegister, targetRegister, offset, origin, basicOrigin);
        }

        public Assembled(int instructionCode, Instruction origin, BasicInstruction<Assembled> basicOrigin) {
            super(instructionCode, origin, basicOrigin);
        }

        @Override
        public String parametersToString(String registersStart) {
            return registersStart + getSourceRegister()
                    + ", " + registersStart + getTargetRegister()
                    + ", 0x" + StringUtils.addZeros(Integer.toHexString(getImmediate()), 4);
        }
    }

    public static class SingleCycle extends SingleCycleExecution<Assembled> {

        public SingleCycle(MIPSSimulation<SingleCycleArchitecture> simulation, Assembled instruction, int address) {
            super(simulation, instruction, address);
        }

        @Override
        public void execute() {
            Register rs = register(instruction.getSourceRegister());
            Register rt = register(instruction.getTargetRegister());
            if (rs.getValue() == rt.getValue()) return;
            Register pc = pc();
            pc.setValue(pc.getValue() + (instruction.getImmediateAsSigned() << 2));

        }
    }

    public static class MultiCycle extends MultiCycleExecution<Assembled> {

        public MultiCycle(MIPSSimulation<MultiCycleArchitecture> simulation, Assembled instruction, int address) {
            super(simulation, instruction, address, false, false);
        }

        @Override
        public void decode() {
        }

        @Override
        public void execute() {
            if (value(instruction.getTargetRegister()) != value(instruction.getSourceRegister())) {
                jump(getAddress() + 4 + (instruction.getImmediateAsSigned() << 2));
            }
        }

        @Override
        public void memory() {
        }

        @Override
        public void writeBack() {
        }
    }

    public static class Pipelined extends MultiCycleExecution<Assembled> {

        public Pipelined(MIPSSimulation<MultiCycleArchitecture> simulation, Assembled instruction, int address) {
            super(simulation, instruction, address, false, !simulation.getData().shouldSolveBranchesOnDecode());
        }

        @Override
        public void decode() {
            requires(instruction.getSourceRegister());
            requires(instruction.getTargetRegister());
            lock(pc());

            if (solveBranchOnDecode()) {
                if (value(instruction.getTargetRegister()) != value(instruction.getSourceRegister())) {
                    jump(getAddress() + 4 + (instruction.getImmediateAsSigned() << 2));
                } else unlock(pc());
            }
        }

        @Override
        public void execute() {
        }

        @Override
        public void memory() {

        }

        @Override
        public void writeBack() {
            if (!solveBranchOnDecode()) {
                if (value(instruction.getTargetRegister()) != value(instruction.getSourceRegister())) {
                    jump(getAddress() + 4 + (instruction.getImmediateAsSigned() << 2));
                } else unlock(pc());
            }
        }
    }
}
