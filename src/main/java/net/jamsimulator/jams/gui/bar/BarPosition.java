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

package net.jamsimulator.jams.gui.bar;

import javafx.geometry.Orientation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

/**
 * Represets the position where a {@link Bar} is located at.
 */
public enum BarPosition {

    TOP_LEFT(Orientation.HORIZONTAL, true),
    TOP_RIGHT(Orientation.HORIZONTAL, false),
    BOTTOM_LEFT(Orientation.HORIZONTAL, true),
    BOTTOM_RIGHT(Orientation.HORIZONTAL, false),
    LEFT_TOP(Orientation.VERTICAL, true),
    LEFT_BOTTOM(Orientation.VERTICAL, false),
    RIGHT_TOP(Orientation.VERTICAL, true),
    RIGHT_BOTTOM(Orientation.VERTICAL, false);

    private final Orientation orientation;
    private final boolean firstPane;
    private final String[] styleClasses;

    BarPosition(Orientation orientation, boolean firstPane) {
        this.orientation = orientation;
        this.firstPane = firstPane;
        this.styleClasses = new String[]{"bar-" + orientation.name().toLowerCase(Locale.ROOT),
                "bar-" + name().toLowerCase(Locale.ROOT).replace("_", "-")};
    }

    /**
     * Returns the {@link Orientation} {@link Bar}s with this position should be displayed.
     *
     * @return the {@link Orientation}.
     */
    public Orientation getOrientation() {
        return orientation;
    }

    /**
     * Returns whether panes of {@link Bar}s with this position should be displayed
     * at the start or at the end of a {@link javafx.scene.control.SplitPane}.
     *
     * @return whether panes of {@link Bar}s with this position should be displayed
     * at the start or at the end of a {@link javafx.scene.control.SplitPane}.
     */
    public boolean shouldUseFirstPane() {
        return firstPane;
    }

    /**
     * Adds all position related style classes to the {@link Bar} holding the given {@link Collection}.
     *
     * @param classes the {@link Collection} with the classes.
     */
    public void addStyleClasses(Collection<String> classes) {
        classes.addAll(Arrays.asList(styleClasses));
    }
}
