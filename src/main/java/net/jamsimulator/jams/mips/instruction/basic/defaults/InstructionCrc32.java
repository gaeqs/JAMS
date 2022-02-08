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

import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.manager.Manager;
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
import net.jamsimulator.jams.mips.interrupt.InterruptCause;
import net.jamsimulator.jams.mips.parameter.InstructionParameterTypes;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.utils.NumericUtils;

public class InstructionCrc32 extends BasicRInstruction<InstructionCrc32.Assembled> {

    public static final String MNEMONIC = "crc32";
    public static final ALUType ALU_TYPE = ALUType.INTEGER;
    public static final int OPERATION_CODE = 0b011111;
    public static final int FUNCTION_CODE = 0b001111;

    public static final int CRC_C_TYPE_MASK = 0x7;
    public static final int CRC_C_TYPE_SHIFT = 8;
    public static final int CRC_TYPE_MASK = 0x3;
    public static final int CRC_TYPE_SHIFT = 6;

    public static int POLY = 0xEDB88320;
    public static int C_POLY = 0x82F63B78;

    public static final InstructionParameterTypes PARAMETER_TYPES = new InstructionParameterTypes(
            ParameterType.REGISTER,
            ParameterType.REGISTER
    );


    private final boolean cType;

    private final CRCType type;

    public InstructionCrc32(CRCType type, boolean cType) {
        super(MNEMONIC + (cType ? "c" : "") + type.getSuffix(),
                PARAMETER_TYPES, ALU_TYPE, OPERATION_CODE, FUNCTION_CODE);
        this.cType = cType;
        this.type = type;
        addExecutionBuilder(SingleCycleArchitecture.INSTANCE, SingleCycle::new);
        addExecutionBuilder(MultiCycleArchitecture.INSTANCE, MultiCycle::new);
        addExecutionBuilder(MultiALUPipelinedArchitecture.INSTANCE, MultiCycle::new);
    }

    @Override
    public String getDocumentation() {
        var sufix = (MNEMONIC + (cType ? "c" : "")).toUpperCase().replace('.', '_');
        return Manager.ofS(Language.class).getSelected().getOrDefault("INSTRUCTION_" + sufix + "_DOCUMENTATION");
    }

    @Override
    public AssembledInstruction assembleBasic(ParameterParseResult[] parameters, Instruction origin) {
        return new Assembled(parameters[1].getRegister(), parameters[0].getRegister(),
                cType, type.getId(), origin, this);
    }

    @Override
    public AssembledInstruction assembleFromCode(int instructionCode) {
        return new Assembled(instructionCode, this, this);
    }

    @Override
    public boolean match(int instructionCode) {
        boolean cType = (instructionCode >> CRC_C_TYPE_SHIFT & CRC_C_TYPE_MASK) == 1;
        int type = instructionCode >> CRC_TYPE_SHIFT & CRC_TYPE_MASK;
        return this.cType == cType && this.type.getId() == type;
    }

    public static class Assembled extends AssembledRInstruction {

        public Assembled(int sourceRegister, int targetRegister, boolean cType, int sz,
                         Instruction origin, BasicInstruction<Assembled> basicOrigin) {
            super(
                    OPERATION_CODE,
                    sourceRegister,
                    targetRegister,
                    0,
                    (cType ? 0b00100 : 0) + sz,
                    FUNCTION_CODE,
                    origin,
                    basicOrigin
            );
        }

        public Assembled(int instructionCode, Instruction origin, BasicInstruction<Assembled> basicOrigin) {
            super(instructionCode, origin, basicOrigin);
        }


        public boolean getCRCCType() {
            return (getCode() >> CRC_C_TYPE_SHIFT & CRC_C_TYPE_MASK) == 1;
        }

        public int getCRCType() {
            return getCode() >> CRC_TYPE_SHIFT & CRC_TYPE_MASK;
        }

        @Override
        public String parametersToString(String registersStart) {
            return registersStart + getTargetRegister() + ", " + registersStart + getSourceRegister();
        }
    }

    public static class SingleCycle extends SingleCycleExecution<Assembled> {

        public SingleCycle(MIPSSimulation<SingleCycleArchitecture> simulation, Assembled instruction, int address) {
            super(simulation, instruction, address);
        }

        @Override
        public void execute() {
            var rt = register(instruction.getTargetRegister());
            var rs = value(instruction.getSourceRegister());
            int rz = instruction.getCRCType();
            if (rz == 4) {
                error(InterruptCause.RESERVED_INSTRUCTION_EXCEPTION);
                return;
            }

            int poly = instruction.getCRCCType() ? C_POLY : POLY;

            int value = switch (rz) {
                case 0 -> NumericUtils.crc32(rt.getValue(), rs, 1, poly);
                case 1 -> NumericUtils.crc32(rt.getValue(), rs, 2, poly);
                default -> NumericUtils.crc32(rt.getValue(), rs, 4, poly);
            };
            rt.setValue(value);
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
            requires(instruction.getSourceRegister(), false);
            lock(instruction.getTargetRegister());
        }

        @Override
        public void execute() {
            int rz = instruction.getCRCType();
            if (rz == 4) {
                error(InterruptCause.RESERVED_INSTRUCTION_EXCEPTION);
                return;
            }

            int rt = value(instruction.getTargetRegister());
            int rs = value(instruction.getSourceRegister());

            int poly = instruction.getCRCCType() ? C_POLY : POLY;

            result = switch (rz) {
                case 0 -> NumericUtils.crc32(rt, rs, 1, poly);
                case 1 -> NumericUtils.crc32(rt, rs, 2, poly);
                default -> NumericUtils.crc32(rt, rs, 4, poly);
            };

            forward(instruction.getTargetRegister(), result);
        }

        @Override
        public void memory() {
        }

        @Override
        public void writeBack() {
            setAndUnlock(instruction.getTargetRegister(), result);
        }
    }
}
