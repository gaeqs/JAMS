package net.jamsimulator.jams.event.general;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Event;

/**
 * This event is called through the {@link Jams#getGeneralEventBroadcast() general event broadcast} after
 * JAMS is loaded.
 *
 * In this state the JavaFX application is not loaded yet!
 * Use {@link JAMSApplicationPostInitEvent} if you need the JavaFX application loaded.
 */
public class JAMSPostInitEvent extends Event {
}
