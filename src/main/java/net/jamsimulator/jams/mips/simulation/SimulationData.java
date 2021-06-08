package net.jamsimulator.jams.mips.simulation;

import net.jamsimulator.jams.gui.util.log.Console;
import net.jamsimulator.jams.mips.label.Label;
import net.jamsimulator.jams.mips.syscall.SimulationSyscallExecutions;
import net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfiguration;
import net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfigurationPresets;

import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * Represents the immutable data of a {@link MIPSSimulation}.
 * An instance of this class should be used to build any simulation.
 */
public class SimulationData {

    protected final SimulationSyscallExecutions syscallExecutions;
    protected final File workingDirectory;
    protected final Console console;
    protected final Map<Integer, String> originalInstructions;
    protected final Set<Label> labels;
    protected final boolean callEvents, undoEnabled, enableForwarding, solveBranchOnDecode, enableDelaySlot;

    public SimulationData(SimulationSyscallExecutions syscallExecutions, File workingDirectory, Console console,
                          Map<Integer, String> originalInstructions, Set<Label> labels, boolean callEvents, boolean undoEnabled,
                          boolean enableForwarding, boolean solveBranchOnDecode, boolean enableDelaySlot) {
        this.syscallExecutions = syscallExecutions;
        this.workingDirectory = workingDirectory;
        this.console = console;
        this.originalInstructions = originalInstructions;
        this.labels = labels;
        this.callEvents = callEvents;
        this.undoEnabled = undoEnabled;
        this.enableForwarding = enableForwarding;
        this.solveBranchOnDecode = solveBranchOnDecode;
        this.enableDelaySlot = enableDelaySlot;
    }

    public SimulationData(MIPSSimulationConfiguration configuration, File workingDirectory, Console console,
                          Map<Integer, String> originalInstructions, Set<Label> labels) {
        this.syscallExecutions = new SimulationSyscallExecutions();
        configuration.getSyscallExecutionBuilders().forEach((key, builder) ->
                syscallExecutions.bindExecution(key, builder.build()));

        this.workingDirectory = workingDirectory;
        this.console = console;
        this.originalInstructions = originalInstructions;
        this.labels = labels;

        this.callEvents = configuration.getNodeValue(MIPSSimulationConfigurationPresets.CALL_EVENTS);
        this.undoEnabled = (boolean) configuration.getNodeValue(MIPSSimulationConfigurationPresets.UNDO_ENABLED) && callEvents;
        this.enableForwarding = configuration.getNodeValue(MIPSSimulationConfigurationPresets.FORWARDING_ENABLED);
        this.solveBranchOnDecode = configuration.getNodeValue(MIPSSimulationConfigurationPresets.BRANCH_ON_DECODE);
        this.enableDelaySlot = (boolean) configuration.getNodeValue(MIPSSimulationConfigurationPresets.DELAY_SLOTS_ENABLED) && solveBranchOnDecode;
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

    public Set<Label> getLabels() {
        return labels;
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

    /**
     * Allows instructions to forward data on a pipelined architecture.
     *
     * @return whether instructions can forward data.
     */
    public boolean isForwardingEnabled() {
        return enableForwarding;
    }

    /**
     * Allows to solve branches on decode. This only works on architectures with multiple steps.
     *
     * @return whether branches should be solved on the decode step.
     */
    public boolean shouldSolveBranchesOnDecode() {
        return solveBranchOnDecode;
    }

    /**
     * If this option is enabled, instructions right after a control
     * transfer instruction that is not compact will be always executed.
     *
     * @return whether delay slots are enabled.
     */
    public boolean areDelaySlotsEnabled() {
        return enableDelaySlot;
    }
}
