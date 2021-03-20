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

package net.jamsimulator.jams.gui.bar.mode.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.gui.bar.mode.BarSnapshotViewMode;
import net.jamsimulator.jams.utils.Validate;

public class BarSnapshotViewModeRegisterEvent extends Event {

    protected BarSnapshotViewMode viewMode;

    BarSnapshotViewModeRegisterEvent(BarSnapshotViewMode viewMode) {
        Validate.notNull(viewMode, "View mode cannot be null!");
        this.viewMode = viewMode;
    }

    public BarSnapshotViewMode getViewMode() {
        return viewMode;
    }

    public static class Before extends BarSnapshotViewModeRegisterEvent implements Cancellable {

        private boolean cancelled;

        public Before(BarSnapshotViewMode viewMode) {
            super(viewMode);
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }
    }

    public static class After extends BarSnapshotViewModeRegisterEvent {

        public After(BarSnapshotViewMode viewMode) {
            super(viewMode);
        }

    }
}
