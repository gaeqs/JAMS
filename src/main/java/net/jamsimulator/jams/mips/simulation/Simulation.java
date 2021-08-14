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

import net.jamsimulator.jams.event.EventBroadcast;
import net.jamsimulator.jams.gui.util.log.Console;
import net.jamsimulator.jams.mips.instruction.exception.InstructionNotFoundException;

import java.util.function.Consumer;

/**
 * Represents the execution of a set of instructions, including all elements required for the correct
 * execution of the program.
 * <p>
 * External simulators must implement this interface for a correct behaviour of the user interface.
 *
 * @param <Address> the type of number the simulation uses to represent addresses.
 */
public interface Simulation<Address extends Number> extends EventBroadcast {

    /**
     * Returns the {@link Console} of this simulation.
     * This console is used to print the output of the simulation and to receive data from the user.
     *
     * @return the {@link Console}.
     */
    Console getConsole();

    /**
     * Returns whether the given address has a breakpoint.
     *
     * @param address the address.
     * @return whether there's a breakpoint in the given address.
     */
    boolean hasBreakpoint(Address address);

    /**
     * Adds a breakpoint to the given address.
     * If the given address has already a breakpoint this method returns false.
     *
     * @param address the address.
     * @return whether the operation was successful.
     */
    boolean addBreakpoint(Address address);

    /**
     * Removes a breakpoint of the given address.
     * If the given address has not a breakpoint this method returns false.
     *
     * @param address the address.
     * @return whether the operation was successful.
     */
    boolean removeBreakpoint(Address address);

    /**
     * Executes the given consumer for each breakpoint in this simulation.
     *
     * @param consumer the consumer.
     */
    void forEachBreakpoint(Consumer<Address> consumer);

    /**
     * Adds or removes the breakpoint linked to the given address.
     *
     * @param address the given address.
     */
    void toggleBreakpoint(Address address);

    /**
     * Returns the delay before a cycle of this simulation in milliseconds.
     * <p>
     * If the value is 0, this simulation will run at the fastest it can.
     *
     * @return the delay in ms.
     */
    int getCycleDelay();

    /**
     * Sets the delay before a cycle of this simulation in milliseconds.
     * <p>
     * If the value is 0, this simulation will run at the fastest it can.
     *
     * @param cycleDelay the delay in ms.
     */
    void setCycleDelay(int cycleDelay);

    /**
     * Returns the current cycle of this {@link MIPSSimulation}.
     * This is also the amount of executed cycles.
     *
     * @return the current cycle of this {@link MIPSSimulation}.
     */
    long getCycles();

    /**
     * Increases the cycle count by one.
     */
    void addCycleCount();

    /**
     * Returns whether this simulation is executing code.
     *
     * @return whether this simulatin is executing code.
     */
    boolean isRunning();

    /**
     * Marks the execution of this simulation as interrupted.
     * This method should be only called by the execution thread.
     * <p>
     * This will not execute a MIPS32 / 6502 interrupt!
     */
    void interruptThread();

    /**
     * Checks whether the execution thread was interrupted.
     * If true, the "interrupted" flag of the execution is marked as true.
     * This method should be only called by the execution thread.
     * <p>
     * This has nothing to do with MIPS32 / NES interrupts!
     *
     * @return whether the execution was interrupted.
     */
    boolean checkThreadInterrupted();

    /**
     * Stops the execution of the simulation.
     */
    void stop();

    /**
     * Returns the simulation to its initial state. This also stops the simulation if running.
     * <p>
     * This method waits for the simulation to finish if it's running.
     *
     * @throws InterruptedException if the {@link Thread} calling this method is interrupted while is waiting for the simulation to finish.
     */
    void reset() throws InterruptedException;

    /**
     * Waits till the current execution is finished.
     *
     * @throws InterruptedException if the {@link Thread} calling this method is interrupted while is waiting.
     */
    void waitForExecutionFinish() throws InterruptedException;

    /**
     * Executes the next step of this simulation.
     *
     * @throws InstructionNotFoundException when an instruction couldn't be decoded.
     */
    void executeOneStep();

    /**
     * Executes steps until the simulation is stopped.
     *
     * @throws InstructionNotFoundException when an instruction couldn't be decoded.
     */
    void executeAll();

    /**
     * Returns whether this simulation can undo steps.
     *
     * @return whether this simulationm can undo steps.
     */
    boolean isUndoEnabled();

    /**
     * Undoes the last step made by this simulation.
     * This method won't do anything if no steps were made.
     * <p>
     * If this simulation was executing all instructions and this method is used,
     * the simulation will stop.
     * <p>
     * This method waits for the simulation to finish if it's running.
     *
     * @return whether a step was undone.
     * @throws InterruptedException if the {@link Thread} calling this method is interrupted while is waiting for the simulation to finish.
     */
    boolean undoLastStep() throws InterruptedException;

    /**
     * Runs the given code synchronized to this simulation.
     * <p>
     * This allows to modify registers and memory safely.
     *
     * @param runnable the code to run.
     */
    void runSynchronized(Runnable runnable);

}
