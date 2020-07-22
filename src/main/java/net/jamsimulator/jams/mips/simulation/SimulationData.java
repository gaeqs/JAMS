package net.jamsimulator.jams.mips.simulation;

import net.jamsimulator.jams.gui.util.log.Console;
import net.jamsimulator.jams.mips.syscall.SimulationSyscallExecutions;
import net.jamsimulator.jams.project.mips.MIPSSimulationConfiguration;

import java.io.File;
import java.util.Map;

/**
 * Represents the immutable data of a {@link Simulation}.
 * An instance of this class should be used to build any simulation.
 */
public class SimulationData {

	protected final SimulationSyscallExecutions syscallExecutions;
	protected final File workingDirectory;
	protected final Console console;
	protected final Map<Integer, String> originalInstructions;
	protected final boolean callEvents, undoEnabled;

	public SimulationData(SimulationSyscallExecutions syscallExecutions, File workingDirectory, Console console,
						  Map<Integer, String> originalInstructions, boolean callEvents, boolean undoEnabled) {
		this.syscallExecutions = syscallExecutions;
		this.workingDirectory = workingDirectory;
		this.console = console;
		this.originalInstructions = originalInstructions;
		this.callEvents = callEvents;
		this.undoEnabled = undoEnabled;
	}

	public SimulationData(MIPSSimulationConfiguration configuration, File workingDirectory, Console console, Map<Integer, String> originalInstructions) {
		this.syscallExecutions = new SimulationSyscallExecutions();
		configuration.getSyscallExecutionBuilders().forEach((key, builder) ->
				syscallExecutions.bindExecution(key, builder.build()));

		this.workingDirectory = workingDirectory;
		this.console = console;
		this.originalInstructions = originalInstructions;

		this.callEvents = configuration.isCallEvents();
		this.undoEnabled = configuration.isUndoEnabled() && callEvents;
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
	 * Returns the {@link Console} of this simulation.
	 * This console is used to print the output of the simulation and to receive data from the user.
	 *
	 * @return the {@link Console}.
	 */
	public Console getConsole() {
		return console;
	}

	public Map<Integer, String> getOriginalInstructions() {
		return originalInstructions;
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
	public boolean canCallEvents() {
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
	public boolean isUndoEnabled() {
		return undoEnabled;
	}
}
