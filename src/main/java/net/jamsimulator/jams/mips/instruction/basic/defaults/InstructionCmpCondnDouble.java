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
import net.jamsimulator.jams.mips.architecture.MultiCycleArchitecture;
import net.jamsimulator.jams.mips.architecture.PipelinedArchitecture;
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRFPUInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicRFPUInstruction;
import net.jamsimulator.jams.mips.instruction.data.APUType;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution;
import net.jamsimulator.jams.mips.interrupt.InterruptCause;
import net.jamsimulator.jams.mips.interrupt.MIPSInterruptException;
import net.jamsimulator.jams.mips.parameter.InstructionParameterTypes;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.utils.NumericUtils;
import net.jamsimulator.jams.utils.StringUtils;

public class InstructionCmpCondnDouble extends BasicRFPUInstruction<InstructionCmpCondnDouble.Assembled> {

    public static final String NAME_SUFIX = "CMP_D";
    public static final String MNEMONIC = "cmp.%s.d";
    public static final APUType APU_TYPE = APUType.FLOAT_ADDTION;
    public static final int OPERATION_CODE = 0b010001;
    public static final int FMT = 0b10101;

    public static final InstructionParameterTypes PARAMETER_TYPES = new InstructionParameterTypes(ParameterType.EVEN_FLOAT_REGISTER, ParameterType.EVEN_FLOAT_REGISTER, ParameterType.EVEN_FLOAT_REGISTER);

    private final FloatCondition condition;

    public InstructionCmpCondnDouble(FloatCondition condition) {
        super(String.format(MNEMONIC, condition.getMnemonic()), PARAMETER_TYPES, APU_TYPE, OPERATION_CODE, condition.getCode(), FMT);
        this.condition = condition;
        addExecutionBuilder(SingleCycleArchitecture.INSTANCE, SingleCycle::new);
        addExecutionBuilder(MultiCycleArchitecture.INSTANCE, MultiCycle::new);
        addExecutionBuilder(MultiCycleArchitecture.INSTANCE, MultiCycle::new);
        addExecutionBuilder(PipelinedArchitecture.INSTANCE, MultiCycle::new);
    }

    @Override
    public String getName() {
        var name = Manager.ofS(Language.class).getSelected().getOrDefault("INSTRUCTION_" + NAME_SUFIX);
        return name.replace("{TYPE}", condition.getName());
    }

    @Override
    public String getDocumentation() {
        var documentation = StringUtils.parseEscapeCharacters(Manager.ofS(Language.class).getSelected().getOrDefault("INSTRUCTION_" + NAME_SUFIX + "_DOCUMENTATION"));
        return documentation.replace("{TYPE}", condition.getName())
                .replace("{MNEMONIC}", condition.getMnemonic())
                .replace("{CODE}", StringUtils.addZeros(Integer.toBinaryString(condition.getCode()), 5));
    }

    @Override
    public AssembledInstruction assembleBasic(ParameterParseResult[] parameters, Instruction origin) {
        return new Assembled(parameters[2].getRegister(), parameters[1].getRegister(), parameters[0].getRegister(), getFunctionCode(), origin, this);
    }

    @Override
    public AssembledInstruction assembleFromCode(int instructionCode) {
        return new Assembled(instructionCode, this, this);
    }

    public static class Assembled extends AssembledRFPUInstruction {

        public Assembled(int targetRegister, int sourceRegister, int destinationRegister, int function_code,
                         Instruction origin, BasicInstruction<InstructionCmpCondnDouble.Assembled> basicOrigin) {
            super(OPERATION_CODE, FMT, targetRegister, sourceRegister, destinationRegister, function_code, origin, basicOrigin);
        }

        public Assembled(int instructionCode, Instruction origin, BasicInstruction<InstructionCmpCondnDouble.Assembled> basicOrigin) {
            super(instructionCode, origin, basicOrigin);
        }

        public boolean cond0() {
            return (getFunctionCode() & 0b1) > 0;
        }

        public boolean cond1() {
            return (getFunctionCode() & 0b10) > 0;
        }

        public boolean cond2() {
            return (getFunctionCode() & 0b100) > 0;
        }

        public boolean cond3() {
            return (getFunctionCode() & 0b1000) > 0;
        }

        public boolean cond4() {
            return (getFunctionCode() & 0b10000) > 0;
        }


        @Override
        public String parametersToString(String registersStart) {
            return registersStart + getDestinationRegister()
                    + ", " + registersStart + getSourceRegister()
                    + ", " + registersStart + getTargetRegister();
        }
    }

