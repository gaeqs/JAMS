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
import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.utils.Validate;

/**
 * This event is called when a {@link ConfigurationAttachment} is registered.
 */
public class ConfigurationAttachmentRegisterEvent extends ConfigurationAttachmentEvent {

    private ConfigurationAttachmentRegisterEvent(
            AttachmentConfiguration configuration, ConfigurationAttachment attachment) {
        super(configuration, attachment);
    }

    public static class Before extends ConfigurationAttachmentRegisterEvent implements Cancellable {

        private boolean cancelled;

        public Before(AttachmentConfiguration configuration, ConfigurationAttachment attachment) {
            super(configuration, attachment);
        }

        public void setAttachment(ConfigurationAttachment attachment) {
            Validate.notNull(attachment, "Attachment cannot be null!");
            this.attachment = attachment;
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

    public static class After extends ConfigurationAttachmentRegisterEvent {

        public After(AttachmentConfiguration configuration, ConfigurationAttachment attachment) {
            super(configuration, attachment);
        }
    }
}