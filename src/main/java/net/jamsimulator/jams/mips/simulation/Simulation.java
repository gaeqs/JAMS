/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.mips.simulation;

import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.gui.util.log.Console;
import net.jamsimulator.jams.gui.util.log.event.ConsoleInputEvent;
import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.exception.InstructionNotFoundException;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.event.MemoryByteSetEvent;
import net.jamsimulator.jams.mips.memory.event.MemoryWordSetEvent;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.simulation.event.SimulationFinishedEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationLockEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationResetEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationUnlockEvent;
import net.jamsimulator.jams.mips.simulation.file.SimulationFiles;

import java.util.*;

/**
 * Represents the execution of a set of instructions, including a memory and a register set.
 * Simulations are used to execute MIPS code.
 * <p>
 * They are based on {@link Architecture}s: the simulation should behave like the
 * architecture does on a real machine.
 * <p>
 * One step is equals to one cycle. Users should be able to execute and undo steps.
 * They should be also able to reset the simulation to their first state.
 *
 * @param <Arch> the architecture the simulation is based on.
 */
public abstract class Simulation<Arch extends Architecture> extends SimpleEventBroadcast {

	protected final Arch architecture;
	protected final InstructionSet instructionSet;
	protected final SimulationData data;

	protected final Registers registers;
	protected final Memory memory;
	protected final SimulationFiles files;

	protected int instructionStackBottom;
	protected final Set<Integer> breakpoints;

	protected final Map<Integer, AssembledInstruction> instructionCache;

	protected Thread thread;
	protected final Object lock;
	protected final Object finishedRunningLock;
	protected boolean interrupted;
	protected boolean running;
	protected boolean finished;

	/**
	 * Creates the simulation.
	 *
	 * @param architecture           the architecture of the simulation. This should be given by a simulation subclass.
	 * @param instructionSet         the instruction used by the simulation. This set should be the same as the set used to compile the code.
	 * @param registers              the registers to use on this simulation.
	 * @param memory                 the memory to use in this simulation.
	 * @param instructionStackBottom the address of the bottom of the instruction stack.
	 * @param data                   the immutable data of this simulation.
	 */
	public Simulation(Arch architecture, InstructionSet instructionSet, Registers registers, Memory memory, int instructionStackBottom, SimulationData data) {
		this.architecture = architecture;
		this.instructionSet = instructionSet;
		this.registers = registers;
		this.memory = memory;
		this.instructionStackBottom = instructionStackBottom;
		this.data = data;
		this.files = new SimulationFiles(this);

		this.breakpoints = new HashSet<>();
		this.instructionCache = new HashMap<>();

		if (data.canCallEvents() && data.isUndoEnabled()) {
			memory.registerListeners(this, true);
			registers.registerListeners(this, true);
			files.registerListeners(this, true);
		}

		if (getConsole() != null) {
			getConsole().registerListeners(this, true);
		}

		lock = new Object();
		finishedRunningLock = new Object();

		running = false;
		finished = false;
	}

	/**
	 * Returns the {@link Architecture} this simulation is based on.
	 *
	 * @return the {@link Architecture}.
	 */
	public Arch getArchitecture() {
		return architecture;
	}

	/**
	 * Returns the {@link InstructionSet} used by this simulation. This set is used to decode instructions.
	 *
	 * @return the {@link InstructionSet}.
	 */
	public InstructionSet getInstructionSet() {
		return instructionSet;
	}

	/**
	 * Returns a instance with the immutable data of this simulation.
	 *
	 * @return the instance.
	 */
	public SimulationData getData() {
		return data;
	}

	/**
	 * Returns the {@link Registers} of this simulation.
	 * <p>
	 * Modifications of these registers outside the simulation won't be registered by the simulation's changes stack,
	 * allowing no undo operations.
	 *
	 * @return the {@link Registers}.
	 */
	public Registers getRegisters() {
		return registers;
	}

	/**
	 * Returns the {@link Memory} of this simulation.
	 * <p>
	 * Modifications of the memory outside the simulation won't be registered by the simulation's changes stack,
	 * allowing no undo operations.
	 *
	 * @return the {@link Memory}.
	 */
	public Memory getMemory() {
		return memory;
	}

	/**
	 * Returns the open files of this simulation. This small manager allows to open, get and close files.
	 *
	 * @return the {@link SimulationFiles file manager}.
	 */
	public SimulationFiles getFiles() {
		return files;
	}

	/**
	 * Returns the {@link Console} of this simulation.
	 * This console is used to print the output of the simulation and to receive data from the user.
	 *
	 * @return the {@link Console}.
	 */
	public Console getConsole() {
		return data.getConsole();
	}

	/**
	 * Returns a modifiable {@link Set} with all this simulation's breakpoints.
	 *
	 * @return the {@link Set}.
	 */
	public Set<Integer> getBreakpoints() {
		return breakpoints;
	}

	/**
	 * Returns the instruction stack bottom address.
	 * This value may be modifiable if any instruction cell is modified in the {@link Memory}.
	 *
	 * @return the instruction stack bottom address.
	 */
	public int getInstructionStackBottom() {
		return instructionStackBottom;
	}


	public boolean isRunning() {
		return running;
	}

	/**
	 * Finishes the execution of this program.
	 * This value can be set to false using the methods {@link  #reset()} and {@link #undoLastStep()}.
	 */
	public void exit() {
		if (finished) return;
		finished = true;
		callEvent(new SimulationFinishedEvent(this));
	}

