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

package net.jamsimulator.jams.configuration.event;

import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.utils.Validate;

import java.util.Optional;

/**
 * This event is invoked when a node changes in a {@link Configuration}.
 */
public class ConfigurationNodeChangeEvent extends Event {

    protected final Object oldValue;
    protected Configuration configuration;
    protected String node;
    protected Object newValue;

    private ConfigurationNodeChangeEvent(Configuration configuration, String node, Object oldValue, Object newValue) {
        Validate.isTrue(node != null && !node.isEmpty() && !node.startsWith(".") && !node.endsWith("."), "Illegal node " + node + "!");
        this.configuration = configuration;
        this.node = node;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getNode() {
        return node;
    }

    public Optional<Object> getOldValue() {
        return Optional.ofNullable(oldValue);
    }

    public <T> Optional<T> getOldValueAs() {
        try {
            return Optional.ofNullable((T) oldValue);
        } catch (ClassCastException ex) {
            return Optional.empty();
        }
    }

    public Optional<Object> getNewValue() {
        return Optional.ofNullable(newValue);
    }

    public <T> Optional<T> getNewValueAs() {
        try {
            return Optional.ofNullable((T) newValue);
        } catch (ClassCastException ex) {
            return Optional.empty();
        }
    }

    public static class Before extends ConfigurationNodeChangeEvent implements Cancellable {

        private boolean cancelled;

        public Before(Configuration configuration, String node, Object oldValue, Object newValue) {
            super(configuration, node, oldValue, newValue);
        }

        public void setNode(String node) {
            Validate.isTrue(node != null && !node.isEmpty() && !node.startsWith(".") && !node.endsWith("."), "Illegal node " + node + "!");
            this.node = node;
        }

        public void setNewValue(Object newValue) {
            this.newValue = newValue;
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

    public static class After extends ConfigurationNodeChangeEvent {

        public After(Configuration configuration, String node, Object oldValue, Object newValue) {
            super(configuration, node, oldValue, newValue);
        }

    }
}
