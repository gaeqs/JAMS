/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.gui.mips.project;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.bar.BarPosition;
import net.jamsimulator.jams.gui.bar.BarSnapshot;
import net.jamsimulator.jams.gui.bar.mode.BarSnapshotViewModePane;
import net.jamsimulator.jams.gui.editor.FileEditorHolder;
import net.jamsimulator.jams.gui.editor.FileEditorHolderHolder;
import net.jamsimulator.jams.gui.image.icon.IconData;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.mips.sidebar.FilesToAssembleSidebar;
import net.jamsimulator.jams.gui.project.ProjectFolderExplorer;
import net.jamsimulator.jams.gui.project.ProjectTab;
import net.jamsimulator.jams.gui.project.WorkingPane;
import net.jamsimulator.jams.gui.util.PixelScrollPane;
import net.jamsimulator.jams.gui.util.log.SimpleLog;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.project.mips.MIPSProject;

import java.io.File;
import java.util.Set;

/**
 * This class represent the working pane of a project.
 */
public class MIPSStructurePane extends WorkingPane implements FileEditorHolderHolder {

    protected final MIPSProject project;
    protected final MIPSStructurePaneButtons paneButtons;

    protected ProjectFolderExplorer explorer;
    protected FilesToAssembleSidebar filesToAssembleSidebar;

    protected SimpleLog log;

    /**
     * Creates the mips project pane.
     *
     * @param parent     the {@link Tab} containing this pane.
     * @param projectTab the {@link ProjectTab} of the project.
     * @param project    the {@link MIPSProject} to handle.
     */
    public MIPSStructurePane(Tab parent, ProjectTab projectTab, MIPSProject project) {
        super(parent, projectTab, null, false);
        center = new FileEditorHolder(this);
        this.project = project;

        paneButtons = new MIPSStructurePaneButtons(this);

        init();

        loadExplorer();
        loadFilesToAssembleSidebar();
        loadLogBottomBar();

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

    @Override
    public FileEditorHolder getFileEditorHolder() {
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
        getFileEditorHolder().openFile(file);
    }

    private void loadExplorer() {
        ScrollPane pane = new PixelScrollPane();
        pane.setFitToHeight(true);
        pane.setFitToWidth(true);
        explorer = new ProjectFolderExplorer(project, Set.of(project.getData().getFilesToAssemble()), pane);
        pane.setContent(explorer);

        manageBarAddition("explorer", pane, Icons.SIDEBAR_EXPLORER, Messages.BAR_EXPLORER_NAME,
                BarPosition.LEFT_TOP, BarSnapshotViewModePane.INSTANCE, true);

        explorer.setFileOpenAction(file -> openFile(file.getFile()));
    }

    private void loadFilesToAssembleSidebar() {
        ScrollPane pane = new PixelScrollPane();
        pane.setFitToHeight(true);
        pane.setFitToWidth(true);
        filesToAssembleSidebar = new FilesToAssembleSidebar(project, project.getData().getFilesToAssemble(), pane);
        pane.setContent(filesToAssembleSidebar);
        manageBarAddition("files_to_assemble", pane, Icons.SIDEBAR_EXPLORER, Messages.BAR_FILES_TO_ASSEMBLE_NAME,
                BarPosition.LEFT_BOTTOM, BarSnapshotViewModePane.INSTANCE, true);
    }

    private void loadLogBottomBar() {
        log = new SimpleLog();
        manageBarAddition("log", log, Icons.FILE_FILE, Messages.BAR_LOG_NAME,
                BarPosition.BOTTOM_LEFT, BarSnapshotViewModePane.INSTANCE, true);
    }

    private void manageBarAddition(String name, Node node, IconData icon, String languageNode, BarPosition defaultPosition,
                                   BarSnapshotViewModePane defaultViewMode, boolean defaultEnable) {
        barMap.registerSnapshot(new BarSnapshot(name, node, defaultPosition, defaultViewMode, defaultEnable, icon, languageNode));
    }


    @Override
    public String getLanguageNode() {
        return Messages.PROJECT_TAB_STRUCTURE;
    }

    @Override
    public void populateHBox(HBox buttonsHBox) {
        buttonsHBox.getChildren().clear();
        buttonsHBox.getChildren().addAll(paneButtons.getNodes());
    }

    @Override
    public void saveAllOpenedFiles() {
        getFileEditorHolder().saveAll(true);
    }

    @Override
    public void onClose() {
        super.onClose();
        explorer.dispose();
    }
}
