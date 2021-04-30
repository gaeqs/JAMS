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

package net.jamsimulator.jams.project.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.project.ProjectSnapshot;
import net.jamsimulator.jams.utils.Validate;

public class RecentProjectAddEvent extends Event {

    protected ProjectSnapshot snapshot;

    RecentProjectAddEvent(ProjectSnapshot snapshot) {
        Validate.notNull(snapshot, "Snapshot cannot be null!");
        this.snapshot = snapshot;
    }

    public ProjectSnapshot getSnapshot() {
        return snapshot;
    }

    public static class Before extends RecentProjectAddEvent implements Cancellable {

        private boolean cancelled;

        public Before(ProjectSnapshot snapshot) {
            super(snapshot);
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

    public static class After extends RecentProjectAddEvent {

        public After(ProjectSnapshot snapshot) {
            super(snapshot);
        }

    }
}
