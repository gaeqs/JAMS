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

package net.jamsimulator.jams.mips.simulation.file.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.mips.simulation.file.SimulationFile;
import net.jamsimulator.jams.utils.Validate;

/**
 * This event is called when a simulation file is closed.
 */
public class SimulationFileCloseEvent extends Event {

    protected SimulationFile file;

    /**
     * Creates the event.
     *
     * @param file the {@link SimulationFile} to close.
     */
    private SimulationFileCloseEvent(SimulationFile file) {
        Validate.notNull(file, "File cannot be null!");
        this.file = file;
    }

    /**
     * Returns the {@link SimulationFile} to open.
     *
     * @return the {@link SimulationFile}.
     */
    public SimulationFile getFile() {
        return file;
    }

    /**
     * Event called before closing a file.
     */
    public static class Before extends SimulationFileCloseEvent implements Cancellable {

        private boolean cancelled;

        /**
         * Creates the event.
         *
         * @param file the {@link SimulationFile} to close.
         */
        public Before(SimulationFile file) {
            super(file);
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

    /**
     * Event called after closing a file.
     */
    public static class After extends SimulationFileCloseEvent {


        /**
         * Creates the event.
         *
         * @param file the closed {@link SimulationFile}.
         */
        public After(SimulationFile file) {
            super(file);
        }
    }
}
