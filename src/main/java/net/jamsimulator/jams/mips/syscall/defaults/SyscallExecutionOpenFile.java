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
import net.jamsimulator.jams.mips.syscall.SyscallExecution;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

public class SyscallExecutionOpenFile implements SyscallExecution {

    public static final String NAME = "OPEN_FILE";

    private final int nameRegister, flagRegister, modeRegister, resultRegister;

    public SyscallExecutionOpenFile(int nameRegister, int flagRegister, int modeRegister, int resultRegister) {
        this.nameRegister = nameRegister;
        this.flagRegister = flagRegister;
        this.modeRegister = modeRegister;
        this.resultRegister = resultRegister;
    }

    @Override
    public void execute(MIPSSimulation<?> simulation) {
        Register nameRegister = simulation.getRegisters().getRegister(this.nameRegister).orElse(null);
        if (nameRegister == null) throw new IllegalStateException("Register " + this.nameRegister + " not found");
        Register flagRegister = simulation.getRegisters().getRegister(this.flagRegister).orElse(null);
        if (flagRegister == null) throw new IllegalStateException("Register " + this.flagRegister + " not found");
        Register modeRegister = simulation.getRegisters().getRegister(this.modeRegister).orElse(null);
        if (modeRegister == null) throw new IllegalStateException("Register " + this.modeRegister + " not found");
        Register resultRegister = simulation.getRegisters().getRegister(this.resultRegister).orElse(null);
        if (resultRegister == null) throw new IllegalStateException("Register " + this.resultRegister + " not found");

        String name = getString(simulation, nameRegister.getValue());
        if (name.isEmpty()) {
            resultRegister.setValue(-1);
            return;
        }

        File file;
        Path path = Paths.get(name);
        if (path.isAbsolute()) {
            file = path.toFile();
        } else {
            file = new File(simulation.getData().getWorkingDirectory(), name);
        }

        boolean write, append;

        switch (flagRegister.getValue()) {
            case 0 -> {
                write = false;
                append = false;
            }
            case 1 -> {
                write = true;
                append = false;
            }
            case 9 -> {
                write = true;
                append = true;
            }
            default -> {
                resultRegister.setValue(-1);
                return;
            }
        }

        try {
            resultRegister.setValue(simulation.getFiles().open(file, write, append));
        } catch (IOException ex) {
            resultRegister.setValue(-1);
        }

    }

    @Override
    public void executeMultiCycle(MultiCycleExecution<?> execution) {
        var nameAddress = execution.value(nameRegister);
        var flag = execution.value(flagRegister);
        var mode = execution.value(modeRegister);


        var name = getString(execution.getSimulation(), nameAddress);
        if (name.isEmpty()) {
            execution.setAndUnlock(resultRegister, -1);
            return;
        }

        File file;
        Path path = Paths.get(name);
        if (path.isAbsolute()) {
            file = path.toFile();
        } else {
            file = new File(execution.getSimulation().getData().getWorkingDirectory(), name);
        }

        boolean write, append;

        switch (flag) {
            case 0 -> {
                write = false;
                append = false;
            }
            case 1 -> {
                write = true;
                append = false;
            }
            case 9 -> {
                write = true;
                append = true;
            }
            default -> {
                execution.setAndUnlock(resultRegister, -1);
                return;
            }
        }

        try {
            execution.setAndUnlock(resultRegister, execution.getSimulation().getFiles().open(file, write, append));
        } catch (IOException ex) {
            execution.setAndUnlock(resultRegister, -1);
        }
    }

    private String getString(MIPSSimulation<?> simulation, int address) {
        Memory memory = simulation.getMemory();
        char[] chars = new char[1024];
        int amount = 0;
        char c;
        while ((c = (char) memory.getByte(address++)) != '\0' && amount < 1024) {
            chars[amount++] = c;
        }
        return new String(chars, 0, amount);
    }

    public static class Builder extends SyscallExecutionBuilder<SyscallExecutionOpenFile> {

        private final IntegerProperty nameRegister;
        private final IntegerProperty flagRegister;
        private final IntegerProperty modeRegister;
        private final IntegerProperty resultRegister;

        public Builder() {
            super(NAME, new LinkedList<>());
            properties.add(nameRegister = new SimpleIntegerProperty(null, "NAME_REGISTER", 4));
            properties.add(flagRegister = new SimpleIntegerProperty(null, "FLAG_REGISTER", 5));
            properties.add(modeRegister = new SimpleIntegerProperty(null, "MODE_REGISTER", 6));
            properties.add(resultRegister = new SimpleIntegerProperty(null, "RESULT_REGISTER", 2));
        }

        @Override
        public SyscallExecutionOpenFile build() {
            return new SyscallExecutionOpenFile(nameRegister.get(), flagRegister.get(), modeRegister.get(), resultRegister.get());
        }

        @Override
        public SyscallExecutionBuilder<SyscallExecutionOpenFile> makeNewInstance() {
            return new Builder();
        }

        @Override
        public SyscallExecutionBuilder<SyscallExecutionOpenFile> copy() {
            var builder = new Builder();
            builder.nameRegister.setValue(nameRegister.getValue());
            builder.flagRegister.setValue(flagRegister.getValue());
            builder.modeRegister.setValue(modeRegister.getValue());
            builder.resultRegister.setValue(resultRegister.getValue());
            return builder;
        }
    }
}
