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

package net.jamsimulator.jams.gui.start;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.image.quality.QualityImageView;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.gui.util.PixelScrollPane;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageButton;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.project.ProjectTypeManager;
import net.jamsimulator.jams.project.ProjectSnapshot;
import net.jamsimulator.jams.project.event.RecentProjectAddEvent;
import net.jamsimulator.jams.project.mips.MIPSProjectType;

import java.io.File;

public class StartWindowSectionProjects extends AnchorPane implements StartWindowSection {

    private final StartWindow window;
    private VBox projects;

    public StartWindowSectionProjects(StartWindow window) {
        this.window = window;
        loadButtons();
        loadRecentProjects();

        Jams.getRecentProjects().registerListeners(this, true);
    }

    @Override
    public String getLanguageNode() {
        return Messages.START_PROJECTS;
    }

    @Override
    public Node toNode() {
        return this;
    }

    @Override
    public String getName() {
        return "projects";
    }

    private void loadButtons() {
        var openButton = new LanguageButton(Messages.ACTION_GENERAL_OPEN_PROJECT);

        openButton.getStyleClass().add("button-light");

        openButton.setOnAction(event -> {
            var chooser = new DirectoryChooser();
            var folder = chooser.showDialog(JamsApplication.getStage());
            if (folder == null || JamsApplication.getProjectsTabPane().isProjectOpen(folder)) return;

            var type = Manager.get(ProjectTypeManager.class).getByProjectfolder(folder).orElse(MIPSProjectType.INSTANCE);
            JamsApplication.getProjectsTabPane().openProject(type.loadProject(folder));
            window.getStage().hide();
        });

        var hbox = new HBox(openButton);
        hbox.getStyleClass().add("start-window-project-buttons-box");

        AnchorUtils.setAnchor(hbox, 0, -1, 0, 0);
        getChildren().add(hbox);
    }

    private void loadRecentProjects() {
        projects = new VBox();
        var scrollpane = new PixelScrollPane(projects);
        scrollpane.setFitToWidth(true);
        scrollpane.setFitToHeight(true);

        AnchorUtils.setAnchor(scrollpane, 50, 0, 0, 0);
        getChildren().add(scrollpane);
        refeshProjects();
    }

    private void refeshProjects() {
        projects.getChildren().clear();
        Jams.getRecentProjects().forEach(snapshot -> {
            var file = new File(snapshot.path());
            if (!file.isDirectory()) return;
            projects.getChildren().add(new RecentProject(snapshot));
        });
    }

    @Listener
    private void onRecentProjectAdd(RecentProjectAddEvent.After event) {
        // We refresh instead of adding it because it can be an already present
        // project that is being moved to the top of the list.
        refeshProjects();
    }


    private class RecentProject extends HBox {

        public RecentProject(ProjectSnapshot snapshot) {
            getStyleClass().add("start-window-project-entry");

            var title = new Label(snapshot.name());
            var path = new Label(snapshot.path());

            title.getStyleClass().add("title");
            path.getStyleClass().add("path");

            var type = Manager.get(ProjectTypeManager.class).getByProjectfolder(new File(snapshot.path()));
            if (type.isPresent() && type.get().getIcon().isPresent()) {
                var image = new QualityImageView(type.get().getIcon().get(), 40, 40);
                getChildren().addAll(image, new VBox(title, path));
            } else {
                getChildren().addAll(title, new VBox(title, path));
            }

            setOnMouseClicked(event -> {
                var folder = new File(snapshot.path());
                if (!folder.isDirectory()) {
                    projects.getChildren().remove(this);
                    return;
                }

                JamsApplication.getProjectsTabPane().openProject(type.orElseThrow().loadProject(folder));
                window.getStage().hide();
            });
        }

    }
}
