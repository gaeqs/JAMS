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
import net.jamsimulator.jams.mips.instruction.basic.MemoryInstruction;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution;
import net.jamsimulator.jams.mips.parameter.InstructionParameterTypes;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.utils.StringUtils;

public class InstructionLdc1 extends BasicInstruction<InstructionLdc1.Assembled> implements MemoryInstruction {

    public static final String MNEMONIC = "ldc1";
    public static final ALUType ALU_TYPE = ALUType.INTEGER;
    public static final int OPERATION_CODE = 0b110101;

    public static final InstructionParameterTypes PARAMETER_TYPES = new InstructionParameterTypes(
            ParameterType.EVEN_FLOAT_REGISTER,
            ParameterType.SIGNED_16_BIT_REGISTER_SHIFT
    );

    public InstructionLdc1() {
        super(MNEMONIC, PARAMETER_TYPES, ALU_TYPE, OPERATION_CODE);
        addExecutionBuilder(SingleCycleArchitecture.INSTANCE, SingleCycle::new);
        addExecutionBuilder(MultiCycleArchitecture.INSTANCE, MultiCycle::new);
        addExecutionBuilder(MultiALUPipelinedArchitecture.INSTANCE, MultiCycle::new);
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

    @Override
    public boolean isWriteInstruction() {
        return false;
    }

    public static class Assembled extends AssembledI16Instruction {

        public Assembled(int baseRegister, int targetRegister, int offset,
                         Instruction origin, BasicInstruction<Assembled> basicOrigin) {
            super(OPERATION_CODE, baseRegister, targetRegister, offset, origin, basicOrigin);
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
            int t = instruction.getTargetRegister();
            checkEvenRegister(t);

            int address = value(instruction.getSourceRegister()) + instruction.getImmediateAsSigned();
            int low = simulation.getMemory().getWord(address);
            int high = simulation.getMemory().getWord(address + 4);
            registerCOP1(t).setValue(low);
            registerCOP1(t + 1).setValue(high);
        }
    }

    public static class MultiCycle extends MultiCycleExecution<MultiCycleArchitecture, Assembled> {

        private int resultLow, resultHigh;

        public MultiCycle(MIPSSimulation<? extends MultiCycleArchitecture> simulation, Assembled instruction, int address) {
            super(simulation, instruction, address, true, true);
        }

        @Override
        public void decode() {
            int t = instruction.getTargetRegister();
            checkEvenRegister(t);
            requires(instruction.getSourceRegister(), false);
            lockCOP1Double(t);
        }

        @Override
        public void execute() {
        }

        @Override
        public void memory() {
            int address = value(instruction.getSourceRegister()) + instruction.getImmediateAsSigned();
            resultLow = simulation.getMemory().getWord(address);
            resultHigh = simulation.getMemory().getWord(address + 4);
            forwardCOP1(instruction.getTargetRegister(), resultLow);
            forwardCOP1(instruction.getTargetRegister() + 1, resultHigh);
        }

        @Override
        public void writeBack() {
            setAndUnlockCOP1(instruction.getTargetRegister(), resultLow);
            setAndUnlockCOP1(instruction.getTargetRegister() + 1, resultHigh);
        }
    }
}
