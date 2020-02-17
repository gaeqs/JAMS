package net.jamsimulator.jams;

import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.manager.AssemblerBuilderManager;
import net.jamsimulator.jams.mips.assembler.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Jams {

	private static AssemblerBuilderManager assemblerBuilderManager;
	private static InstructionSet defaultInstructionSet;
	private static DirectiveSet defaultDirectiveSet;

	//JAMS main method.
	public static void main(String[] args) throws IOException, ParseException {
		defaultInstructionSet = new InstructionSet(true, true, true);
		defaultDirectiveSet = new DirectiveSet(true, true);
		assemblerBuilderManager = AssemblerBuilderManager.INSTANCE;
		JamsApplication.launch(JamsApplication.class, args);
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


	/**
	 * Returns the default {@link DirectiveSet}.
	 *
	 * @return the default {@link DirectiveSet}.
	 * @see DirectiveSet
	 */
	public static DirectiveSet getDefaultDirectiveSet() {
		return defaultDirectiveSet;
	}

	/**
	 * Return the {@link AssemblerBuilderManager}.
	 *
	 * @return the {@link AssemblerBuilderManager}.
	 * @see AssemblerBuilderManager
	 */
	public static AssemblerBuilderManager getAssemblerBuilderManager() {
		return assemblerBuilderManager;
	}
}