	/**
	 * Returns whether this simulation has finished its execution.
	 *
	 * @return whether this simulation has finished its execution.
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * Pops the next input or pauses the execution.
	 * If the execution is interrupted this method returns {@code null}.
	 * <p>
	 * This method should be called only by the execution thread.
	 */
	public String popInputOrLock() {
		Optional<String> optional = Optional.empty();
		while (!optional.isPresent()) {
			optional = data.getConsole().popInput();

			if (!optional.isPresent()) {
				try {
					callEvent(new SimulationLockEvent(this));
					synchronized (lock) {
						lock.wait();
					}
				} catch (InterruptedException ex) {
					interrupt();
					return null;
				}
			}
		}
		return optional.get();
	}

	/**
	 * Pops the next character or pauses the execution.
	 * If the execution is interrupted this method returns {@code 0}.
	 * <p>
	 * This method should be called only by the execution thread.
	 */
	public char popCharOrLock() {
		Optional<Character> optional = Optional.empty();
		while (!optional.isPresent()) {
			optional = data.getConsole().popChar();

			if (!optional.isPresent()) {
				try {
					callEvent(new SimulationLockEvent(this));
					synchronized (lock) {
						lock.wait();
					}
				} catch (InterruptedException ex) {
					interrupt();
					return 0;
				}
			}
		}
		return optional.get();
	}

	/**
	 * Marks the execution of this simulation as interrupted.
	 * This method should be only called by the execution thread.
	 */
	public void interrupt() {
		interrupted = true;
	}

	/**
	 * Checks whether the execution thread was interrupted.
	 * If true, the "interrupted" flag of the execution is marked as true.
	 * This method should be only called by the execution thread.
	 *
	 * @return whether the execution was interrupted.
	 */
	public boolean checkInterrupted() {
		if (Thread.interrupted()) interrupted = true;
		return interrupted;
	}

	/**
	 * ¿
	 * Fetch the {@link AssembledInstruction} located at the given address.
	 * <p>
	 * This method may return {@code null} if the instruction cannot be decoded.
	 *
	 * @param pc the address to fetch.
	 * @return the {@link AssembledInstruction} or null.
	 * @throws IllegalArgumentException  if the given address is not aligned to words.
	 * @throws IndexOutOfBoundsException if the address if out of bounds.
	 */
	public AssembledInstruction fetch(int pc) {
		AssembledInstruction cached = instructionCache.get(pc);
		if (cached != null) return cached;

		int data = memory.getWord(pc, true, true);

		Optional<? extends BasicInstruction<?>> optional = instructionSet.getInstructionByInstructionCode(data);
		if (!optional.isPresent()) return null;
		BasicInstruction<?> instruction = optional.get();
		cached = instruction.assembleFromCode(data);
		instructionCache.put(pc, cached);
		return cached;
	}

	/**
	 * Stops the execution of the simulation.
	 */
	public void stop() {
		if (thread != null) {
			thread.interrupt();
			thread = null;
		}
	}

	/**
	 * Returns the simulation to its first state.
	 * <p>
	 * This method depends on {@link Registers#restoreSavedState()} and {@link Memory#restoreSavedState()}.
	 * Any invocation to {@link Registers#saveState()} and {@link Memory#saveState()} on this simulation's
	 * {@link Memory} and {@link Registers} may result on unexpected results.
	 */
	public void reset() {
		stop();
		waitForExecutionFinish();

		registers.restoreSavedState();
		memory.restoreSavedState();
		finished = false;

		callEvent(new SimulationResetEvent(this));
	}

	/**
	 * Waits till the current execution is finished.
	 */
	public void waitForExecutionFinish() {
		synchronized (finishedRunningLock) {
			if (running) {
				//Wait till cycle restoration.
				try {
					finishedRunningLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Executes the next step of this simulation.
	 *
	 * @throws InstructionNotFoundException when an instruction couldn't be decoded or when the bottom of the instruction stack is reached.
	 */
	public abstract void nextStep();

	/**
	 * Executes steps until the bottom of the instruction stack is reached.
	 *
	 * @throws InstructionNotFoundException when an instruction couldn't be decoded.
	 */
	public abstract void executeAll();

	/**
	 * Undoes the last step made by this simulation.
	 * This method won't do nothing if no steps were made.
	 * <p>
	 * If this simulation was execution all instructions and this method is used,
	 * the simulation will stop.
	 *
	 * @return whether a step was undone.
	 */
	public abstract boolean undoLastStep();


	//TODO this should be reworked.
	@Listener
	private void onMemoryChange(MemoryByteSetEvent.After event) {
		int address = event.getAddress() >> 2 << 2;
		instructionCache.remove(address);
		if (event.getMemorySection().getName().equals("Text") && instructionStackBottom < event.getAddress()) {
			instructionStackBottom = address;
		}
	}

	@Listener
	private void onMemoryChange(MemoryWordSetEvent.After event) {
		int address = event.getAddress();
		instructionCache.remove(address);
		if (event.getMemorySection().getName().equals("Text") && instructionStackBottom < event.getAddress()) {
			instructionStackBottom = address;
		}
	}

	@Listener
	private void onInput(ConsoleInputEvent.After event) {
		callEvent(new SimulationUnlockEvent(this));
		synchronized (lock) {
			lock.notifyAll();
		}
	}

}
