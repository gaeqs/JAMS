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

package net.jamsimulator.jams.mips.simulation;

import net.jamsimulator.jams.gui.util.log.Console;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.label.Label;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.syscall.SimulationSyscallExecutions;
import net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfiguration;
import net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfigurationPresets;

import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * Wrapper of the information required to build a {@link MIPSSimulation}.
 * <p>
 * Elements of this elements may be mutable. Use only to send data to the simulation's constructor.
 */
public record MIPSSimulationData(
        MIPSSimulationConfiguration configuration,
        File workingDirectory,
        Console console,
        MIPSSimulationSource source,
        InstructionSet instructionSet,
        Registers registers,
        Memory memory,
        int instructionStackBottom,
        int kernelStackBottom) {

    ///**
    // * Returns the {@link SimulationSyscallExecutions syscall execution}s of this simulation.
    // * These executions are used when the instruction "syscall" is invoked.
    // *
    // * @return the {@link SimulationSyscallExecutions syscall execution}s.
    // */
    //public SimulationSyscallExecutions getSyscallExecutions() {
    //    return syscallExecutions;
    //}
//
    ///**
    // * Returns the working directory of this simulation.
    // * <p>
    // * The simulation used this directory as the relative root for files.
    // *
    // * @return the working directory.
    // */
    //public File getWorkingDirectory() {
    //    return workingDirectory;
    //}
//
    ///**
    // * Returns the {@link Console} of this simulation.
    // * This console is used to print the output of the simulation and to receive data from the user.
    // *
    // * @return the {@link Console}.
    // */
    //public Console getConsole() {
    //    return console;
    //}
//
    //public Map<Integer, String> getOriginalInstructions() {
    //    return originalInstructions;
    //}
//
    //public Set<Label> getLabels() {
    //    return labels;
    //}
//
    ///**
    // * Returns whether the simulation should generate register, memory and instruction events.
    // * <p>
    // * If this option is enabled the simulation will be executed faster, but some options or plugins won't work.
    // * <p>
    // * These events would work if the simulation is stopped.
    // *
    // * @return whether the simulation should generate register, memory and instruction events.
    // */
    //public boolean canCallEvents() {
    //    return callEvents;
    //}
//
    ///**
    // * Allows undoing instruction executions.
    // * <p>
    // * This technology consumes memory and computational resources.
    // * If this option is disable the simulation will be executed faster, but you won't be able to undo instructions.
    // * <p>
    // * This option requires events to be enabled.
    // *
    // * @return whether instruction's undoes are enabled.
    // */
    //public boolean isUndoEnabled() {
    //    return undoEnabled;
    //}
//
    ///**
    // * Allows instructions to forward data on a pipelined architecture.
    // *
    // * @return whether instructions can forward data.
    // */
    //public boolean isForwardingEnabled() {
    //    return enableForwarding;
    //}
//
    ///**
    // * Allows solving branches on decode. This only works on architectures with multiple steps.
    // *
    // * @return whether branches should be solved on the decode step.
    // */
    //public boolean shouldSolveBranchesOnDecode() {
    //    return solveBranchOnDecode;
    //}
//
    ///**
    // * If this option is enabled, instructions right after a control
    // * transfer instruction that is not compact will always be executed.
    // *
    // * @return whether delay slots are enabled.
    // */
    //public boolean areDelaySlotsEnabled() {
    //    return enableDelaySlot;
    //}
}
