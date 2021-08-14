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
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.file.SimulationFile;
import net.jamsimulator.jams.mips.syscall.SyscallExecution;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

import java.util.LinkedList;
import java.util.Optional;

public class SyscallExecutionReadFile implements SyscallExecution {

    public static final String NAME = "READ_FILE";

    private final int idRegister, addressRegister, maxBytesRegister, resultRegister;

    public SyscallExecutionReadFile(int idRegister, int addressRegister, int maxBytesRegister, int resultRegister) {
        this.idRegister = idRegister;
        this.addressRegister = addressRegister;
        this.maxBytesRegister = maxBytesRegister;
        this.resultRegister = resultRegister;
    }

    @Override
    public void execute(MIPSSimulation<?> simulation) {
        Register idRegister = simulation.getRegisters().getRegister(this.idRegister).orElse(null);
        if (idRegister == null) throw new IllegalStateException("Register " + this.idRegister + " not found");
        Register addressRegister = simulation.getRegisters().getRegister(this.addressRegister).orElse(null);
        if (addressRegister == null) throw new IllegalStateException("Register " + this.addressRegister + " not found");
        Register mBRegister = simulation.getRegisters().getRegister(this.maxBytesRegister).orElse(null);
        if (mBRegister == null) throw new IllegalStateException("Register " + this.maxBytesRegister + " not found");
        Register resultRegister = simulation.getRegisters().getRegister(this.resultRegister).orElse(null);
        if (resultRegister == null) throw new IllegalStateException("Register " + this.resultRegister + " not found");

        Optional<SimulationFile> optional = simulation.getFiles().get(idRegister.getValue());
        if (optional.isEmpty()) {
            resultRegister.setValue(-1);
            return;
        }

        SimulationFile file = optional.get();

        try {

            byte[] read = file.read(mBRegister.getValue());
            int address = addressRegister.getValue();
            Memory memory = simulation.getMemory();
            for (byte b : read) {
                memory.setByte(address++, b);
            }

            resultRegister.setValue(read.length);

        } catch (RuntimeException ex) {
            resultRegister.setValue(-1);
        }
    }

    @Override
    public void executeMultiCycle(MultiCycleExecution<?> execution) {
        var simulation = execution.getSimulation();

        var id = execution.value(idRegister);
        var address = execution.value(addressRegister);
        var maxBytes = execution.value(maxBytesRegister);

        Optional<SimulationFile> optional = simulation.getFiles().get(id);
        if (optional.isEmpty()) {
            execution.setAndUnlock(resultRegister, -1);
            return;
        }

        SimulationFile file = optional.get();

        try {
            byte[] read = file.read(maxBytes);
            Memory memory = simulation.getMemory();
            for (byte b : read) {
                memory.setByte(address++, b);
            }

            execution.setAndUnlock(resultRegister, read.length);
        } catch (RuntimeException ex) {
            execution.setAndUnlock(resultRegister, -1);
        }
    }

    public static class Builder extends SyscallExecutionBuilder<SyscallExecutionReadFile> {

        private final IntegerProperty idRegister;
        private final IntegerProperty addressRegister;
        private final IntegerProperty maxBytesRegister;
        private final IntegerProperty resultRegister;

        public Builder() {
            super(NAME, new LinkedList<>());
            properties.add(idRegister = new SimpleIntegerProperty(null, "ID_REGISTER", 4));
            properties.add(addressRegister = new SimpleIntegerProperty(null, "ADDRESS_REGISTER", 5));
            properties.add(maxBytesRegister = new SimpleIntegerProperty(null, "MAX_BYTES_REGISTER", 6));
            properties.add(resultRegister = new SimpleIntegerProperty(null, "RESULT_REGISTER", 2));
        }

        @Override
        public SyscallExecutionReadFile build() {
            return new SyscallExecutionReadFile(idRegister.get(), addressRegister.get(), maxBytesRegister.get(), resultRegister.get());
        }

        @Override
        public SyscallExecutionBuilder<SyscallExecutionReadFile> makeNewInstance() {
            return new Builder();
        }

        @Override
        public SyscallExecutionBuilder<SyscallExecutionReadFile> copy() {
            var builder = new Builder();
            builder.idRegister.setValue(idRegister.getValue());
            builder.addressRegister.setValue(addressRegister.getValue());
            builder.maxBytesRegister.setValue(maxBytesRegister.getValue());
            builder.resultRegister.setValue(resultRegister.getValue());
            return builder;
        }
    }
}
