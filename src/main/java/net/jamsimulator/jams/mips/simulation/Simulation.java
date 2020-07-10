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
import net.jamsimulator.jams.mips.simulation.event.SimulationLockEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationUnlockEvent;
import net.jamsimulator.jams.mips.syscall.SimulationSyscallExecutions;

import java.util.Optional;
import java.util.function.Function;

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

	protected final Registers registers;
	protected final Memory memory;
	protected final SimulationSyscallExecutions syscallExecutions;

	private final Console console;

	protected boolean locked;
	protected Function<String, Boolean> functionExecution;
	protected boolean shouldResumeExecutionOnUnlock;


	protected boolean finished;
	protected int instructionStackBottom;

	/**
	 * Creates the simulation.
	 *
	 * @param architecture           the architecture of the simulation. This should be given by a simulation subclass.
	 * @param instructionSet         the instruction used by the simulation. This set should be the same as the set used to compile the code.
	 * @param registers              the registers to use on this simulation.
	 * @param memory                 the memory to use in this simulation.
	 * @param syscallExecutions      the syscall executions.
	 * @param console                the log used to output info.
	 * @param instructionStackBottom the address of the bottom of the instruction stack.
	 */
	public Simulation(Arch architecture, InstructionSet instructionSet, Registers registers, Memory memory,
					  SimulationSyscallExecutions syscallExecutions, Console console, int instructionStackBottom) {
		this.architecture = architecture;
		this.instructionSet = instructionSet;
		this.registers = registers;
		this.memory = memory;
		this.syscallExecutions = syscallExecutions;
		this.console = console;
		this.instructionStackBottom = instructionStackBottom;
		memory.registerListeners(this, true);
		registers.registerListeners(this, true);
		console.registerListeners(this, true);
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
	 * Returns the {@link SimulationSyscallExecutions syscall execution}s of this simulation.
	 * These executions are used when the instruction "syscall" is invoked.
	 *
	 * @return the {@link SimulationSyscallExecutions syscall execution}s.
	 */
	public SimulationSyscallExecutions getSyscallExecutions() {
		return syscallExecutions;
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

	/**
	 * Returns the instruction stack bottom address.
	 * This value may be modifiable if any instruction cell is modified in the {@link Memory}.
	 *
	 * @return the instruction stack bottom address.
	 */
	public int getInstructionStackBottom() {
		return instructionStackBottom;
	}

	/**
	 * Returns whether the simulation is locked.
	 * <p>
	 * A simulation is locked when it's waiting some data from the user.
	 * <p>
	 * Resets and undoes will unlock the simulation.
	 *
	 * @return whether the simulation is locked.
	 */
	public boolean isLocked() {
		return locked;
	}

	public void popInputOrLock(Function<String, Boolean> onUnlock) {
		Optional<String> optional;
		boolean finished = false;
		while (!finished) {
			optional = console.popInput();
			if (!optional.isPresent()) {
				callEvent(new SimulationLockEvent(this));
				locked = true;
				functionExecution = onUnlock;
				return;
			} else {
				finished = onUnlock.apply(optional.get());
			}
		}
	}

	/**¿
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
		int data = memory.getWord(pc);
		Optional<? extends BasicInstruction<?>> optional = instructionSet.getInstructionByInstructionCode(data);
		if (!optional.isPresent()) return null;
		BasicInstruction<?> instruction = optional.get();
		return instruction.assembleFromCode(data);
	}

	/**
	 * Returns the simulation to its first state.
	 * <p>
	 * This method depends on {@link Registers#restoreSavedState()} and {@link Memory#restoreSavedState()}.
	 * Any invocation to {@link Registers#saveState()} and {@link Memory#saveState()} on this simulation's
	 * {@link Memory} and {@link Registers} may result on unexpected results.
	 */
	public void reset() {
		registers.restoreSavedState();
		memory.restoreSavedState();
		callEvent(new SimulationUnlockEvent(this));
		locked = false;
		finished = false;
		shouldResumeExecutionOnUnlock = false;
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

	/**
	 * Finishes the execution of this program.
	 * This value can be set to false using the methods {@link  #reset()} and {@link #undoLastStep()}.
	 */
	public void exit() {
		finished = true;
	}

	/**
	 * Returns whether this simulation has finished its execution.
	 *
	 * @return whether this simulation has finished its execution.
	 */
	public boolean isFinished() {
		return finished;
	}

	@Listener
	private void onMemoryChange(MemoryByteSetEvent.After event) {
		if (event.getMemorySection().getName().equals("Text") && instructionStackBottom < event.getAddress()) {
			instructionStackBottom = event.getAddress() >> 2 << 2;
		}
	}

	@Listener
	private void onMemoryChange(MemoryWordSetEvent.After event) {
		if (event.getMemorySection().getName().equals("Text") && instructionStackBottom < event.getAddress()) {
			instructionStackBottom = event.getAddress();
		}
	}

	@Listener
	private void onInput(ConsoleInputEvent.After event) {
		if (!locked) return;
		locked = false;
		callEvent(new SimulationUnlockEvent(this));
		popInputOrLock(functionExecution);
		if (shouldResumeExecutionOnUnlock) {
			executeAll();
		}
	}

}
