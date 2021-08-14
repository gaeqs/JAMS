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
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.project.event.ProjectCloseEvent;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.language.wrapper.LanguageTooltip;
import net.jamsimulator.jams.project.Project;

public class JamsMemoryBarElement extends AnchorPane implements ProjectBottomBarElement {

    public static final String NAME = "jams_memory_bar_element";
    public static final String STYLE_CLASS = "jams-memory-bar-element";

    private final Project project;

    private final UpdateTimer timer;

    private final ProgressBar bar;
    private final LanguageLabel label;

    private final String[] replacements = new String[]{"{USED}", "0", "{TOTAL}", "0"};

    public JamsMemoryBarElement(Project project) {
        getStyleClass().add(STYLE_CLASS);
        this.project = project;

        bar = new ProgressBar();
        label = new LanguageLabel(Messages.BOTTOM_BAR_MEMORY, replacements);
        label.setTooltip(new LanguageTooltip(Messages.BOTTOM_BAR_MEMORY_TOOLTIP));
        label.setOnMouseClicked(event -> {
            System.gc();
            event.consume();
        });

        AnchorUtils.setAnchor(bar, 0, 0, 0, 0);
        AnchorUtils.setAnchor(label, 0, 0, 0, 0);
        getChildren().addAll(bar, label);

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

    @Override
    public ProjectBottomBarPosition getPosition() {
        return ProjectBottomBarPosition.RIGHT;
    }

    @Override
    public int getPriority() {
        return 0;
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

            var total = Runtime.getRuntime().totalMemory();
            var free = Runtime.getRuntime().freeMemory();
            var used = total - free;

            bar.setProgress(used / (double) total);

            replacements[1] = String.valueOf(used / 1048576);
            replacements[3] = String.valueOf(total / 1048576);
            label.refreshMessage();
        }
    }

}
