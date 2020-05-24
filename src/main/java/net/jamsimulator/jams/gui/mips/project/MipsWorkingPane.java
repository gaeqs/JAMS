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

package net.jamsimulator.jams.gui.mips.project;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.editor.FileEditorHolder;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.main.WorkingPane;
import net.jamsimulator.jams.gui.mips.explorer.MipsFolderExplorer;
import net.jamsimulator.jams.gui.mips.sidebar.FilesToAssembleDisplay;
import net.jamsimulator.jams.gui.project.ProjectTab;
import net.jamsimulator.jams.gui.sidebar.SidebarButton;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.project.mips.MipsProject;

import java.io.File;

/**
 * This class represent the working pane of a project.
 */
public class MipsWorkingPane extends WorkingPane {

	protected final MipsProject project;
	protected MipsFolderExplorer explorer;
	protected FilesToAssembleDisplay filesToAssembleDisplay;

	/**
	 * Creates the mips project pane.
	 *
	 * @param parent     the {@link Tab} containing this pane.
	 * @param projectTab the {@link ProjectTab} of the project.
	 * @param project    the {@link MipsProject} to handle.
	 */
	public MipsWorkingPane(Tab parent, ProjectTab projectTab, MipsProject project) {
		super(parent, projectTab, null, false);
		center = new FileEditorHolder(this);
		this.project = project;

		init();

		SplitPane.setResizableWithParent(center, true);

		loadExplorer();
		loadFilesToAssembleDisplay();

	}

	/**
	 * Returns the {@link MipsProject} handled by this pane.
	 *
	 * @return the {@link MipsProject}.
	 */
	public MipsProject getProject() {
		return project;
	}

	/**
	 * Returns the {@link FileEditorHolder} that handles files in this pane.
	 *
	 * @return the {@link FileEditorHolder}.
	 */
	public FileEditorHolder getFileDisplayHolder() {
		return (FileEditorHolder) center;
	}

	private void loadExplorer() {
		Image explorerIcon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIDEBAR_EXPLORER,
				Icons.SIDEBAR_EXPLORER_PATH, 1024, 1024).orElse(null);

		ScrollPane pane = new ScrollPane();
		pane.setFitToHeight(true);
		pane.setFitToWidth(true);
		explorer = new MipsFolderExplorer(project, pane);
		pane.setContent(explorer);

		pane.getContent().addEventHandler(ScrollEvent.SCROLL, scrollEvent -> {
			double deltaY = scrollEvent.getDeltaY() * 0.003;
			pane.setVvalue(pane.getVvalue() - deltaY);
		});


		//explorer.prefWidthProperty().bind(pane.widthProperty().subtract(2));
		topLeftSidebar.addNode("Explorer", pane, explorerIcon, Messages.EXPLORER_NAME);

		explorer.setFileOpenAction(file -> openFile(file.getFile()));
	}

	private void loadFilesToAssembleDisplay() {
		Image explorerIcon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIDEBAR_EXPLORER,
				Icons.SIDEBAR_EXPLORER_PATH, SidebarButton.IMAGE_SIZE, SidebarButton.IMAGE_SIZE).orElse(null);

		ScrollPane pane = new ScrollPane();
		pane.setFitToHeight(true);
		pane.setFitToWidth(true);
		filesToAssembleDisplay = new FilesToAssembleDisplay(project, pane);
		pane.setContent(filesToAssembleDisplay);

		pane.getContent().addEventHandler(ScrollEvent.SCROLL, scrollEvent -> {
			double deltaY = scrollEvent.getDeltaY() * 0.003;
			pane.setVvalue(pane.getVvalue() - deltaY);
		});

		bottomLeftSidebar.addNode("FilesToAssemble", pane, explorerIcon, Messages.FILES_TO_ASSEMBLE_NAME);
	}

	@Override
	public void onClose() {
		explorer.dispose();
	}

	@Override
	public void openFile(File file) {
		getFileDisplayHolder().openFile(file);
	}
}
