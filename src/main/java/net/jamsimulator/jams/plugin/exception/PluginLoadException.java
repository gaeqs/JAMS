package net.jamsimulator.jams.plugin.exception;

import net.jamsimulator.jams.plugin.PluginHeader;
import net.jamsimulator.jams.utils.Validate;

/**
 * This exception is thrown when a plugin load fails.
 * <p>
 * Instances of this exception contains the {@link PluginHeader} of the plugin.
 */
public class PluginLoadException extends Exception {

    private final PluginHeader header;

    public PluginLoadException(PluginHeader header) {
        super();
        Validate.notNull(header, "Header cannot be null!");
        this.header = header;
    }

    public PluginLoadException(String message, PluginHeader header) {
        super(message);
        Validate.notNull(header, "Header cannot be null!");
        this.header = header;
    }

    public PluginLoadException(Exception cause, PluginHeader header) {
        super(cause);
        Validate.notNull(header, "Header cannot be null!");
        this.header = header;
    }

    public PluginLoadException(Exception cause, String message, PluginHeader header) {
        super(message, cause);
        Validate.notNull(header, "Header cannot be null!");
        this.header = header;
    }

    /**
     * Returns the {@link PluginHeader} of the {@link net.jamsimulator.jams.plugin.Plugin plugin} that failed to load.
     *
     * @return the {@link PluginHeader}
     */
    public PluginHeader getHeader() {
        return header;
    }
}
