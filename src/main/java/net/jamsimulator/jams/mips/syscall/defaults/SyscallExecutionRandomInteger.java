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

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class SyscallExecutionRandomInteger implements SyscallExecution {

    public static final String NAME = "RANDOM_INTEGER";
    private final int generatorRegister, valueRegister;

    private final Set<Integer> requiredRegisters, lockedRegisters;

    public SyscallExecutionRandomInteger(int generatorRegister, int valueRegister) {
        this.generatorRegister = generatorRegister;
        this.valueRegister = valueRegister;

        requiredRegisters = Set.of(generatorRegister);
        lockedRegisters = Set.of(valueRegister);
    }

    @Override
    public void execute(MIPSSimulation<?> simulation) {
        Register genRegister = simulation.getRegisters().getRegister(this.generatorRegister).orElse(null);
        if (genRegister == null) throw new IllegalStateException("Register " + this.generatorRegister + " not found");
        Register valueRegister = simulation.getRegisters().getRegister(this.valueRegister).orElse(null);
        if (valueRegister == null) throw new IllegalStateException("Register " + this.valueRegister + " not found");

        valueRegister.setValue(simulation.getNumberGenerators().getGenerator(genRegister.getValue()).nextInt());
    }

    @Override
    public Map<Integer, Integer> executeMultiCycle(MultiCycleExecution<?, ?> execution) {
        var simulation = execution.getSimulation();
        var index = execution.value(generatorRegister);

        return Map.of(valueRegister, simulation.getNumberGenerators().getGenerator(index).nextInt());
    }

    @Override
    public Set<Integer> getRequiredRegisters() {
        return requiredRegisters;
    }

    @Override
    public Set<Integer> getLockedRegisters() {
        return lockedRegisters;
    }

    public static class Builder extends SyscallExecutionBuilder<SyscallExecutionRandomInteger> {

        private final IntegerProperty generatorRegister, valueRegister;

        public Builder(ResourceProvider provider) {
            super(provider, NAME, new LinkedList<>());
            properties.add(generatorRegister = new SimpleIntegerProperty(null, "GENERATOR_REGISTER", 4));
            properties.add(valueRegister = new SimpleIntegerProperty(null, "VALUE_REGISTER", 4));
        }

        @Override
        public SyscallExecutionRandomInteger build() {
            return new SyscallExecutionRandomInteger(generatorRegister.get(), valueRegister.get());
        }

        @Override
        public SyscallExecutionBuilder<SyscallExecutionRandomInteger> makeNewInstance() {
            return new Builder(provider);
        }

        @Override
        public SyscallExecutionBuilder<SyscallExecutionRandomInteger> copy() {
            var builder = new Builder(provider);
            builder.generatorRegister.setValue(generatorRegister.getValue());
            builder.valueRegister.setValue(valueRegister.getValue());
            return builder;
        }
    }
}
