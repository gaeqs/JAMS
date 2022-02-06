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

package net.jamsimulator.jams.configuration;

import net.jamsimulator.jams.configuration.format.ConfigurationFormat;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.event.EventBroadcast;
import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Represents the root of a configuration. This instance should be created using a JSON string or
 * a file that contains it.
 */
public class RootConfiguration extends Configuration implements EventBroadcast {

    private final SimpleEventBroadcast broadcast;
    private File file;

    /**
     * Creates an empty root configuration.
     */
    public RootConfiguration() {
        super(null, new HashMap<>(), null);
        root = this;
        file = null;
        broadcast = new SimpleEventBroadcast();
    }

    /**
     * Creates a root configuration using a file that contains a JSON string.
     *
     * @param file the file to parse.
     * @param format the format of the file.
     * @throws IOException when the file cannot be readed.
     */
    public RootConfiguration(File file, ConfigurationFormat format) throws IOException {
        super(null, format.deserialize(FileUtils.readAll(file)), null);
        this.root = this;
        this.file = file;
        this.broadcast = new SimpleEventBroadcast();
    }

    /**
     * Creates a root configuration using a string.
     *
     * @param data the data.
     * @param format the format of the data.
     */
    public RootConfiguration(String data, ConfigurationFormat format) {
        super(null, format.deserialize(data), null);
        root = this;
        file = null;
        broadcast = new SimpleEventBroadcast();
    }

    /**
     * Creates a root configuration using a {@link Reader} that contains a string.
     *
     * @param reader the reader.
     * @param format the format of the data.
     * @throws IOException when the file cannot be readed.
     */
    public RootConfiguration(Reader reader, ConfigurationFormat format) throws IOException {
        super(null, format.deserialize(FileUtils.readAll(reader)), null);
        root = this;
        file = null;
        broadcast = new SimpleEventBroadcast();
    }

    /**
     * Sets the default save file.
     *
     * @param file the default save file.
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Saves the {@link RootConfiguration} into the file that loaded it, if present.
     * The output depends on the selected format.
     *
     * @param format       the output format.
     * @param prettyOutput whether the output text should be stylized.
     * @throws IOException writer IOException.
     */
    public void save(ConfigurationFormat format, boolean prettyOutput) throws IOException {
        if (file != null)
            save(file, format, prettyOutput);
    }

    //region broadcast methods

    @Override
    public boolean registerListener(Object instance, Method method, boolean useWeakReferences) {
        return broadcast.registerListener(instance, method, useWeakReferences);
    }

    @Override
    public int registerListeners(Object instance, boolean useWeakReferences) {
        return broadcast.registerListeners(instance, useWeakReferences);
    }

    @Override
    public boolean unregisterListener(Object instance, Method method) {
        return broadcast.unregisterListener(instance, method);
    }

    @Override
    public int unregisterListeners(Object instance) {
        return broadcast.unregisterListeners(instance);
    }

    @Override
    public <T extends Event> T callEvent(T event) {
        return broadcast.callEvent(event, this);
    }

    @Override
    public void transferListenersTo(EventBroadcast broadcast) {
        this.broadcast.transferListenersTo(broadcast);
    }

    //endregion
}
