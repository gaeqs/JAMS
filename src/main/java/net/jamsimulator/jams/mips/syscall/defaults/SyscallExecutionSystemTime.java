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

import javafx.beans.property.IntegerProperty;
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
import java.util.Map;
import java.util.Set;

public class SyscallExecutionSystemTime implements SyscallExecution {

    public static final String NAME = "SYSTEM_TIME";
    private final int lowOrderRegister, highOrderRegister;

    private final Set<Integer> lockedRegisters;

    public SyscallExecutionSystemTime(int lowOrderRegister, int highOrderRegister) {
        this.lowOrderRegister = lowOrderRegister;
        this.highOrderRegister = highOrderRegister;
        lockedRegisters = Set.of(lowOrderRegister, highOrderRegister);
    }

    @Override
    public void execute(MIPSSimulation<?> simulation) {
        Register low = simulation.getRegisters().getRegister(lowOrderRegister).orElse(null);
        if (low == null) throw new IllegalStateException("Register " + lowOrderRegister + " not found");
        Register high = simulation.getRegisters().getRegister(highOrderRegister).orElse(null);
        if (high == null) throw new IllegalStateException("Register " + highOrderRegister + " not found");

        int[] values = NumericUtils.longToInts(System.currentTimeMillis());
        low.setValue(values[0]);
        high.setValue(values[1]);
    }

    @Override
    public Map<Integer, Integer> executeMultiCycle(MultiCycleExecution<?, ?> execution) {
        int[] values = NumericUtils.longToInts(System.currentTimeMillis());
        return Map.of(lowOrderRegister, values[0], highOrderRegister, values[1]);
    }

    @Override
    public Set<Integer> getRequiredRegisters() {
        return Collections.emptySet();
    }

    @Override
    public Set<Integer> getLockedRegisters() {
        return lockedRegisters;
    }

    public static class Builder extends SyscallExecutionBuilder<SyscallExecutionSystemTime> {

        private final IntegerProperty low, high;

        public Builder(ResourceProvider provider) {
            super(provider, NAME, new LinkedList<>());
            properties.add(low = new SimpleIntegerProperty(null, "LOW_REGISTER", 4));
            properties.add(high = new SimpleIntegerProperty(null, "HIGH_REGISTER", 5));
        }

        @Override
        public SyscallExecutionSystemTime build() {
            return new SyscallExecutionSystemTime(low.get(), high.get());
        }

        @Override
        public SyscallExecutionBuilder<SyscallExecutionSystemTime> makeNewInstance() {
            return new Builder(provider);
        }

        @Override
        public SyscallExecutionBuilder<SyscallExecutionSystemTime> copy() {
            var builder = new Builder(provider);
            builder.low.setValue(low.getValue());
            builder.high.setValue(high.getValue());
            return builder;
        }
    }
}
