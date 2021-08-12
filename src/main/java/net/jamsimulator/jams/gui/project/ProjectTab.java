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
import net.jamsimulator.jams.event.EventBroadcast;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.event.file.FileEvent;
import net.jamsimulator.jams.event.file.FolderEventBroadcast;
import net.jamsimulator.jams.event.general.JAMSShutdownEvent;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.main.MainAnchorPane;
import net.jamsimulator.jams.gui.project.bottombar.ProjectBottomBar;
import net.jamsimulator.jams.gui.project.event.ProjectCloseEvent;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.project.Project;
import net.jamsimulator.jams.project.ProjectSnapshot;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

/**
 * Represents a folder project's tab. This must be used by {@link MainAnchorPane#getProjectListTabPane()}
 * <p>
 * This project tab give us the ability to listen file changes inside the project directory.
 * To listen a file, just implemente a listener to the {@link FileEvent}
 * event in your class and register it in the project tab of the desired {@link Project}.
 * <p>
 * When the project tab is closed, all file listeners will be unregistered from the tab.
 */
public class ProjectTab extends Tab implements EventBroadcast {

    private final Project project;
    private final ProjectTabPane projectTabPane;
    private final List<EventHandler<Event>> closeListeners;
    private final HBox buttonsHBox;
    private final ProjectBottomBar bottomBar;

    private final FolderEventBroadcast folderEventBroadcast;

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

        folderEventBroadcast = new FolderEventBroadcast(this);
        var folderEventBroadcastThread = new Thread(folderEventBroadcast::folderListenerProcessor);
        folderEventBroadcastThread.setDaemon(true);
        folderEventBroadcastThread.start();

        try {
            folderEventBroadcast.registerPathRecursively(project.getFolder().toPath()
            );
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Register the file event that manages the recursive addition.
        registerListeners(this, true);
        Jams.getGeneralEventBroadcast().registerListeners(this, true);

        AnchorPane pane = new AnchorPane();
        pane.getStyleClass().add("project-tab-anchor-pane");

        projectTabPane = new ProjectTabPane(this, (old, tab) -> {
            if (tab != null) {
                Platform.runLater(() -> {
                    Node node = tab.getContent();
                    if (tab.getContent() instanceof ProjectPane) {
                        ((ProjectPane) node).populateHBox(getButtonsHBox());
                    }

                    AnchorUtils.setAnchor(node, 28, 20, 0, 0);
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

        bottomBar = new ProjectBottomBar(project);
        bottomBar.setPrefHeight(20);
        bottomBar.setMaxHeight(20);
        AnchorUtils.setAnchor(bottomBar, -1, 0, 0, 0);
        pane.getChildren().add(bottomBar);

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
            try {
                folderEventBroadcast.kill();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
     * Returns the bar at the bottom of each project.
     *
     * @return the bar.
     */
    public ProjectBottomBar getBottomBar() {
        return bottomBar;
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

    @Override
    public boolean registerListener(Object instance, Method method, boolean useWeakReferences) {
        return folderEventBroadcast.registerListener(instance, method, useWeakReferences);
    }

    @Override
    public int registerListeners(Object instance, boolean useWeakReferences) {
        return folderEventBroadcast.registerListeners(instance, useWeakReferences);
    }

    @Override
    public boolean unregisterListener(Object instance, Method method) {
        return folderEventBroadcast.unregisterListener(instance, method);
    }

    @Override
    public int unregisterListeners(Object instance) {
        return folderEventBroadcast.unregisterListeners(instance);
    }

    @Override
    public <T extends net.jamsimulator.jams.event.Event> T callEvent(T event) {
        return folderEventBroadcast.callEvent(event, this);
    }

    @Listener(priority = Integer.MAX_VALUE)
    private void onFileAdd(FileEvent event) {
        if (event.getWatchEvent().kind() == ENTRY_CREATE) {
            try {
                if (Files.isDirectory(event.getPath())) {
                    // Register all child files of this directory to the folder event broadcast
                    folderEventBroadcast.registerPathRecursively(event.getPath()
                    );
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Listener
    private void onShutdown(JAMSShutdownEvent.Before event) {
        try {
            folderEventBroadcast.kill();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
