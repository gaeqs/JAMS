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

package net.jamsimulator.jams.mips.instruction.execution;

import net.jamsimulator.jams.mips.architecture.MultiCycleArchitecture;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.pipelined.ForwardingSupporter;
import net.jamsimulator.jams.mips.simulation.pipelined.exception.RAWHazardException;

public abstract class MultiCycleExecution<Inst extends AssembledInstruction> extends InstructionExecution<MultiCycleArchitecture, Inst> {

	protected int[] decodeResult;
	protected int[] executionResult;
	protected int[] memoryResult;

	protected boolean executesMemory, executesWriteBack;

	public MultiCycleExecution(Simulation<MultiCycleArchitecture> simulation, Inst instruction, int address, boolean executesMemory, boolean executesWriteBack) {
		super(simulation, instruction, address);
		this.executesMemory = executesMemory;
		this.executesWriteBack = executesWriteBack;
	}

	public boolean executesMemory() {
		return executesMemory;
	}

	public boolean executesWriteBack() {
		return executesWriteBack;
	}

	//region requires

	public void requires(int identifier) {
		requires(register(identifier));
	}

	public void requiresCOP0(int identifier) {
		requires(registerCop0(identifier));
	}

	public void requiresCOP0(int identifier, int sel) {
		requires(registerCop0(identifier, sel));
	}

	public void requiresCOP1(int identifier) {
		requires(registerCop1(identifier));
	}

	public void requires(Register register) {
		var supportsForwarding = simulation instanceof ForwardingSupporter
				&& ((ForwardingSupporter) simulation).isForwardingSupported();

		if (register.isLocked() && !supportsForwarding) {
			throw new RAWHazardException();
		}
	}

	//endregion

	//region value

	public int value(int identifier) {
		return value(register(identifier));
	}

	public int valueCOP0(int identifier) {
		return value(registerCop0(identifier));
	}

	public int valueCOP0(int identifier, int sel) {
		return value(registerCop0(identifier, sel));
	}

	public int valueCOP1(int identifier) {
		return value(registerCop1(identifier));
	}

	public int value(Register register) {
		if (!register.isLocked(this)) {
			return register.getValue();
		}

		if (simulation instanceof ForwardingSupporter) {
			var optional = ((ForwardingSupporter) simulation).getForwarding().get(register);
			if (optional.isPresent()) return optional.getAsInt();
		}

		throw new RAWHazardException();
	}

	//endregion value

	//region lock

	public void lock(int identifier) {
		register(identifier).lock(this);
	}

	public void lockCOP0(int identifier) {
		registerCop0(identifier).lock(this);
	}

	public void lockCOP0(int identifier, int sel) {
		registerCop0(identifier, sel).lock(this);
	}

	public void lockCOP1(int identifier) {
		registerCop1(identifier).lock(this);
	}

	public void lock(Register register) {
		register.lock(this);
	}

	//endregion

	//region unlock

	public void unlock(int identifier) {
		register(identifier).unlock(this);
	}

	public void unlockCOP0(int identifier) {
		registerCop0(identifier).unlock(this);
	}

	public void unlockCOP0(int identifier, int sel) {
		registerCop0(identifier, sel).unlock(this);
	}

	public void unlockCOP1(int identifier) {
		registerCop1(identifier).unlock(this);
	}

	public void unlock(Register register) {
		register.unlock(this);
	}

	//endregion

	//region set and unlock

	public void setAndUnlock(int identifier, int value) {
		setAndUnlock(register(identifier), value);
	}

	public void setAndUnlockCOP0(int identifier, int value) {
		setAndUnlock(registerCop0(identifier), value);
	}

	public void setAndUnlockCOP0(int identifier, int sel, int value) {
		setAndUnlock(registerCop0(identifier, sel), value);
	}

	public void setAndUnlockCOP1(int identifier, int value) {
		setAndUnlock(registerCop1(identifier), value);
	}

	public void setAndUnlock(Register register, int value) {
		register.setValue(value);
		register.unlock(this);
	}

	//endregion

	//region forward

	public void forward(int identifier, int value, boolean memory) {
		forward(register(identifier), value, memory);
	}

	public void forwardCOP0(int identifier, int value, boolean memory) {
		forward(registerCop0(identifier), value, memory);
	}

	public void forwardCOP0(int identifier, int sel, int value, boolean memory) {
		forward(registerCop0(identifier, sel), value, memory);
	}

	public void forwardCOP1(int identifier, int value, boolean memory) {
		forward(registerCop1(identifier), value, memory);
	}

	public void forward(Register register, int value, boolean memory) {
		if (simulation instanceof ForwardingSupporter) {
			((ForwardingSupporter) simulation).getForwarding().forward(register, value, memory);
		}
	}

	//endregion

	public abstract void decode();

	public abstract void execute();

	public abstract void memory();

	public abstract void writeBack();
}
