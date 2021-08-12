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
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.project.event.ProjectCloseEvent;
import net.jamsimulator.jams.project.Project;

public class JamsMemoryBar extends ProgressBar {

    private final Project project;

    private final UpdateTimer timer;
    private final Tooltip tooltip;

    public JamsMemoryBar(Project project) {
        this.project = project;
        tooltip = new Tooltip();
        setTooltip(tooltip);

        setPrefWidth(75);
        JamsApplication.getProjectsTabPane().registerListeners(this, true);

        timer = new UpdateTimer();
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

            var total = Runtime.getRuntime().totalMemory();
            var free = Runtime.getRuntime().freeMemory();
            var used = total - free;

            setProgress(used / (double) total);
            tooltip.setText(used / 1048576 + " MiB / " + total / 1048576 +" MiB");
        }
    }

}
