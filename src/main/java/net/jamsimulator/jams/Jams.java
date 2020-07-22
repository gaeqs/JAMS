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

package net.jamsimulator.jams;

import net.jamsimulator.jams.configuration.RootConfiguration;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.manager.*;
import net.jamsimulator.jams.utils.ConfigurationUtils;
import net.jamsimulator.jams.utils.FolderUtils;
import net.jamsimulator.jams.utils.TempUtils;

import java.io.File;

public class Jams {

	private static File mainFolder;

	private static RootConfiguration mainConfiguration;

	private static LanguageManager languageManager;
	private static FileTypeManager fileTypeManager;

	private static ArchitectureManager architectureManager;
	private static AssemblerBuilderManager assemblerBuilderManager;
	private static MemoryBuilderManager memoryBuilderManager;
	private static CacheBuilderManager cacheBuilderManager;
	private static RegistersBuilderManager registersBuilderManager;

	private static InstructionSetManager instructionSetManager;
	private static DirectiveSetManager directiveSetManager;
	private static SyscallExecutionBuilderManager syscallExecutionBuilderManager;

	//JAMS main method.
	public static void main(String[] args) {
		mainFolder = FolderUtils.checkMainFolder();
		TempUtils.loadTemporalFolder();

		mainConfiguration = ConfigurationUtils.loadMainConfiguration();

		languageManager = LanguageManager.INSTANCE;
		fileTypeManager = FileTypeManager.INSTANCE;

		architectureManager = ArchitectureManager.INSTANCE;
		assemblerBuilderManager = AssemblerBuilderManager.INSTANCE;
		memoryBuilderManager = MemoryBuilderManager.INSTANCE;
		cacheBuilderManager = CacheBuilderManager.INSTANCE;
		registersBuilderManager = RegistersBuilderManager.INSTANCE;

		instructionSetManager = InstructionSetManager.INSTANCE;
		directiveSetManager = DirectiveSetManager.INSTANCE;
		syscallExecutionBuilderManager = SyscallExecutionBuilderManager.INSTANCE;

		JamsApplication.main(args);
	}

	public static String getVersion() {
		return String.valueOf(Jams.class.getPackage().getImplementationVersion());
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
	 * Returns the {@link FileTypeManager}.
	 *
	 * @return the {@link FileTypeManager}.
	 */
	public static FileTypeManager getFileTypeManager() {
		return fileTypeManager;
	}

	/**
	 * Returns the {@link ArchitectureManager}.
	 *
	 * @return the {@link ArchitectureManager}.
	 */
	public static ArchitectureManager getArchitectureManager() {
		return architectureManager;
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

	/**
	 * Returns the {@link CacheBuilderManager}.
	 *
	 * @return the {@link CacheBuilderManager}.
	 */
	public static CacheBuilderManager getCacheBuilderManager() {
		return cacheBuilderManager;
	}

	/**
	 * Returns the {@link RegistersBuilderManager}.
	 *
	 * @return the {@link RegistersBuilderManager}.
	 */
	public static RegistersBuilderManager getRegistersBuilderManager() {
		return registersBuilderManager;
	}

	/**
	 * Returns the {@link InstructionSetManager}.
	 *
	 * @return the {@link InstructionSetManager}.
	 */
	public static InstructionSetManager getInstructionSetManager() {
		return instructionSetManager;
	}

	/**
	 * Returns the {@link DirectiveSetManager}.
	 *
	 * @return the {@link DirectiveSetManager}.
	 */
	public static DirectiveSetManager getDirectiveSetManager() {
		return directiveSetManager;
	}

	/**
	 * Returns the {@link SyscallExecutionBuilderManager}.
	 *
	 * @return the {@link SyscallExecutionBuilderManager}.
	 */
	public static SyscallExecutionBuilderManager getSyscallExecutionBuilderManager() {
		return syscallExecutionBuilderManager;
	}
}
