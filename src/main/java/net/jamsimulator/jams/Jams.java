package net.jamsimulator.jams;

import net.jamsimulator.jams.mips.instruction.manager.InstructionManager;

public class Jams {

	private static InstructionManager defaultInstructionManager;

	//JAMS main method.
	public static void main(String[] args) {
		defaultInstructionManager = new InstructionManager(true, true);
	}


	/**
	 * Returns the default {@link InstructionManager}.
	 *
	 * @return the default {@link InstructionManager}.
	 * @see InstructionManager
	 */
	public static InstructionManager getDefaultInstructionManager() {
		return defaultInstructionManager;
	}
}
