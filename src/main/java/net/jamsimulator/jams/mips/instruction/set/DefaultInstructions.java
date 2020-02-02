package net.jamsimulator.jams.mips.instruction.set;

import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAdd;
import net.jamsimulator.jams.mips.instruction.pseudo.PseudoInstruction;

import java.util.HashSet;
import java.util.Set;

class DefaultInstructions {

	static Set<BasicInstruction> basicInstructions = new HashSet<>();
	static Set<PseudoInstruction> pseudoInstructions = new HashSet<>();

	static {
		basicInstructions.add(new InstructionAdd());
	}

}
