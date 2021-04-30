package net.jamsimulator.jams.event.general;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Event;

/**
 * This event is called through the {@link Jams#getGeneralEventBroadcast() general event broadcast} when JAMS
 * is being shutdown.
 */
public class JAMSShutdownEvent extends Event {


    JAMSShutdownEvent() {
    }

    /**
     * This event is called before JAMS is shutdown.
     */
    public static class Before extends JAMSShutdownEvent {
    }

    /**
     * This event is called after JAMS is shutdown.
     */
    public static class After extends JAMSShutdownEvent {
    }

}
