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

package net.jamsimulator.jams.mips.syscall;

import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.syscall.defaults.SyscallExecutionRunExceptionHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SimulationSyscallExecutions {

    private static final SyscallExecutionRunExceptionHandler defaultExecution = new SyscallExecutionRunExceptionHandler();

    private final Map<Integer, SyscallExecution> executions;

    public SimulationSyscallExecutions() {
        executions = new HashMap<>();
    }

    public Map<Integer, SyscallExecution> getExecutions() {
        return Collections.unmodifiableMap(executions);
    }

    public SyscallExecution getExecution(int v0Code) {
        return executions.getOrDefault(v0Code, defaultExecution);
    }

    public void bindExecution(int v0Code, SyscallExecution execution) {
        executions.put(v0Code, execution);
    }

    public void executeSyscall(MIPSSimulation<?> simulation) {
        Register v0 = simulation.getRegisters().getRegister(2).orElse(null);
        if (v0 == null) throw new IllegalStateException("Register v0 not found");

        SyscallExecution execution = executions.getOrDefault(v0.getValue(), defaultExecution);
        execution.execute(simulation);
    }

    public void manageSyscallRequireAndLock(MultiCycleExecution<?, ?> execution) {
        var value = execution.value(2);
        var syscall = executions.getOrDefault(value, defaultExecution);
        syscall.getRequiredRegisters().forEach(it -> execution.requires(it, false));
        syscall.getLockedRegisters().forEach(execution::lock);
    }

    public Map<Integer, Integer> executeSyscallMultiCycle(MultiCycleExecution<?, ?> execution) {
        var value = execution.value(2);
        var syscall = executions.getOrDefault(value, defaultExecution);
        return syscall.executeMultiCycle(execution);
    }

}
