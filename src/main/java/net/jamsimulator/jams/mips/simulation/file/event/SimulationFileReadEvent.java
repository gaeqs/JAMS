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
 * This event is called when data is read from a {@link SimulationFile}.
 */
public class SimulationFileReadEvent extends Event {

    protected SimulationFile file;
    protected int amount;

    /**
     * Creates the event.
     *
     * @param file   the {@link SimulationFile}.
     * @param amount the amount of bytes to read.
     */
    private SimulationFileReadEvent(SimulationFile file, int amount) {
        Validate.notNull(file, "File cannot be null!");
        this.file = file;
        this.amount = amount;
    }

    /**
     * Returns the {@link SimulationFile}.
     *
     * @return the {@link SimulationFile}.
     */
    public SimulationFile getFile() {
        return file;
    }

    /**
     * Returns the amount of bytes to read.
     *
     * @return the amount of bytes.
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Event called before reading the data.
     */
    public static class Before extends SimulationFileReadEvent implements Cancellable {

        private boolean cancelled;

        /**
         * Creates the event.
         *
         * @param file   the {@link SimulationFile}.
         * @param amount the amount of bytes to read.
         */
        public Before(SimulationFile file, int amount) {
            super(file, amount);
        }

        /**
         * Sets the amount of bytes to read.
         *
         * @param amount the amount of bytes.
         */
        public void setAmount(int amount) {
            this.amount = amount;
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
     * Event called after reading the data.
     */
    public static class After extends SimulationFileReadEvent {

        protected byte[] data;

        /**
         * Creates the event.
         *
         * @param file   the {@link SimulationFile}.
         * @param amount the amount of bytes to read.
         * @param data   the read data.
         */
        public After(SimulationFile file, int amount, byte[] data) {
            super(file, amount);
            Validate.notNull(data, "Data cannot be null!");
            this.data = data;
        }

        /**
         * Returns the data to write.
         * This data is modifiable.
         *
         * @return the data.
         */
        public byte[] getData() {
            return data;
        }

        /**
         * Sets the read data.
         *
         * @param data the data.
         */
        public void setData(byte[] data) {
            Validate.notNull(data, "Data cannot be null!");
            this.data = data;
        }
    }
}
