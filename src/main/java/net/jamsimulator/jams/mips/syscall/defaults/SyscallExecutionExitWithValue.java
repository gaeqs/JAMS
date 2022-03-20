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

import javafx.beans.property.SimpleIntegerProperty;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.syscall.SyscallExecution;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class SyscallExecutionExitWithValue implements SyscallExecution {

    public static final String NAME = "EXIT_WITH_VALUE";

    private final int register;

    private final Set<Integer> requiredRegisters;

    public SyscallExecutionExitWithValue(int register) {
        this.register = register;
        requiredRegisters = Set.of(register);
    }

    @Override
    public void execute(MIPSSimulation<?> simulation) {
        Register register = simulation.getRegisters().getRegister(this.register).orElse(null);
        if (register == null) throw new IllegalStateException("Register " + this.register + " not found");

        simulation.requestExit(register.getValue(), 0);
        if (simulation.getLog() != null) {
            simulation.getLog().println();
            simulation.getLog().printDoneLn("Execution finished with code " + register.getValue());
            simulation.getLog().println();
        }
    }

    @Override
    public Map<Integer, Integer> executeMultiCycle(MultiCycleExecution<?, ?> execution) {
        var value = execution.value(register);
        var simulation = execution.getSimulation();
        simulation.requestExit(value, execution.getInstructionId());
        if (simulation.getLog() != null) {
            simulation.getLog().println();
            simulation.getLog().printDoneLn("Execution finished with code " + value);
            simulation.getLog().println();
        }
        return Collections.emptyMap();
    }


    @Override
    public Set<Integer> getRequiredRegisters() {
        return requiredRegisters;
    }

    @Override
    public Set<Integer> getLockedRegisters() {
        return Collections.emptySet();
    }

    public static class Builder extends SyscallExecutionBuilder<SyscallExecutionExitWithValue> {

        private final SimpleIntegerProperty register;

        public Builder(ResourceProvider provider) {
            super(provider, NAME, new LinkedList<>());
            properties.add(register = new SimpleIntegerProperty(null, "REGISTER", 4));
        }

        @Override
        public SyscallExecutionExitWithValue build() {
            return new SyscallExecutionExitWithValue(register.get());
        }

        @Override
        public SyscallExecutionBuilder<SyscallExecutionExitWithValue> makeNewInstance() {
            return new Builder(provider);
        }

        @Override
        public SyscallExecutionBuilder<SyscallExecutionExitWithValue> copy() {
            var builder = new Builder(provider);
            builder.register.setValue(register.getValue());
            return builder;
        }
    }
}
