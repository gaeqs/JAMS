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
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.project.event.ProjectCloseEvent;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.project.Project;
import task.JamsTask;

public class ProjectTaskBar extends HBox {

    private final Project project;

    private final LanguageLabel label;
    private final ProgressBar bar;
    private final UpdateTimer timer;

    private JamsTask currentTask;

    public ProjectTaskBar(Project project) {
        this.project = project;

        label = new LanguageLabel(null);
        bar = new ProgressBar(-1);
        timer = new UpdateTimer();

        bar.setProgress(-1);
        bar.setPrefWidth(150);
        bar.setPrefHeight(10);
        bar.setVisible(false);

        setAlignment(Pos.CENTER);
        setSpacing(5);
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


    private class UpdateTimer extends AnimationTimer {

        private long nextFrame = 0L;

        @Override
        public void handle(long now) {
            if (nextFrame > now) return;
            nextFrame = now + 100000000L;

            if (currentTask == null || currentTask.future().isDone()) {
                findNewTask();
            }
        }

        private void findNewTask() {
            currentTask = project.getTaskExecutor().getFirstTask().orElse(null);
            if (currentTask == null) {
                label.setNode(null);
                bar.progressProperty().unbind();
                bar.setProgress(-1);
                bar.setVisible(false);
            } else {
                label.setNode(currentTask.languageNode());
                bar.setVisible(true);
                if (currentTask.progress() == null) {
                    bar.progressProperty().unbind();
                    bar.setProgress(-1);
                } else {
                    bar.progressProperty().bind(currentTask.progress());
                }
            }
        }
    }

}
