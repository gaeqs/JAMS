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
import net.jamsimulator.jams.gui.bar.BarType;
import net.jamsimulator.jams.gui.bar.PaneSnapshot;
import net.jamsimulator.jams.gui.editor.FileEditorHolder;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.mips.explorer.MipsFolderExplorer;
import net.jamsimulator.jams.gui.mips.sidebar.FilesToAssembleSidebar;
import net.jamsimulator.jams.gui.mips.sidebar.SimulationSidebar;
import net.jamsimulator.jams.gui.project.ProjectTab;
import net.jamsimulator.jams.gui.project.WorkingPane;
import net.jamsimulator.jams.gui.util.log.SimpleLog;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.project.mips.MIPSProject;

import java.io.File;
import java.util.HashSet;

/**
 * This class represent the working pane of a project.
 */
public class MipsStructurePane extends WorkingPane {

	protected final MIPSProject project;
	protected MipsFolderExplorer explorer;
	protected FilesToAssembleSidebar filesToAssembleSidebar;
	protected SimulationSidebar simulationSidebar;

	protected SimpleLog log;

	/**
	 * Creates the mips project pane.
	 *
	 * @param parent     the {@link Tab} containing this pane.
	 * @param projectTab the {@link ProjectTab} of the project.
	 * @param project    the {@link MIPSProject} to handle.
	 */
	public MipsStructurePane(Tab parent, ProjectTab projectTab, MIPSProject project) {
		super(parent, projectTab, null, new HashSet<>(), false);
		center = new FileEditorHolder(this);
		this.project = project;

		loadExplorer();
		loadFilesToAssembleSidebar();
		loadSimulationSidebar();
		loadLogBottomBar();

		init();

		SplitPane.setResizableWithParent(center, true);
	}

	/**
	 * Returns the {@link MIPSProject} handled by this pane.
	 *
	 * @return the {@link MIPSProject}.
	 */
	public MIPSProject getProject() {
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

	/**
	 * Returns the {@link SimpleLog} of this project.
	 *
	 * @return the {@link SimpleLog}.
	 */
	public SimpleLog getLog() {
		return log;
	}

	/**
	 * Opens the given {@link File}.
	 *
	 * @param file the {@link File}.
	 */
	public void openFile(File file) {
		getFileDisplayHolder().openFile(file);
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

		paneSnapshots.add(new PaneSnapshot("Explorer", BarType.TOP_LEFT,
				pane, explorerIcon, Messages.BAR_EXPLORER_NAME));

		explorer.setFileOpenAction(file -> openFile(file.getFile()));
	}

	private void loadFilesToAssembleSidebar() {
		Image explorerIcon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIDEBAR_EXPLORER,
				Icons.SIDEBAR_EXPLORER_PATH, 1024, 1024).orElse(null);

		ScrollPane pane = new ScrollPane();
		pane.setFitToHeight(true);
		pane.setFitToWidth(true);
		filesToAssembleSidebar = new FilesToAssembleSidebar(project, pane);
		pane.setContent(filesToAssembleSidebar);

		pane.getContent().addEventHandler(ScrollEvent.SCROLL, scrollEvent -> {
			double deltaY = scrollEvent.getDeltaY() * 0.003;
			pane.setVvalue(pane.getVvalue() - deltaY);
		});

		paneSnapshots.add(new PaneSnapshot("FilesToAssemble", BarType.BOTTOM_LEFT,
				pane, explorerIcon, Messages.BAR_FILES_TO_ASSEMBLE_NAME));
	}

	private void loadSimulationSidebar() {
		Image icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.PROJECT_ASSEMBLE_GRAY,
				Icons.PROJECT_ASSEMBLE_GRAY_PATH, 1024, 1024).orElse(null);
		ScrollPane pane = new ScrollPane();
		pane.setFitToWidth(true);
		pane.setFitToHeight(true);
		simulationSidebar = new SimulationSidebar(project);
		pane.setContent(simulationSidebar);
		paneSnapshots.add(new PaneSnapshot("Simulation", BarType.TOP_RIGHT,
				pane, icon, Messages.BAR_SIMULATION_NAME));
	}

	private void loadLogBottomBar() {
		Image icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.FILE_FILE,
				Icons.FILE_FILE_PATH, 1024, 1024).orElse(null);
		ScrollPane pane = new ScrollPane();
		pane.setFitToWidth(true);
		pane.setFitToHeight(true);

		log = new SimpleLog();
		pane.setContent(log);
		paneSnapshots.add(new PaneSnapshot("Log", BarType.BOTTOM, pane, icon, Messages.BAR_LOG_NAME));
	}

	@Override
	public String getLanguageNode() {
		return Messages.PROJECT_TAB_STRUCTURE;
	}

	@Override
	public void onClose() {
		super.onClose();
		explorer.dispose();
	}
}
