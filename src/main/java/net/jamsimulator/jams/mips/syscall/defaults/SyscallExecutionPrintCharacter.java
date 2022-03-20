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

import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class SyscallExecutionPrintCharacter implements SyscallExecution {

    public static final String NAME = "PRINT_CHARACTER";

    private final boolean lineJump;
    private final int register;

    private final Set<Integer> requiredRegisters;

    public SyscallExecutionPrintCharacter(boolean lineJump, int register) {
        this.lineJump = lineJump;
        this.register = register;

        requiredRegisters = Set.of(register);
    }

    @Override
    public void execute(MIPSSimulation<?> simulation) {
        Register register = simulation.getRegisters().getRegister(this.register).orElse(null);
        if (register == null) throw new IllegalStateException("Register " + this.register + " not found");
        char value = (char) (register.getValue() & 0xFF);
        simulation.getLog().print(value);
        if (lineJump) simulation.getLog().println();
    }

    @Override
    public Map<Integer, Integer> executeMultiCycle(MultiCycleExecution<?, ?> execution) {
        var character = (char) (execution.value(register) & 0xFF);
        var console = execution.getSimulation().getLog();
        console.print(character);
        if (lineJump) console.println();
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

    public static class Builder extends SyscallExecutionBuilder<SyscallExecutionPrintCharacter> {

        private final BooleanProperty lineJump;
        private final IntegerProperty register;

        public Builder(ResourceProvider provider) {
            super(provider, NAME, new LinkedList<>());
            properties.add(lineJump = new SimpleBooleanProperty(null, "LINE_JUMP", false));
            properties.add(register = new SimpleIntegerProperty(null, "REGISTER", 4));
        }

        @Override
        public SyscallExecutionPrintCharacter build() {
            return new SyscallExecutionPrintCharacter(lineJump.get(), register.get());
        }

        @Override
        public SyscallExecutionBuilder<SyscallExecutionPrintCharacter> makeNewInstance() {
            return new Builder(provider);
        }

        @Override
        public SyscallExecutionBuilder<SyscallExecutionPrintCharacter> copy() {
            var builder = new Builder(provider);
            builder.lineJump.setValue(lineJump.getValue());
            builder.register.setValue(register.getValue());
            return builder;
        }
    }
}
