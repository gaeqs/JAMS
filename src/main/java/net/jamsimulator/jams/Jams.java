package net.jamsimulator.jams;

import net.jamsimulator.jams.configuration.RootConfiguration;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.manager.AssemblerBuilderManager;
import net.jamsimulator.jams.manager.LanguageManager;
import net.jamsimulator.jams.manager.MemoryBuilderManager;
import net.jamsimulator.jams.mips.assembler.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.utils.ConfigurationUtils;
import net.jamsimulator.jams.utils.FolderUtils;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;

public class Jams {

	private static File mainFolder;

	private static RootConfiguration mainConfiguration;

	private static LanguageManager languageManager;
	private static AssemblerBuilderManager assemblerBuilderManager;
	private static MemoryBuilderManager memoryBuilderManager;
	private static InstructionSet defaultInstructionSet;
	private static DirectiveSet defaultDirectiveSet;

	//JAMS main method.
	public static void main(String[] args) throws IOException, ParseException {
		mainFolder = FolderUtils.checkMainFolder();

		mainConfiguration = ConfigurationUtils.loadMainConfiguration();

		languageManager = LanguageManager.INSTANCE;
		defaultInstructionSet = new InstructionSet(true, true, true);
		defaultDirectiveSet = new DirectiveSet(true, true);
		assemblerBuilderManager = AssemblerBuilderManager.INSTANCE;
		memoryBuilderManager = MemoryBuilderManager.INSTANCE;
		JamsApplication.launch(JamsApplication.class, args);
	}

	/**
	 * Returns JAMS's main folder. This folder is used to store general data.
	 *
	 * @return JAMS's main folder.
	 */
	public static File getMainFolder() {
		return mainFolder;
	}

	/**
	 * Returns JAMS's main configuration.
	 *
	 * @return JAMS's main configuration.
	 */
	public static RootConfiguration getMainConfiguration() {
		return mainConfiguration;
	}

	/**
	 * Returns the {@link LanguageManager}.
	 *
	 * @return the {@link LanguageManager}.
	 */
	public static LanguageManager getLanguageManager() {
		return languageManager;
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

	/**
	 * Returns the {@link MemoryBuilderManager}.
	 *
	 * @return the {@link MemoryBuilderManager}.
	 */
	public static MemoryBuilderManager getMemoryBuilderManager() {
		return memoryBuilderManager;
	}
}
