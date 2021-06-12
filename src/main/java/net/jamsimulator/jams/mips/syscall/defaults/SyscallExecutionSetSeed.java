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
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.syscall.SyscallExecution;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

import java.util.LinkedList;

public class SyscallExecutionSetSeed implements SyscallExecution {

    public static final String NAME = "SET_SEED";
    private final int generatorRegister, seedRegister;

    public SyscallExecutionSetSeed(int generatorRegister, int seedRegister) {
        this.generatorRegister = generatorRegister;
        this.seedRegister = seedRegister;
    }

    @Override
    public void execute(MIPSSimulation<?> simulation) {
        Register genRegister = simulation.getRegisters().getRegister(this.generatorRegister).orElse(null);
        if (genRegister == null) throw new IllegalStateException("Register " + this.generatorRegister + " not found");
        Register seedRegister = simulation.getRegisters().getRegister(this.seedRegister).orElse(null);
        if (seedRegister == null) throw new IllegalStateException("Register " + this.seedRegister + " not found");

        var generator = simulation.getNumberGenerators().getGenerator(genRegister.getValue());
        generator.setSeed(seedRegister.getValue());
    }

    @Override
    public void executeMultiCycle(MultiCycleExecution<?> execution) {
        int index = execution.value(generatorRegister);
        int seed = execution.value(seedRegister);

        execution.getSimulation().getNumberGenerators().getGenerator(index).setSeed(seed);
    }

    public static class Builder extends SyscallExecutionBuilder<SyscallExecutionSetSeed> {

        private final IntegerProperty generatorRegister, seedRegister;

        public Builder() {
            super(NAME, new LinkedList<>());
            properties.add(generatorRegister = new SimpleIntegerProperty(null, "GENERATOR_REGISTER", 4));
            properties.add(seedRegister = new SimpleIntegerProperty(null, "SEED_REGISTER", 5));
        }

        @Override
        public SyscallExecutionSetSeed build() {
            return new SyscallExecutionSetSeed(generatorRegister.get(), seedRegister.get());
        }

        @Override
        public SyscallExecutionBuilder<SyscallExecutionSetSeed> makeNewInstance() {
            return new Builder();
        }

        @Override
        public SyscallExecutionBuilder<SyscallExecutionSetSeed> copy() {
            var builder = new Builder();
            builder.generatorRegister.setValue(generatorRegister.getValue());
            builder.seedRegister.setValue(seedRegister.getValue());
            return builder;
        }
    }
}
