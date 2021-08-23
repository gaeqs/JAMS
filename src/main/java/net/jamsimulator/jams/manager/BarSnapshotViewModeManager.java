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

package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.gui.bar.mode.BarSnapshotViewMode;
import net.jamsimulator.jams.gui.bar.mode.BarSnapshotViewModePane;
import net.jamsimulator.jams.gui.bar.mode.BarSnapshotViewModePersistentWindow;
import net.jamsimulator.jams.gui.bar.mode.BarSnapshotViewModeWindow;

/**
 * This singleton stores all {@link BarSnapshotViewMode}s that projects may use.
 * <p>
 * To register an {@link BarSnapshotViewMode} use {@link #add(Object)}.
 * To unregister an {@link BarSnapshotViewMode} use {@link #remove(Object)}.
 * An {@link BarSnapshotViewMode}'s removal from the manager doesn't make editors to stop using
 * it inmediatelly.
 */
public final class BarSnapshotViewModeManager extends Manager<BarSnapshotViewMode> {

    public static final BarSnapshotViewModeManager INSTANCE = new BarSnapshotViewModeManager();

    private BarSnapshotViewModeManager() {
        super(BarSnapshotViewMode.class);
    }

    @Override
    protected void loadDefaultElements() {
        add(BarSnapshotViewModePane.INSTANCE);
        add(BarSnapshotViewModeWindow.INSTANCE);
        add(BarSnapshotViewModePersistentWindow.INSTANCE);
    }
}
