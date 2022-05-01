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

package net.jamsimulator.jams.configuration.event;

import net.jamsimulator.jams.configuration.AttachmentConfiguration;
import net.jamsimulator.jams.configuration.ConfigurationAttachment;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.utils.Validate;

/**
 * Represents an event related to a {@link  ConfigurationAttachment}.
 */
public class ConfigurationAttachmentEvent extends Event {

    protected final AttachmentConfiguration configuration;
    protected ConfigurationAttachment attachment;

    public ConfigurationAttachmentEvent(AttachmentConfiguration configuration, ConfigurationAttachment attachment) {
        Validate.notNull(configuration, "Configuration cannot be null!");
        Validate.notNull(attachment, "Attachment cannot be null!");
        this.configuration = configuration;
        this.attachment = attachment;
    }

    public AttachmentConfiguration getConfiguration() {
        return configuration;
    }

    public ConfigurationAttachment getAttachment() {
        return attachment;
    }
}
