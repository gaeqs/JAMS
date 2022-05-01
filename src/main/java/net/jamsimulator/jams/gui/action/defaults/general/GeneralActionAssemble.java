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

package net.jamsimulator.jams.gui.action.defaults.general;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.action.context.ContextAction;
import net.jamsimulator.jams.gui.action.context.MainMenuRegion;
import net.jamsimulator.jams.gui.bar.BarButton;
import net.jamsimulator.jams.gui.editor.code.CodeFileEditor;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.main.MainMenuBar;
import net.jamsimulator.jams.gui.project.ProjectTab;
import net.jamsimulator.jams.gui.util.log.Log;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.project.Project;
import net.jamsimulator.jams.task.LanguageTask;

public class GeneralActionAssemble extends ContextAction {

    public static final String NAME = "GENERAL_ASSEMBLE";
    public static final KeyCombination DEFAULT_COMBINATION = new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN, KeyCombination.ALT_DOWN);

    public GeneralActionAssemble(ResourceProvider provider) {
        super(provider, NAME, RegionTags.GENERAL, Messages.ACTION_GENERAL_ASSEMBLE, DEFAULT_COMBINATION,
                GeneralActionRegions.MIPS_PRIORITY, MainMenuRegion.SIMULATION, Icons.PROJECT_ASSEMBLE);
    }

    public static void compileAndShow(Project project) {
        ProjectTab tab = project.getProjectTab().orElse(null);
        if (tab == null) return;

        project.getTaskExecutor().execute(LanguageTask.of(Messages.TASK_ASSEMBLING, () -> {
            var pane = tab.getProjectTabPane().getWorkingPane();
            pane.saveAllOpenedFiles();

            if (Jams.getMainConfiguration().data().getOrElse("simulation.open_log_on_assemble", true)) {
                Platform.runLater(() -> pane.getBarMap().searchButton("log").ifPresent(BarButton::show));
            }
            var log = pane.getBarMap().getSnapshotNodeOfType(Log.class);

            try {
                project.generateSimulation(log.orElse(null));
            } catch (Exception ex) {
                if (log.isPresent()) {
                    log.get().printErrorLn("ERROR:");
                    log.get().printErrorLn(ex.getMessage());
                }
                ex.printStackTrace();
            }
        }));
    }

    @Override
    public boolean run(Object node) {
        runFromMenu();
        return true;
    }

    @Override
    public void runFromMenu() {
        var optionalProject = JamsApplication.getProjectsTabPane().getFocusedProject();
        if (optionalProject.isEmpty()) return;
        compileAndShow(optionalProject.get().getProject());
    }

    @Override
    public boolean supportsExplorerState(Explorer explorer) {
        return false;
    }

    @Override
    public boolean supportsTextEditorState(CodeFileEditor editor) {
        return false;
    }

    @Override
    public boolean supportsMainMenuState(MainMenuBar bar) {
        return JamsApplication.getProjectsTabPane().getFocusedProject().isPresent();
    }
}
