/*
 *  MIT License
 *
 *  Copyright (c) 2022 Gael Rial Costas
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

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.event.AttachmentConfigurationRefreshEvent;
import net.jamsimulator.jams.configuration.event.ConfigurationAttachmentRegisterEvent;
import net.jamsimulator.jams.configuration.event.ConfigurationAttachmentUnregisterEvent;
import net.jamsimulator.jams.configuration.format.ConfigurationFormat;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.manager.event.ProviderUnloadEvent;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Represents a {@link Configuration} made of attachments.
 * Plugins can add and remove attachments from this configuration.
 * When a plugin is unloaded, all its attachments are removed from
 * this configuration.
 * <p>
 * All modifications made to this configuration will be removed
 * when an attachment is added or removed.
 */
public class AttachmentConfiguration extends RootConfiguration {

    private final SortedSet<ConfigurationAttachment> attachments = new TreeSet<>();
    private boolean dirty = false;

    /**
     * Creates a configuration using an absolute name, a data map and a root.
     */
    public AttachmentConfiguration() {
        super();
        Jams.getGeneralEventBroadcast().registerListeners(this, true);
    }

    public SortedSet<ConfigurationAttachment> getAttachments() {
        return Collections.unmodifiableSortedSet(attachments);
    }

    public boolean addAttachment(ConfigurationAttachment attachment) {
        Validate.notNull(attachment, "Attachment cannot be null!");
        var before = callEvent(new ConfigurationAttachmentRegisterEvent.Before(this, attachment));
        if (before.isCancelled()) return false;
        attachment = before.getAttachment();
        if (attachments.add(attachment)) {
            dirty = true;
            callEvent(new ConfigurationAttachmentRegisterEvent.After(this, attachment));
            callEvent(new AttachmentConfigurationRefreshEvent(this));
            return true;
        }
        return false;
    }

    public boolean removeAttachment(ConfigurationAttachment attachment) {
        Validate.notNull(attachment, "Attachment cannot be null!");
        var before = callEvent(new ConfigurationAttachmentUnregisterEvent.Before(this, attachment));
        if (before.isCancelled()) return false;
        if (attachments.remove(attachment)) {
            dirty = true;
            callEvent(new ConfigurationAttachmentUnregisterEvent.After(this, attachment));
            callEvent(new AttachmentConfigurationRefreshEvent(this));
            return true;
        }
        return false;
    }

    public boolean removeAttachmentsOf(ResourceProvider provider) {
        Validate.notNull(provider, "Provider cannot be null!");

        boolean removedAny = false;
        var iterator = attachments.iterator();
        while (iterator.hasNext()) {
            var next = iterator.next();
            if (next.provider().equals(provider)) {
                if (callEvent(new ConfigurationAttachmentUnregisterEvent.Before(this, next)).isCancelled())
                    continue;
                iterator.remove();
                callEvent(new ConfigurationAttachmentUnregisterEvent.After(this, next));
                removedAny = true;
            }
        }

        if (removedAny) {
            dirty = true;
            callEvent(new AttachmentConfigurationRefreshEvent(this));
        }

        return removedAny;
    }

    @Override
    public <T> Optional<T> get(String key) {
        if (dirty) {
            recalculateMap();
        }
        return super.get(key);
    }

    @Override
    public Map<String, Object> getAll(boolean deep) {
        if (dirty) {
            recalculateMap();
        }
        return super.getAll(deep);
    }

    @Override
    public void save(File file, ConfigurationFormat format, boolean prettyOutput) throws IOException {
        if (dirty) {
            recalculateMap();
        }
        super.save(file, format, prettyOutput);
    }

    private void recalculateMap() {
        map.clear();
        attachments.forEach(attachment -> addNotPresentValues(attachment.configuration()));
        dirty = false;
    }

    @Listener
    private void onProviderUnload(ProviderUnloadEvent event) {
        removeAttachmentsOf(event.getProvider());
    }

}
