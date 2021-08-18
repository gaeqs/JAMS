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

package net.jamsimulator.jams.gui.project.bottombar;

import javafx.animation.AnimationTimer;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.project.event.ProjectCloseEvent;
import net.jamsimulator.jams.project.Project;

public class ProjectTaskBarElement extends HBox implements ProjectBottomBarElement {

    public static final String NAME = "project_task_bar_element";
    public static final String STYLE_CLASS = "project-task-bar-element";

    private final Project project;

    private final Label label;
    private final ProgressBar bar;
    private final UpdateTimer timer;

    private Task<?> currentTask;

    public ProjectTaskBarElement(Project project) {
        getStyleClass().add(STYLE_CLASS);
        this.project = project;

        label = new Label();
        bar = new ProgressBar(-1);
        timer = new UpdateTimer();

        bar.setProgress(-1);
        bar.setVisible(false);

        getChildren().addAll(label, bar);
        JamsApplication.getProjectsTabPane().registerListeners(this, true);

        timer.start();
    }


    @Listener
    private void onProjectClose(ProjectCloseEvent event) {
        if (project.equals(event.getProject())) {
            timer.stop();
        }
    }

    @Override
    public ProjectBottomBarPosition getPosition() {
        return ProjectBottomBarPosition.RIGHT;
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE - 100;
    }

    @Override
    public Node asNode() {
        return this;
    }

    @Override
    public String getName() {
        return NAME;
    }


    private class UpdateTimer extends AnimationTimer {

        private long nextFrame = 0L;

        @Override
        public void handle(long now) {
            if (nextFrame > now) return;
            nextFrame = now + 100000000L;

            if (currentTask == null || currentTask.isDone()) {
                findNewTask();
            }
        }

        private void findNewTask() {
            currentTask = project.getTaskExecutor().getFirstTask().orElse(null);
            if (currentTask == null) {
                label.textProperty().unbind();
                label.setText(null);
                bar.progressProperty().unbind();
                bar.setProgress(-1);
                bar.setVisible(false);
            } else {
                label.textProperty().bind(currentTask.titleProperty());
                bar.setVisible(true);
                bar.progressProperty().bind(currentTask.progressProperty());
            }
        }
    }

}
