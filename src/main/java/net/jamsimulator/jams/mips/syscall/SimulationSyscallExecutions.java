package net.jamsimulator.jams.mips.syscall;

import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.Simulation;
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

	public void bindExecution(int v0Code, SyscallExecution execution) {
		executions.put(v0Code, execution);
	}

	public void executeSyscall(Simulation<?> simulation) {
		Register v0 = simulation.getRegisters().getRegister(2).orElse(null);
		if (v0 == null) throw new IllegalStateException("Register v0 not found");

		SyscallExecution execution = executions.getOrDefault(v0.getValue(), defaultExecution);
		execution.execute(simulation);
	}


	public void executeSyscallMultiCycle(MultiCycleExecution<?> execution) {
		var value = execution.value(2);
		var syscall = executions.getOrDefault(value, defaultExecution);
		syscall.executeMultiCycle(execution);
	}

}
