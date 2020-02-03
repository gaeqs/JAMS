package net.jamsimulator.jams;

import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Jams {

	private static InstructionSet defaultInstructionSet;

	//JAMS main method.
	public static void main(String[] args) throws IOException, ParseException {
		defaultInstructionSet = new InstructionSet(true, true, true);
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
