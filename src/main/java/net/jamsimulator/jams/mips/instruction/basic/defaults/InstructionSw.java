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

import net.jamsimulator.jams.mips.architecture.MultiAPUPipelinedArchitecture;
import net.jamsimulator.jams.mips.architecture.MultiCycleArchitecture;
import net.jamsimulator.jams.mips.architecture.PipelinedArchitecture;
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.apu.APUType;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledI16Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution;
import net.jamsimulator.jams.mips.parameter.InstructionParameterTypes;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.utils.StringUtils;

public class InstructionSw extends BasicInstruction<InstructionSw.Assembled> {

    public static final String MNEMONIC = "sw";
    public static final APUType APU_TYPE = APUType.INTEGER;
    public static final int OPERATION_CODE = 0b101011;

    public static final InstructionParameterTypes PARAMETER_TYPES = new InstructionParameterTypes(ParameterType.REGISTER, ParameterType.SIGNED_16_BIT_REGISTER_SHIFT);

    public InstructionSw() {
        super(MNEMONIC, PARAMETER_TYPES, APU_TYPE, OPERATION_CODE);
        addExecutionBuilder(SingleCycleArchitecture.INSTANCE, SingleCycle::new);
        addExecutionBuilder(MultiCycleArchitecture.INSTANCE, MultiCycle::new);
        addExecutionBuilder(PipelinedArchitecture.INSTANCE, MultiCycle::new);
        addExecutionBuilder(MultiAPUPipelinedArchitecture.INSTANCE, MultiCycle::new);
    }

    @Override
    public AssembledInstruction assembleBasic(ParameterParseResult[] parameters, Instruction origin) {
        return new Assembled(parameters[1].getRegister(), parameters[0].getRegister(),
                parameters[1].getImmediate(), origin, this);
    }

    @Override
    public AssembledInstruction assembleFromCode(int instructionCode) {
        return new Assembled(instructionCode, this, this);
    }

    public static class Assembled extends AssembledI16Instruction {

        public Assembled(int baseRegister, int targetRegister, int offset, Instruction origin, BasicInstruction<Assembled> basicOrigin) {
            super(InstructionSw.OPERATION_CODE, baseRegister, targetRegister, offset, origin, basicOrigin);
        }

        public Assembled(int instructionCode, Instruction origin, BasicInstruction<Assembled> basicOrigin) {
            super(instructionCode, origin, basicOrigin);
        }

        @Override
        public String parametersToString(String registersStart) {
            return registersStart + getTargetRegister()
                    + ", 0x" + StringUtils.addZeros(Integer.toHexString(getImmediate()), 4)
                    + "(" + registersStart + getSourceRegister() + ")";
        }
    }

    public static class SingleCycle extends SingleCycleExecution<Assembled> {

        public SingleCycle(MIPSSimulation<SingleCycleArchitecture> simulation, Assembled instruction, int address) {
            super(simulation, instruction, address);
        }

        @Override
        public void execute() {
            Register base = register(instruction.getSourceRegister());
            Register rt = register(instruction.getTargetRegister());
            int address = base.getValue() + instruction.getImmediateAsSigned();
            simulation.getMemory().setWord(address, rt.getValue());
        }
    }

    public static class MultiCycle extends MultiCycleExecution<MultiCycleArchitecture, Assembled> {

        public MultiCycle(MIPSSimulation<? extends MultiCycleArchitecture> simulation, Assembled instruction, int address) {
            super(simulation, instruction, address, true, false);
        }

        @Override
        public void decode() {
            requires(instruction.getSourceRegister(), false);
            requires(instruction.getTargetRegister(), true);
        }

        @Override
        public void execute() {
            int address = value(instruction.getSourceRegister()) + instruction.getImmediateAsSigned();
            executionResult = new int[]{address};
        }

        @Override
        public void memory() {
            simulation.getMemory().setWord(executionResult[0], value(instruction.getTargetRegister()));
        }

        @Override
        public void writeBack() {
        }

    }
}
