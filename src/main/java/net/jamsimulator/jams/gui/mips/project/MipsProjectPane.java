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
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.display.FileDisplayList;
import net.jamsimulator.jams.gui.explorer.folder.FolderExplorer;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.main.WorkingPane;
import net.jamsimulator.jams.gui.project.ProjectTab;
import net.jamsimulator.jams.gui.sidebar.SidebarButton;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.project.MipsProject;

import java.io.File;

/**
 * This class represent the working pane of a project.
 */
public class MipsProjectPane extends WorkingPane {

	protected final MipsProject project;
	protected FolderExplorer explorer;

	/**
	 * Creates the mips project pane.
	 *
	 * @param parent     the {@link Tab} containing this pane.
	 * @param projectTab the {@link ProjectTab} of the project.
	 * @param project    the {@link MipsProject} to handle.
	 */
	public MipsProjectPane(Tab parent, ProjectTab projectTab, MipsProject project) {
		super(parent, projectTab, new FileDisplayList(null));
		this.project = project;

		getFileDisplayList().setWorkingPane(this);
		loadSidebarModules();
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
	 * Returns the {@link FileDisplayList} that handles files in this pane.
	 *
	 * @return the {@link FileDisplayList}.
	 */
	public FileDisplayList getFileDisplayList() {
		return (FileDisplayList) center;
	}

	private void loadSidebarModules() {
		Image explorerIcon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIDEBAR_EXPLORER,
				Icons.SIDEBAR_EXPLORER_PATH, SidebarButton.IMAGE_SIZE, SidebarButton.IMAGE_SIZE).orElse(null);

		ScrollPane pane = new ScrollPane();
		explorer = new FolderExplorer(project.getFolder(), pane);
		pane.setContent(explorer);
		pane.setFitToHeight(true);
		pane.setFitToWidth(true);

		pane.getContent().addEventHandler(ScrollEvent.SCROLL, scrollEvent -> {
			double deltaY = scrollEvent.getDeltaY() * 0.003;
			pane.setVvalue(pane.getVvalue() - deltaY);
		});


		//explorer.prefWidthProperty().bind(pane.widthProperty().subtract(2));
		topLeftSidebar.addNode("Explorer", pane, explorerIcon, Messages.EXPLORER_NAME);

		explorer.setFileOpenAction(file -> openFile(file.getFile()));
	}

	@Override
	public void onClose() {
		explorer.killWatchers();
	}

	@Override
	public void openFile(File file) {
		getFileDisplayList().openFile(file);
	}
}
