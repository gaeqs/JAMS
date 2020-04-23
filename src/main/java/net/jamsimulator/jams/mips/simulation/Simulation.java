package net.jamsimulator.jams.mips.simulation;

import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.register.Registers;

import java.util.Optional;

public abstract class Simulation<Arch extends Architecture> {

	protected final Arch architecture;
	protected final InstructionSet instructionSet;

	protected final Registers registerSet;
	protected final Memory memory;

	public Simulation(Arch architecture, InstructionSet instructionSet, Registers registerSet, Memory memory) {
		this.architecture = architecture;
		this.instructionSet = instructionSet;
		this.registerSet = registerSet;
		this.memory = memory;
		memory.registerListeners(this);
	}

	public Arch getArchitecture() {
		return architecture;
	}

	public InstructionSet getInstructionSet() {
		return instructionSet;
	}

	public Registers getRegisterSet() {
		return registerSet;
	}

	public Memory getMemory() {
		return memory;
	}

	public AssembledInstruction fetch(int pc) {
		int data = memory.getWord(pc);
		Optional<BasicInstruction> optional = instructionSet.getInstructionByInstructionCode(data);
		if (!optional.isPresent()) return null;
		BasicInstruction instruction = optional.get();
		return instruction.compileFromCode(data);
	}

	public abstract void nextStep();

	public abstract void executeAll();

}
