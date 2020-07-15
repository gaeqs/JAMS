package net.jamsimulator.jams.mips.simulation;

import net.jamsimulator.jams.gui.util.log.Console;
import net.jamsimulator.jams.mips.syscall.SimulationSyscallExecutions;
import net.jamsimulator.jams.project.mips.MipsSimulationConfiguration;

import java.io.File;

/**
 * Represents the immutable data of a {@link Simulation}.
 * An instance of this class should be used to build any simulation.
 */
public class SimulationData {

	protected final SimulationSyscallExecutions syscallExecutions;
	protected final File workingDirectory;
	protected final Console console;
	protected final boolean callEvents, enableUndo;

	public SimulationData(SimulationSyscallExecutions syscallExecutions, File workingDirectory, boolean callEvents, boolean enableUndo, Console console) {
		this.syscallExecutions = syscallExecutions;
		this.workingDirectory = workingDirectory;
		this.callEvents = callEvents;
		this.enableUndo = enableUndo;
		this.console = console;
	}

	public SimulationData(MipsSimulationConfiguration configuration, File workingDirectory, Console console) {
		this.syscallExecutions = new SimulationSyscallExecutions();
		configuration.getSyscallExecutionBuilders().forEach((key, builder) ->
				syscallExecutions.bindExecution(key, builder.build()));

		this.workingDirectory = workingDirectory;
		this.callEvents = configuration.isCallEvents();
		this.enableUndo = configuration.isEnableUndo() && callEvents;
		this.console = console;
	}

	/**
	 * Returns the {@link SimulationSyscallExecutions syscall execution}s of this simulation.
	 * These executions are used when the instruction "syscall" is invoked.
	 *
	 * @return the {@link SimulationSyscallExecutions syscall execution}s.
	 */
	public SimulationSyscallExecutions getSyscallExecutions() {
		return syscallExecutions;
	}

	/**
	 * Returns the working directory of this simulation.
	 * <p>
	 * The simulation used this directory as the relative root for files.
	 *
	 * @return the working directory.
	 */
	public File getWorkingDirectory() {
		return workingDirectory;
	}

	/**
	 * Returns whether the simulation should generate register, memory and instruction events.
	 * <p>
	 * If this option is enabled the simulation will be executed faster, but some options or plugins won't work.
	 * <p>
	 * These events would work if the simulation is stopped.
	 *
	 * @return whether the simulation should generate register, memory and instruction events.
	 */
	public boolean isCallEvents() {
		return callEvents;
	}

	/**
	 * Allows to undo instruction executions.
	 * <p>
	 * This technology consumes memory and computational resources.
	 * If this option is disable the simulation will be executed faster, but you won't be able to undo instructions.
	 * <p>
	 * This option requires events to be enabled.
	 *
	 * @return whether instruction's undoes are enabled.
	 */
	public boolean isEnableUndo() {
		return enableUndo;
	}

	/**
	 * Returns the {@link Console} of this simulation.
	 * This console is used to print the output of the simulation and to receive data from the user.
	 *
	 * @return the {@link Console}.
	 */
	public Console getConsole() {
		return console;
	}
}
