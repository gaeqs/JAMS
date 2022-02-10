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
import net.jamsimulator.jams.mips.instruction.basic.BasicIFPUInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution;
import net.jamsimulator.jams.mips.parameter.InstructionParameterTypes;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.register.COP0Register;
import net.jamsimulator.jams.mips.register.COP0RegistersBits;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;

public class InstructionEi extends BasicIFPUInstruction<InstructionEi.Assembled> {

    public static final String MNEMONIC = "ei";
    public static final ALUType ALU_TYPE = ALUType.INTEGER;
    public static final int OPERATION_CODE = 0b010000;
    public static final int SUBCODE = 0b01011;

    public static final InstructionParameterTypes PARAMETER_TYPES = new InstructionParameterTypes(
            ParameterType.REGISTER
    );

    public static final int IMMEDIATE_VALUE = 0b01100_00000_100000;

    public InstructionEi() {
        super(MNEMONIC, PARAMETER_TYPES, ALU_TYPE, OPERATION_CODE, SUBCODE);
        addExecutionBuilder(SingleCycleArchitecture.INSTANCE, SingleCycle::new);
        addExecutionBuilder(MultiCycleArchitecture.INSTANCE, MultiCycle::new);
        addExecutionBuilder(MultiALUPipelinedArchitecture.INSTANCE, MultiCycle::new);
    }

    @Override
    public AssembledInstruction assembleBasic(ParameterParseResult[] parameters, Instruction origin) {
        return new Assembled(
                origin,
                this,
                parameters[0].getRegister()
        );
    }

    @Override
    public boolean match(int instructionCode) {
        int immediate = instructionCode & AssembledI16Instruction.IMMEDIATE_MASK;
        return super.match(instructionCode) && immediate == IMMEDIATE_VALUE;
    }

    @Override
    public AssembledInstruction assembleFromCode(int instructionCode) {
        return new Assembled(instructionCode, this, this);
    }

    public static class Assembled extends AssembledI16Instruction {

        public Assembled(Instruction origin, BasicInstruction<Assembled> basicOrigin, int targetRegister) {
            super(
                    OPERATION_CODE,
                    SUBCODE,
                    targetRegister,
                    IMMEDIATE_VALUE,
                    origin,
                    basicOrigin
            );
        }

        public Assembled(int instructionCode, Instruction origin, BasicInstruction<Assembled> basicOrigin) {
            super(instructionCode, origin, basicOrigin);
        }

        @Override
        public String parametersToString(String registersStart) {
            return registersStart + getTargetRegister();
        }
    }

    public static class SingleCycle extends SingleCycleExecution<Assembled> {

        public SingleCycle(MIPSSimulation<SingleCycleArchitecture> simulation, Assembled instruction, int address) {
            super(simulation, instruction, address);
        }

        @Override
        public void execute() {
            var status = (COP0Register) registerCOP0(12);
            register(instruction.getTargetRegister()).setValue(status.getValue());
            status.modifyBits(1, COP0RegistersBits.STATUS_IE, 1);
        }
    }

    public static class MultiCycle extends MultiCycleExecution<MultiCycleArchitecture, Assembled> {

        private int result;
        private int status;

        public MultiCycle(MIPSSimulation<? extends MultiCycleArchitecture> simulation, Assembled instruction, int address) {
            super(simulation, instruction, address, false, true);
        }

        @Override
        public void decode() {
            requiresCOP0(12, false);
            lockCOP0(12);
            lock(instruction.getTargetRegister());
        }

        @Override
        public void execute() {
            result = valueCOP0(12);
            status = result | 1;
            forward(instruction.getTargetRegister(), result);
            forwardCOP0(12, status);
        }

        @Override
        public void memory() {
        }

        @Override
        public void writeBack() {
            setAndUnlock(instruction.getTargetRegister(), result);
            setAndUnlockCOP0(12, status);
        }
    }
}
