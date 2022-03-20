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

public class SyscallExecutionAllocateMemory implements SyscallExecution {

    public static final String NAME = "ALLOCATE_MEMORY";
    private final int amountRegister, addressRegister;

    private final Set<Integer> requiredRegisters, lockedRegisters;

    public SyscallExecutionAllocateMemory(int amountRegister, int addressRegister) {
        this.amountRegister = amountRegister;
        this.addressRegister = addressRegister;
        requiredRegisters = Set.of(amountRegister);
        lockedRegisters = Set.of(addressRegister);
    }

    @Override
    public void execute(MIPSSimulation<?> simulation) {
        Register amountReg = simulation.getRegisters().getRegister(this.amountRegister).orElse(null);
        if (amountReg == null) throw new IllegalStateException("Register " + this.amountRegister + " not found");

        Register addressReg = simulation.getRegisters().getRegister(this.addressRegister).orElse(null);
        if (addressReg == null) throw new IllegalStateException("Register " + this.addressRegister + " not found");

        int address = simulation.getMemory().allocateMemory(amountReg.getValue());
        addressReg.setValue(address);
    }

    @Override
    public Map<Integer, Integer> executeMultiCycle(MultiCycleExecution<?, ?> execution) {
        var amount = execution.value(amountRegister);
        var address = execution.getSimulation().getMemory().allocateMemory(amount);
        return Map.of(addressRegister, address);
    }

    @Override
    public Set<Integer> getRequiredRegisters() {
        return requiredRegisters;
    }

    @Override
    public Set<Integer> getLockedRegisters() {
        return lockedRegisters;
    }

    public static class Builder extends SyscallExecutionBuilder<SyscallExecutionAllocateMemory> {

        private final IntegerProperty amountRegister;
        private final IntegerProperty addressRegister;

        public Builder(ResourceProvider provider) {
            super(provider, NAME, new LinkedList<>());
            properties.add(amountRegister = new SimpleIntegerProperty(null, "AMOUNT_REGISTER", 4));
            properties.add(addressRegister = new SimpleIntegerProperty(null, "ADDRESS_REGISTER", 2));
        }

        @Override
        public SyscallExecutionAllocateMemory build() {
            return new SyscallExecutionAllocateMemory(amountRegister.get(), addressRegister.get());
        }

        @Override
        public SyscallExecutionBuilder<SyscallExecutionAllocateMemory> makeNewInstance() {
            return new Builder(provider);
        }

        @Override
        public SyscallExecutionBuilder<SyscallExecutionAllocateMemory> copy() {
            var builder = new Builder(provider);
            builder.amountRegister.setValue(amountRegister.getValue());
            builder.addressRegister.setValue(addressRegister.getValue());
            return builder;
        }
    }
}
