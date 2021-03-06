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

package net.jamsimulator.jams.gui.mips.explorer;

import javafx.scene.control.ScrollPane;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.explorer.event.ExplorerAddElementEvent;
import net.jamsimulator.jams.gui.explorer.event.ExplorerRemoveElementEvent;
import net.jamsimulator.jams.gui.explorer.folder.ExplorerFile;
import net.jamsimulator.jams.gui.explorer.folder.FolderExplorer;
import net.jamsimulator.jams.project.mips.MIPSProject;
import net.jamsimulator.jams.project.mips.event.FileAddToAssembleEvent;
import net.jamsimulator.jams.project.mips.event.FileRemoveFromAssembleEvent;

import java.io.File;
import java.util.Optional;

public class MipsFolderExplorer extends FolderExplorer {

	public static final String EXPLORER_FILE_TO_ASSEMBLE_STYLE_CLASS = "mips-explorer-file-to-assemble";

	private final MIPSProject project;

	/**
	 * Creates the mips explorer folder.
	 *
	 * @param project    the {@link MIPSProject} of this explorer.
	 * @param scrollPane the {@link ScrollPane} handling this explorer.
	 */
	public MipsFolderExplorer(MIPSProject project, ScrollPane scrollPane) {
		super(project.getFolder(), scrollPane);
		this.project = project;
		project.getData().getFilesToAssemble().registerListeners(this, true);
		for (File file : project.getData().getFilesToAssemble().getFiles()) {
			markFileToAssemble(file);
		}

		getExplorerFolder(project.getData().getFolder()).ifPresent(metadataFolder ->
				metadataFolder.getRepresentation().getStyleClass().add("explorer-jams-folder"));

		registerListeners(this, true);
	}

	/**
	 * Returns the {@link MIPSProject} of this explorer.
	 *
	 * @return the {@link MIPSProject}.
	 */
	public MIPSProject getProject() {
		return project;
	}

	/**
	 * Disposes this project. This method must be called when this explorer is no longer needed.
	 */
	public void dispose() {
		killWatchers();
		project.getData().getFilesToAssemble().unregisterListeners(this);
	}

	private void markFileToAssemble(File file) {
		Optional<ExplorerFile> optional = getExplorerFile(file);
		if (!optional.isPresent()) return;
		ExplorerFile explorerFile = optional.get();

		if (!explorerFile.getStyleClass().contains(EXPLORER_FILE_TO_ASSEMBLE_STYLE_CLASS)) {
			explorerFile.getStyleClass().add(EXPLORER_FILE_TO_ASSEMBLE_STYLE_CLASS);
		}
	}

	private void unmarkFileToAssemble(File file) {
		Optional<ExplorerFile> optional = getExplorerFile(file);
		if (!optional.isPresent()) return;
		ExplorerFile explorerFile = optional.get();

		explorerFile.getStyleClass().remove(EXPLORER_FILE_TO_ASSEMBLE_STYLE_CLASS);
	}

	@Listener
	private void onFileAddedToAssemble(FileAddToAssembleEvent.After event) {
		File file = event.getFile();
		markFileToAssemble(file);
	}

	@Listener
	private void onFileRemoveFromAssemble(FileRemoveFromAssembleEvent.After event) {
		File file = event.getFile();
		unmarkFileToAssemble(file);
	}

	@Listener
	private void onFileAdded(ExplorerAddElementEvent.After event) {
		if (event.getElement() instanceof ExplorerFile) {
			ExplorerFile eFile = (ExplorerFile) event.getElement();
			File file = eFile.getFile();

			if (project.getData().getFilesToAssemble().getFiles().contains(file)) {
				if (!eFile.getStyleClass().contains(EXPLORER_FILE_TO_ASSEMBLE_STYLE_CLASS)) {
					eFile.getStyleClass().add(EXPLORER_FILE_TO_ASSEMBLE_STYLE_CLASS);
				}
			}
		}
	}

	@Listener
	private void onFileRemoved (ExplorerRemoveElementEvent.After event) {
		project.getData().getFilesToAssemble().checkFiles();
	}
}
