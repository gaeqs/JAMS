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

package net.jamsimulator.jams.gui.mips.inspection.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspectionBuilder;

public class MIPSEditorBuilderUnregisterEvent extends Event {

    protected MIPSEditorInspectionBuilder<?> builder;

    MIPSEditorBuilderUnregisterEvent(MIPSEditorInspectionBuilder<?> builder) {
        this.builder = builder;
    }

    public MIPSEditorInspectionBuilder<?> getBuilder() {
        return builder;
    }

    public static class Before extends MIPSEditorBuilderUnregisterEvent implements Cancellable {

        private boolean cancelled;

        public Before(MIPSEditorInspectionBuilder<?> builder) {
            super(builder);
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

    public static class After extends MIPSEditorBuilderUnregisterEvent {

        public After(MIPSEditorInspectionBuilder<?> builder) {
            super(builder);
        }

    }
}
