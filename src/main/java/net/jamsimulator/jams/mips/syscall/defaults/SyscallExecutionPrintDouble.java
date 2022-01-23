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

package net.jamsimulator.jams.mips.syscall.defaults;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.syscall.SyscallExecution;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;
import net.jamsimulator.jams.utils.NumericUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;

public class SyscallExecutionPrintDouble implements SyscallExecution {

    public static final String NAME = "PRINT_DOUBLE";

    private final boolean printHex, lineJump;
    private final int register;

    private final Set<Integer> requiredRegisters;

    public SyscallExecutionPrintDouble(boolean printHex, boolean lineJump, int register) {
        this.printHex = printHex;
        this.lineJump = lineJump;
        this.register = register;
        requiredRegisters = Set.of(register);
    }

    @Override
    public void execute(MIPSSimulation<?> simulation) {
        if (register % 2 != 0) {
            throw new IllegalStateException("Register " + register + " has not an even identifier!");
        }

        Register register1 = simulation.getRegisters().getCoprocessor1Register(register).orElse(null);
        if (register1 == null)
            throw new IllegalStateException("Floating point register " + register + " not found");
        Register register2 = simulation.getRegisters().getCoprocessor1Register(register + 1).orElse(null);
        if (register2 == null)
            throw new IllegalStateException("Floating point register " + (register + 1) + " not found");


        String toPrint;
        if (printHex) {
            long value = (((long) register2.getValue()) << 32) + register1.getValue();
            toPrint = Long.toHexString(value);
        } else {
            double value = NumericUtils.intsToDouble(register1.getValue(), register2.getValue());
            toPrint = String.valueOf(value);
        }

        simulation.getConsole().print(toPrint);
        if (lineJump) simulation.getConsole().println();
    }

    @Override
    public void executeMultiCycle(MultiCycleExecution<?, ?> execution) {
        if (register % 2 != 0) {
            throw new IllegalStateException("Register " + register + " has not an even identifier!");
        }

        var value1 = execution.valueCOP1(register);
        var value2 = execution.valueCOP1(register + 1);

        String toPrint;
        if (printHex) {
            long value = (((long) value2) << 32) + value1;
            toPrint = Long.toHexString(value);
        } else {
            double value = NumericUtils.intsToDouble(value1, value2);
            toPrint = String.valueOf(value);
        }

        var console = execution.getSimulation().getConsole();

        console.print(toPrint);
        if (lineJump) console.println();
    }

    @Override
    public Set<Integer> getRequiredRegisters() {
        return requiredRegisters;
    }

    @Override
    public Set<Integer> getLockedRegisters() {
        return Collections.emptySet();
    }

    public static class Builder extends SyscallExecutionBuilder<SyscallExecutionPrintDouble> {

        private final BooleanProperty hexProperty;
        private final BooleanProperty lineJump;
        private final IntegerProperty register;

        public Builder(ResourceProvider provider) {
            super(provider, NAME, new LinkedList<>());
            properties.add(hexProperty = new SimpleBooleanProperty(null, "PRINT_HEX", false));
            properties.add(lineJump = new SimpleBooleanProperty(null, "LINE_JUMP", false));
            properties.add(register = new SimpleIntegerProperty(null, "REGISTER", 0));
        }

        @Override
        public SyscallExecutionPrintDouble build() {
            return new SyscallExecutionPrintDouble(hexProperty.get(), lineJump.get(), register.get());
        }

        @Override
        public SyscallExecutionBuilder<SyscallExecutionPrintDouble> makeNewInstance() {
            return new Builder(provider);
        }

        @Override
        public SyscallExecutionBuilder<SyscallExecutionPrintDouble> copy() {
            var builder = new Builder(provider);
            builder.hexProperty.setValue(hexProperty.getValue());
            builder.lineJump.setValue(lineJump.getValue());
            builder.register.setValue(register.getValue());
            return builder;
        }
    }
}
