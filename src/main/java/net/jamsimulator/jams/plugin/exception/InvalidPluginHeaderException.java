package net.jamsimulator.jams.plugin.exception;

/**
 * This exception is thrown when a plugin's header load fails.
 */
public class InvalidPluginHeaderException extends Exception {

    public InvalidPluginHeaderException() {
        super();
    }

    public InvalidPluginHeaderException(String message) {
        super(message);
    }

    public InvalidPluginHeaderException(Exception cause) {
        super(cause);
    }

    public InvalidPluginHeaderException(Exception cause, String message) {
        super(message, cause);
    }
}
