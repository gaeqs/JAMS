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
import net.jamsimulator.jams.gui.util.log.Console;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.syscall.SyscallExecution;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

import java.util.LinkedList;

public class SyscallExecutionPrintString implements SyscallExecution {

    public static final String NAME = "PRINT_STRING";

    private final boolean lineJump;
    private final int maxChars, register;

    public SyscallExecutionPrintString(boolean lineJump, int maxChars, int register) {
        this.lineJump = lineJump;
        this.maxChars = Math.max(maxChars, 0);
        this.register = register;
    }

    @Override
    public void execute(MIPSSimulation<?> simulation) {
        Register register = simulation.getRegisters().getRegister(this.register).orElse(null);
        if (register == null) throw new IllegalStateException("Register " + this.register + " not found");
        print(simulation.getMemory(), simulation.getConsole(), register.getValue());
    }

    @Override
    public void executeMultiCycle(MultiCycleExecution<?, ?> execution) {
        print(execution.getSimulation().getMemory(), execution.getSimulation().getConsole(), execution.value(register));
    }

    private void print(Memory memory, Console console, int address) {
        char[] chars = new char[maxChars];
        int amount = 0;
        int c;
        while ((c = memory.getByte(address++)) != '\0' && amount < maxChars) {
            if (c < 0) c += 256;
            chars[amount++] = (char) c;
        }

        if (amount > 0) {
            String string = new String(chars, 0, amount);
            console.print(string);
        }

        if (lineJump) console.println();
    }

    public static class Builder extends SyscallExecutionBuilder<SyscallExecutionPrintString> {

        private final BooleanProperty lineJump;
        private final IntegerProperty maxChars;
        private final IntegerProperty register;

        public Builder(ResourceProvider provider) {
            super(provider, NAME, new LinkedList<>());
            properties.add(lineJump = new SimpleBooleanProperty(null, "LINE_JUMP", false));
            properties.add(maxChars = new SimpleIntegerProperty(null, "MAX_CHARACTERS", 4096));
            properties.add(register = new SimpleIntegerProperty(null, "REGISTER", 4));
        }

        @Override
        public SyscallExecutionPrintString build() {
            return new SyscallExecutionPrintString(lineJump.get(), maxChars.get(), register.get());
        }

        @Override
        public SyscallExecutionBuilder<SyscallExecutionPrintString> makeNewInstance() {
            return new Builder(provider);
        }

        @Override
        public SyscallExecutionBuilder<SyscallExecutionPrintString> copy() {
            var builder = new Builder(provider);
            builder.maxChars.setValue(maxChars.getValue());
            builder.lineJump.setValue(lineJump.getValue());
            builder.register.setValue(register.getValue());
            return builder;
        }
    }
}
