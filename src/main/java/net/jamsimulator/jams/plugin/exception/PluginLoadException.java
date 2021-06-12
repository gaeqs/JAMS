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