    public static class SingleCycle extends SingleCycleExecution<Assembled> {

        public SingleCycle(MIPSSimulation<SingleCycleArchitecture> simulation, Assembled instruction, int address) {
            super(simulation, instruction, address);
        }

        @Override
        public void execute() {
            if (instruction.getTargetRegister() % 2 != 0) evenFloatRegisterException();
            if (instruction.getSourceRegister() % 2 != 0) evenFloatRegisterException();
            if (instruction.getDestinationRegister() % 2 != 0) evenFloatRegisterException();

            Register rt0 = registerCop1(instruction.getTargetRegister());
            Register rt1 = registerCop1(instruction.getTargetRegister() + 1);
            Register rs0 = registerCop1(instruction.getSourceRegister());
            Register rs1 = registerCop1(instruction.getSourceRegister() + 1);
            Register rd0 = registerCop1(instruction.getDestinationRegister());
            Register rd1 = registerCop1(instruction.getDestinationRegister() + 1);

            double ft = NumericUtils.intsToDouble(rt0.getValue(), rt1.getValue());
            double fs = NumericUtils.intsToDouble(rs0.getValue(), rs1.getValue());

            boolean less, equal, unordered;

            if (Double.isNaN(fs) || Double.isNaN(ft)) {
                less = false;
                equal = false;
                unordered = true;
                if (instruction.cond3()) {
                    throw new MIPSInterruptException(InterruptCause.FLOATING_POINT_EXCEPTION);
                }
            } else {
                less = fs < ft;
                equal = fs == ft;
                unordered = false;
            }

            boolean condition = instruction.cond4() ^ ((instruction.cond2() && less) || (instruction.cond1() && equal) || (instruction.cond0() && unordered));
            rd0.setValue(condition ? 0xFFFFFFFF : 0);
            rd1.setValue(condition ? 0xFFFFFFFF : 0);
        }
    }

    public static class MultiCycle extends MultiCycleExecution<Assembled> {

        public MultiCycle(MIPSSimulation<MultiCycleArchitecture> simulation, Assembled instruction, int address) {
            super(simulation, instruction, address, false, true);
        }

        @Override
        public void decode() {
            if (instruction.getTargetRegister() % 2 != 0) evenFloatRegisterException();
            if (instruction.getSourceRegister() % 2 != 0) evenFloatRegisterException();
            if (instruction.getDestinationRegister() % 2 != 0) evenFloatRegisterException();

            requiresCOP1(instruction.getTargetRegister());
            requiresCOP1(instruction.getTargetRegister() + 1);
            requiresCOP1(instruction.getSourceRegister());
            requiresCOP1(instruction.getSourceRegister() + 1);
            lockCOP1(instruction.getDestinationRegister());
            lockCOP1(instruction.getDestinationRegister() + 1);
        }

        @Override
        public void execute() {
            double ft = NumericUtils.intsToDouble(valueCOP1(instruction.getTargetRegister()), valueCOP1(instruction.getTargetRegister() + 1));
            double fs = NumericUtils.intsToDouble(valueCOP1(instruction.getSourceRegister()), valueCOP1(instruction.getSourceRegister() + 1));

            boolean less, equal, unordered;

            if (Double.isNaN(fs) || Double.isNaN(ft)) {
                less = false;
                equal = false;
                unordered = true;
                if (instruction.cond3()) {
                    throw new MIPSInterruptException(InterruptCause.FLOATING_POINT_EXCEPTION);
                }
            } else {
                less = fs < ft;
                equal = fs == ft;
                unordered = false;
            }

            boolean condition = instruction.cond4() ^ ((instruction.cond2() && less) || (instruction.cond1() && equal) || (instruction.cond0() && unordered));
            executionResult = new int[]{condition ? 0xFFFFFFFF : 0, condition ? 0xFFFFFFFF : 0};

            forwardCOP1(instruction.getDestinationRegister(), executionResult[0], false);
            forwardCOP1(instruction.getDestinationRegister() + 1, executionResult[1], false);
        }

        @Override
        public void memory() {
            forwardCOP1(instruction.getDestinationRegister(), executionResult[0], true);
            forwardCOP1(instruction.getDestinationRegister() + 1, executionResult[1], true);
        }

        @Override
        public void writeBack() {
            setAndUnlockCOP1(instruction.getDestinationRegister(), executionResult[0]);
            setAndUnlockCOP1(instruction.getDestinationRegister() + 1, executionResult[1]);
        }
    }
}
