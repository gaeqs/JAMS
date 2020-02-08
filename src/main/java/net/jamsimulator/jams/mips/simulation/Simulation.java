package net.jamsimulator.jams.mips.simulation;

import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledInstruction;
import net.jamsimulator.jams.mips.instruction.exception.InstructionNotFoundException;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.register.RegisterSet;

import java.util.Optional;

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

	public CompiledInstruction getInstruction(int pc) {
		//Fetch
		int data = memory.getWord(pc);
		//Decode
		Optional<BasicInstruction> optional = instructionSet.getInstructionByInstructionCode(data);
		if (!optional.isPresent()) return null;
		BasicInstruction instruction = optional.get();
		return instruction.compileFromCode(data);
	}

	public void executeNextInstruction() {
		int pc = registerSet.getProgramCounter().getValue();

		//Fetch and Decode
		registerSet.getProgramCounter().setValue(pc + 4);
		CompiledInstruction instruction = getInstruction(pc);

		if (instruction == null)
			throw new InstructionNotFoundException("Couldn't decode instruction " + memory.getWord(pc) + ".");

		//Execute, Memory and Write
		instruction.execute(this);
	}
}
