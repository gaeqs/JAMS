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

package net.jamsimulator.jams.gui.project;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.main.MainAnchorPane;
import net.jamsimulator.jams.gui.project.event.ProjectCloseEvent;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.project.Project;
import net.jamsimulator.jams.project.ProjectSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a folder project's tab. This must be used by {@link MainAnchorPane#getProjectListTabPane()}
 */
public class ProjectTab extends Tab {

    private final Project project;
    private final ProjectTabPane projectTabPane;
    private final List<EventHandler<Event>> closeListeners;
    private final HBox buttonsHBox;

    /**
     * Creates the folder project's tab.
     *
     * @param project the handled project.
     */
    public ProjectTab(Project project) {
        super(project.getName());
        getStyleClass().add("project-tab");
        setClosable(true);
        this.project = project;
        closeListeners = new ArrayList<>();

        AnchorPane pane = new AnchorPane();
        pane.getStyleClass().add("project-tab-anchor-pane");

        projectTabPane = new ProjectTabPane(this, (old, tab) -> {
            if (tab != null) {
                Platform.runLater(() -> {
                    Node node = tab.getContent();
                    if (tab.getContent() instanceof ProjectPane) {
                        ((ProjectPane) node).populateHBox(getButtonsHBox());
                    }

                    AnchorUtils.setAnchor(node, 28, 0, 0, 0);
                    if (!pane.getChildren().contains(node)) {
                        pane.getChildren().add(node);
                    } else {
                        node.toFront();
                        node.setVisible(true);
                    }
                });
            }
            if (old != null && old != tab) {
                old.getContent().setVisible(false);
            }
        }, tab -> pane.getChildren().remove(tab.getContent()));

        AnchorUtils.setAnchor(projectTabPane, 0, 0, 0, 400);
        pane.getChildren().add(projectTabPane);

        buttonsHBox = new HBox();
        AnchorUtils.setAnchor(buttonsHBox, 0, -1, -1, 0);
        buttonsHBox.getStyleClass().add("buttons-hbox");
        buttonsHBox.setAlignment(Pos.CENTER_RIGHT);
        buttonsHBox.setSpacing(2);
        buttonsHBox.setPrefHeight(28);
        buttonsHBox.setPrefWidth(400);
        pane.getChildren().add(buttonsHBox);

        setContent(pane);

        setOnClosed(event -> {
            closeListeners.forEach(target -> target.handle(event));
            project.assignProjectTab(null);
            project.onClose();
            Jams.getRecentProjects().add(ProjectSnapshot.of(project));
            JamsApplication.getProjectsTabPane().callEvent(new ProjectCloseEvent(project));
        });
    }

    /**
     * Returns the project handled by this tab-
     *
     * @return the project.
     */
    public Project getProject() {
        return project;
    }

    /**
     * Returns the {@link TabPane} of this project tab.
     *
     * @return the {@link TabPane}.
     */
    public ProjectTabPane getProjectTabPane() {
        return projectTabPane;
    }

    /**
     * Returns the HBox containing some pane buttons.
     * <p>
     * This HBox is shown at the right of the project tab pane.
     *
     * @return the HBox.
     */
    public HBox getButtonsHBox() {
        return buttonsHBox;
    }

    /**
     * Adds a listener that will be invoked when the tab is closed.
     *
     * @param listener the listener.
     */
    public void addTabCloseListener(EventHandler<Event> listener) {
        closeListeners.add(listener);
    }


    /**
     * Removed a listener that would be invoked when the tab is closed.
     *
     * @param listener the listener.
     */
    public void removeStageCloseListener(EventHandler<Event> listener) {
        closeListeners.remove(listener);
    }
}
