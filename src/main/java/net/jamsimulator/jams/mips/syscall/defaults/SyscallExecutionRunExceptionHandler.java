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

import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.interrupt.InterruptCause;
import net.jamsimulator.jams.mips.interrupt.MIPSInterruptException;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.syscall.SyscallExecution;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class SyscallExecutionRunExceptionHandler implements SyscallExecution {

    public static final String NAME = "RUN_EXCEPTION_HANDLER";

    @Override
    public void execute(MIPSSimulation<?> simulation) {
        throw new MIPSInterruptException(InterruptCause.SYSCALL_EXCEPTION);
    }

    @Override
    public Map<Integer, Integer> executeMultiCycle(MultiCycleExecution<?, ?> execution) {
        throw new MIPSInterruptException(InterruptCause.SYSCALL_EXCEPTION);
    }

    @Override
    public Set<Integer> getRequiredRegisters() {
        return Collections.emptySet();
    }

    @Override
    public Set<Integer> getLockedRegisters() {
        return Collections.emptySet();
    }

    public static class Builder extends SyscallExecutionBuilder<SyscallExecutionRunExceptionHandler> {

        public Builder(ResourceProvider provider) {
            super(provider, NAME, Collections.emptyList());
        }

        @Override
        public SyscallExecutionRunExceptionHandler build() {
            return new SyscallExecutionRunExceptionHandler();
        }

        @Override
        public SyscallExecutionBuilder<SyscallExecutionRunExceptionHandler> makeNewInstance() {
            return new Builder(provider);
        }

        @Override
        public SyscallExecutionBuilder<SyscallExecutionRunExceptionHandler> copy() {
            return new Builder(provider);
        }
    }
}
