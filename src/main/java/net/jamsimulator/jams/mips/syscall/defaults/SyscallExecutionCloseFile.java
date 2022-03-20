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

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class SyscallExecutionCloseFile implements SyscallExecution {

    public static final String NAME = "CLOSE_FILE";

    private final int idRegister;

    private final Set<Integer> requiredRegisters;

    public SyscallExecutionCloseFile(int idRegister) {
        this.idRegister = idRegister;
        requiredRegisters = Set.of(idRegister);
    }

    @Override
    public void execute(MIPSSimulation<?> simulation) {
        Register idRegister = simulation.getRegisters().getRegister(this.idRegister).orElse(null);
        if (idRegister == null) throw new IllegalStateException("Register " + this.idRegister + " not found");

        try {
            simulation.getFiles().close(idRegister.getValue());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<Integer, Integer> executeMultiCycle(MultiCycleExecution<?, ?> execution) {
        var id = execution.value(idRegister);

        try {
            execution.getSimulation().getFiles().close(id);
        } catch (IOException ex) {
            ex.printStackTrace();
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

    public static class Builder extends SyscallExecutionBuilder<SyscallExecutionCloseFile> {

        private final IntegerProperty idRegister;

        public Builder(ResourceProvider provider) {
            super(provider, NAME, new LinkedList<>());
            properties.add(idRegister = new SimpleIntegerProperty(null, "ID_REGISTER", 4));
        }

        @Override
        public SyscallExecutionCloseFile build() {
            return new SyscallExecutionCloseFile(idRegister.get());
        }

        @Override
        public SyscallExecutionBuilder<SyscallExecutionCloseFile> makeNewInstance() {
            return new Builder(provider);
        }

        @Override
        public SyscallExecutionBuilder<SyscallExecutionCloseFile> copy() {
            var builder = new Builder(provider);
            builder.idRegister.setValue(idRegister.getValue());
            return builder;
        }
    }
}
