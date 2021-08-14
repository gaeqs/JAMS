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

import javafx.scene.Node;
import javafx.scene.control.Label;
import net.jamsimulator.jams.gui.project.ProjectTab;
import net.jamsimulator.jams.gui.util.log.Log;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class ProjectLastLogBarElement extends Label implements ProjectBottomBarElement {

    public static final String NAME = "project_last_log_bar_element";
    public static final String STYLE_CLASS = "project-last-log-bar-element";

    public ProjectLastLogBarElement(ProjectTab tab) {
        getStyleClass().add(STYLE_CLASS);

        var log = tab.getProjectTabPane().getWorkingPane()
                .getBarMap().getSnapshotNodeOfType(Log.class).orElse(null);
        if (log == null) return;


        log.lastLineProperty().addListener((obs, old, val) -> {
            if (val.isEmpty()) {
                setText("");
                return;
            }
            var time = log.lastLineTimeProperty().get().truncatedTo(ChronoUnit.MINUTES);
            var formattedTime = time.format(DateTimeFormatter.ofPattern("HH:mm"));
            setText(val + " (" + formattedTime + ")");
        });
    }

    @Override
    public ProjectBottomBarPosition getPosition() {
        return ProjectBottomBarPosition.LEFT;
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
}
