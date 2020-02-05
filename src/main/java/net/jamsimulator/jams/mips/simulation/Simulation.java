package net.jamsimulator.jams.mips.simulation;

import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.event.ByteGetEvent;
import net.jamsimulator.jams.mips.register.RegisterSet;

public class Simulation {

	private InstructionSet instructionSet;

	private RegisterSet registerSet;
	private Memory memory;

	public Simulation(InstructionSet instructionSet, RegisterSet registerSet, Memory memory) {
		this.instructionSet = instructionSet;
		this.registerSet = registerSet;
		this.memory = memory;
		memory.registerListeners(this);
	}

	public InstructionSet getInstructionSet() {
		return instructionSet;
	}

	public RegisterSet getRegisterSet() {
		return registerSet;
	}

	public Memory getMemory() {
		return memory;
	}

	@Listener
	private void byteGetEvent(ByteGetEvent.After event) {
		System.out.println(event.getValue());
	}
}
