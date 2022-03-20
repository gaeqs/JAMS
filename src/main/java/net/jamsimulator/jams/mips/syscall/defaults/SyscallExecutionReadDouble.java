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

public class SyscallExecutionReadDouble implements SyscallExecution {

    public static final String NAME = "READ_DOUBLE";
    private final boolean lineJump;
    private final int register;

    private final Set<Integer> lockedRegisters;

    public SyscallExecutionReadDouble(boolean lineJump, int register) {
        this.lineJump = lineJump;
        this.register = register;
        lockedRegisters = Set.of(register);
    }

    @Override
    public void execute(MIPSSimulation<?> simulation) {
        if (this.register % 2 != 0) throw new IllegalStateException("Register " + this.register + " is not even!");
        Register register = simulation.getRegisters().getCoprocessor1Register(this.register).orElse(null);
        Register register2 = simulation.getRegisters().getCoprocessor1Register(this.register + 1).orElse(null);
        if (register == null) throw new IllegalStateException("Register " + this.register + " not found");
        if (register2 == null) throw new IllegalStateException("Register " + (this.register + 1) + " not found");

        boolean done = false;
        while (!done) {
            String value = simulation.popInputOrLock();
            if (simulation.checkThreadInterrupted()) return;

            try {
                double input = Double.parseDouble(value);

                int[] ints = NumericUtils.doubleToInts(input);

                register.setValue(ints[0]);
                register2.setValue(ints[1]);

                simulation.getLog().printDone(value);
                if (lineJump) simulation.getLog().println();
                done = true;
            } catch (NumberFormatException ignore) {
            }
        }
    }

    @Override
    public void executeMultiCycle(MultiCycleExecution<?, ?> execution) {
        var simulation = execution.getSimulation();
        boolean done = false;
        while (!done) {
            String value = simulation.popInputOrLock();
            if (simulation.checkThreadInterrupted()) return;

            try {
                double input = Double.parseDouble(value);

                int[] ints = NumericUtils.doubleToInts(input);

                execution.setAndUnlockCOP1(register, ints[0]);
                execution.setAndUnlockCOP1(register + 1, ints[1]);

                simulation.getLog().printDone(value);
                if (lineJump) simulation.getLog().println();
                done = true;
            } catch (NumberFormatException ignore) {
            }
        }
    }

    @Override
    public Set<Integer> getRequiredRegisters() {
        return Collections.emptySet();
    }

    @Override
    public Set<Integer> getLockedRegisters() {
        return lockedRegisters;
    }

    public static class Builder extends SyscallExecutionBuilder<SyscallExecutionReadDouble> {

        private final BooleanProperty lineJump;
        private final IntegerProperty register;

        public Builder(ResourceProvider provider) {
            super(provider, NAME, new LinkedList<>());
            properties.add(lineJump = new SimpleBooleanProperty(null, "LINE_JUMP", false));
            properties.add(register = new SimpleIntegerProperty(null, "REGISTER", 2));
        }

        @Override
        public SyscallExecutionReadDouble build() {
            return new SyscallExecutionReadDouble(lineJump.get(), register.get());
        }

        @Override
        public SyscallExecutionBuilder<SyscallExecutionReadDouble> makeNewInstance() {
            return new Builder(provider);
        }

        @Override
        public SyscallExecutionBuilder<SyscallExecutionReadDouble> copy() {
            var builder = new Builder(provider);
            builder.lineJump.setValue(lineJump.getValue());
            builder.register.setValue(register.getValue());
            return builder;
        }
    }
}
