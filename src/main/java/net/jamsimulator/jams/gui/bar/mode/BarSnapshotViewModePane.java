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

package net.jamsimulator.jams.gui.bar.mode;

import net.jamsimulator.jams.gui.bar.BarButton;
import net.jamsimulator.jams.gui.bar.BarSnapshot;
import net.jamsimulator.jams.gui.bar.BarSnapshotHolder;

import java.util.Optional;

/**
 * Represents a {@link BarSnapshot snapshot}'s representation mode where
 * its content is placed in a pane.
 */
public class BarSnapshotViewModePane implements BarSnapshotViewMode {

    public static final String NAME = "pane";
    public static final BarSnapshotViewModePane INSTANCE = new BarSnapshotViewModePane();

    private BarSnapshotViewModePane() {
    }

    @Override
    public Optional<BarSnapshotHolder> manageView(BarButton button) {
        return button.getBar().getBarPane().show(button) ? Optional.of(button.getBar().getBarPane()) : Optional.empty();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getLanguageNode() {
        return "BAR_VIEW_MODE_PANE";
    }
}
