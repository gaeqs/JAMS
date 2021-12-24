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
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.file.SimulationFile;
import net.jamsimulator.jams.mips.syscall.SyscallExecution;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

import java.util.LinkedList;
import java.util.Optional;

public class SyscallExecutionWriteFile implements SyscallExecution {

    public static final String NAME = "WRITE_FILE";

    private final int idRegister, addressRegister, amountRegister, resultRegister;

    public SyscallExecutionWriteFile(int idRegister, int addressRegister, int amountRegister, int resultRegister) {
        this.idRegister = idRegister;
        this.addressRegister = addressRegister;
        this.amountRegister = amountRegister;
        this.resultRegister = resultRegister;
    }

    @Override
    public void execute(MIPSSimulation<?> simulation) {
        Register idRegister = simulation.getRegisters().getRegister(this.idRegister).orElse(null);
        if (idRegister == null) throw new IllegalStateException("Register " + this.idRegister + " not found");
        Register addressRegister = simulation.getRegisters().getRegister(this.addressRegister).orElse(null);
        if (addressRegister == null) throw new IllegalStateException("Register " + this.addressRegister + " not found");
        Register amountRegister = simulation.getRegisters().getRegister(this.amountRegister).orElse(null);
        if (amountRegister == null) throw new IllegalStateException("Register " + this.amountRegister + " not found");
        Register resultRegister = simulation.getRegisters().getRegister(this.resultRegister).orElse(null);
        if (resultRegister == null) throw new IllegalStateException("Register " + this.resultRegister + " not found");

        Optional<SimulationFile> optional = simulation.getFiles().get(idRegister.getValue());
        if (optional.isEmpty()) {
            resultRegister.setValue(-1);
            return;
        }

        SimulationFile file = optional.get();

        Memory memory = simulation.getMemory();
        byte[] bytes = new byte[amountRegister.getValue()];
        int address = addressRegister.getValue();

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = memory.getByte(address++);
        }

        try {
            file.write(bytes);
            resultRegister.setValue(bytes.length);
        } catch (RuntimeException ex) {
            resultRegister.setValue(-1);
        }
    }

    @Override
    public void executeMultiCycle(MultiCycleExecution<?, ?> execution) {
        var simulation = execution.getSimulation();
        var id = execution.value(idRegister);
        var address = execution.value(addressRegister);
        var amount = execution.value(amountRegister);

        Optional<SimulationFile> optional = simulation.getFiles().get(id);
        if (optional.isEmpty()) {
            execution.setAndUnlock(resultRegister, -1);
            return;
        }

        SimulationFile file = optional.get();

        Memory memory = simulation.getMemory();
        byte[] bytes = new byte[amount];

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = memory.getByte(address++);
        }

        try {
            file.write(bytes);
            execution.setAndUnlock(resultRegister, bytes.length);
        } catch (RuntimeException ex) {
            execution.setAndUnlock(resultRegister, -1);
        }
    }

    public static class Builder extends SyscallExecutionBuilder<SyscallExecutionWriteFile> {

        private final IntegerProperty idRegister;
        private final IntegerProperty addressRegister;
        private final IntegerProperty amountRegister;
        private final IntegerProperty resultRegister;

        public Builder(ResourceProvider provider) {
            super(provider, NAME, new LinkedList<>());
            properties.add(idRegister = new SimpleIntegerProperty(null, "ID_REGISTER", 4));
            properties.add(addressRegister = new SimpleIntegerProperty(null, "ADDRESS_REGISTER", 5));
            properties.add(amountRegister = new SimpleIntegerProperty(null, "AMOUNT_REGISTER", 6));
            properties.add(resultRegister = new SimpleIntegerProperty(null, "RESULT_REGISTER", 2));
        }

        @Override
        public SyscallExecutionWriteFile build() {
            return new SyscallExecutionWriteFile(idRegister.get(), addressRegister.get(), amountRegister.get(), resultRegister.get());
        }

        @Override
        public SyscallExecutionBuilder<SyscallExecutionWriteFile> makeNewInstance() {
            return new Builder(provider);
        }

        @Override
        public SyscallExecutionBuilder<SyscallExecutionWriteFile> copy() {
            var builder = new Builder(provider);
            builder.idRegister.setValue(idRegister.getValue());
            builder.addressRegister.setValue(addressRegister.getValue());
            builder.amountRegister.setValue(amountRegister.getValue());
            builder.resultRegister.setValue(resultRegister.getValue());
            return builder;
        }
    }
}
