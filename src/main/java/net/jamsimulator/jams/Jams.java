package net.jamsimulator.jams;

import net.jamsimulator.jams.mips.instruction.set.InstructionSet;

public class Jams {

	private static InstructionSet defaultInstructionSet;

	//JAMS main method.
	public static void main(String[] args) {
		defaultInstructionSet = new InstructionSet(true, true);
	}


	/**
	 * Returns the default {@link InstructionSet}.
	 *
	 * @return the default {@link InstructionSet}.
	 * @see InstructionSet
	 */
	public static InstructionSet getDefaultInstructionSet() {
		return defaultInstructionSet;
	}
}
