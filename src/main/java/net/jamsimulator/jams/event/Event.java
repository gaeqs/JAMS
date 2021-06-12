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

package net.jamsimulator.jams.event;

/**
 * Represents an event. Events are used by {@link EventBroadcast}s to
 * send notifications to {@link Listener} methods.
 * <p>
 * You can create you own event creating a child class of {@link Event}.
 * To listen your event, create a method with the event as a parameter
 * and a {@link Listener} annotation. This method can be privated or static.
 * Static listeners are not recommended and they will still require an instance
 * of their class to work as a holder.
 * <p>
 * Events can be send through a {@link EventBroadcast} using the method
 * {@link EventBroadcast#callEvent(Event)}.
 * <p>
 * The value {@link #getCaller() caller} holds the last {@link EventBroadcast}
 * this event was sent through.
 */
public class Event {

    private EventBroadcast caller;

    /**
     * Creates an event.
     * Send it to listeners through {@link EventBroadcast#callEvent(Event)}.
     */
    public Event() {
    }

    /**
     * Returns the {@link EventBroadcast} the event
     * was sent through.
     *
     * @return the {@link EventBroadcast}.
     */
    public EventBroadcast getCaller() {
        return caller;
    }

    /**
     * Sets the {@link EventBroadcast} the event was sent through.
     *
     * @param caller the {@link EventBroadcast}
     */
    void setCaller(EventBroadcast caller) {
        this.caller = caller;
    }
}
