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
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.event.SimulationFinishedEvent;
import net.jamsimulator.jams.mips.syscall.SyscallExecution;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

import java.util.LinkedList;

public class SyscallExecutionExit implements SyscallExecution {

    public static final String NAME = "EXIT";

    public SyscallExecutionExit() {
    }

    @Override
    public void execute(MIPSSimulation<?> simulation) {
        simulation.requestExit();
        if (simulation.getConsole() != null) {
            simulation.getConsole().println();
            simulation.getConsole().printDoneLn("Execution finished successfully");
            simulation.getConsole().println();
        }
        simulation.callEvent(new SimulationFinishedEvent(simulation));
    }

    @Override
    public void executeMultiCycle(MultiCycleExecution<?> execution) {
        execute(execution.getSimulation());
    }

    public static class Builder extends SyscallExecutionBuilder<SyscallExecutionExit> {

        public Builder(ResourceProvider provider) {
            super(provider, NAME, new LinkedList<>());
        }

        @Override
        public SyscallExecutionExit build() {
            return new SyscallExecutionExit();
        }

        @Override
        public SyscallExecutionBuilder<SyscallExecutionExit> makeNewInstance() {
            return new Builder(provider);
        }

        @Override
        public SyscallExecutionBuilder<SyscallExecutionExit> copy() {
            return new Builder(provider);
        }
    }
}
