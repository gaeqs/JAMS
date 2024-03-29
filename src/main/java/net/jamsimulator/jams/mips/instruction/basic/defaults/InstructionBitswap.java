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
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicRInstruction;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution;
import net.jamsimulator.jams.mips.parameter.InstructionParameterTypes;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.utils.NumericUtils;

public class InstructionBitswap extends BasicRInstruction<InstructionBitswap.Assembled> {

    public static final String MNEMONIC = "bitswap";
    public static final ALUType ALU_TYPE = ALUType.INTEGER;
    public static final int OPERATION_CODE = 0b011111;
    public static final int FUNCTION_CODE = 0b100000;
    public static final int SPECIAL_CODE = 0b00000;

    public static final InstructionParameterTypes PARAMETER_TYPES = new InstructionParameterTypes(
            ParameterType.REGISTER,
            ParameterType.REGISTER
    );

    public InstructionBitswap() {
        super(MNEMONIC, PARAMETER_TYPES, ALU_TYPE, OPERATION_CODE, FUNCTION_CODE);
        addExecutionBuilder(SingleCycleArchitecture.INSTANCE, SingleCycle::new);
        addExecutionBuilder(MultiCycleArchitecture.INSTANCE, MultiCycle::new);
        addExecutionBuilder(MultiALUPipelinedArchitecture.INSTANCE, MultiCycle::new);
    }


    @Override
    public boolean match(int instructionCode) {
        return super.match(instructionCode) &&
                ((instructionCode >> Assembled.SHIFT_AMOUNT_SHIFT) & Assembled.SHIFT_AMOUNT_MASK) == SPECIAL_CODE;
    }

    @Override
    public AssembledInstruction assembleBasic(ParameterParseResult[] parameters, Instruction origin) {
        return new Assembled(parameters[1].getRegister(), parameters[0].getRegister(), origin, this);
    }

    @Override
    public AssembledInstruction assembleFromCode(int instructionCode) {
        return new Assembled(instructionCode, this, this);
    }

    public static class Assembled extends AssembledRInstruction {

        public Assembled(int targetRegister, int destinationRegister,
                         Instruction origin, BasicInstruction<Assembled> basicOrigin) {
            super(
                    OPERATION_CODE,
                    0,
                    targetRegister,
                    destinationRegister,
                    SPECIAL_CODE,
                    FUNCTION_CODE,
                    origin,
                    basicOrigin
            );
        }

        public Assembled(int instructionCode, Instruction origin, BasicInstruction<Assembled> basicOrigin) {
            super(instructionCode, origin, basicOrigin);
        }


        @Override
        public int getShiftAmount() {
            return super.getShiftAmount() & SHIFT_AMOUNT_MASK;
        }

        @Override
        public String parametersToString(String registersStart) {
            return registersStart + getDestinationRegister()
                    + ", " + registersStart + getDestinationRegister()
                    + ", " + registersStart + getTargetRegister();
        }
    }

    public static class SingleCycle extends SingleCycleExecution<Assembled> {

        public SingleCycle(MIPSSimulation<SingleCycleArchitecture> simulation, Assembled instruction, int address) {
            super(simulation, instruction, address);
        }

        @Override
        public void execute() {
            register(instruction.getDestinationRegister())
                    .setValue(NumericUtils.swapBits(value(instruction.getTargetRegister())));
        }
    }

    public static class MultiCycle extends MultiCycleExecution<MultiCycleArchitecture, Assembled> {

        private int result;

        public MultiCycle(MIPSSimulation<? extends MultiCycleArchitecture> simulation, Assembled instruction, int address) {
            super(simulation, instruction, address, false, true);
        }

        @Override
        public void decode() {
            requires(instruction.getTargetRegister(), false);
            lock(instruction.getDestinationRegister());
        }

        @Override
        public void execute() {
            result = NumericUtils.swapBits(value(instruction.getTargetRegister()));
            forward(instruction.getTargetRegister(), result);
        }

        @Override
        public void memory() {
        }

        @Override
        public void writeBack() {
            setAndUnlock(instruction.getDestinationRegister(), result);
        }
    }
}
