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

package net.jamsimulator.jams.language;

import net.jamsimulator.jams.manager.ResourceProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a set of messages attached to a language.
 *
 * @param resourceProvider the provider of the attachment.
 * @param attachment       the data.
 *                         This map will be copied. Further modifications to the given map won't affect the attachment.
 * @param priority         the priority of the attachment. Messages in the attachment with the bigger priority will be used.
 */
public record LanguageAttachment(
        ResourceProvider resourceProvider,
        Map<String, String> attachment,
        int priority,
        int id
) implements Comparable<LanguageAttachment> {

    private static final AtomicInteger ID_GENERATOR = new AtomicInteger();

    /**
     * Creates the theme attachment.
     *
     * @param resourceProvider the provider of the attachment.
     * @param attachment       the data.
     *                         This map will be copied. Further modifications to the given map won't affect the attachment.
     * @param priority         the priority of the attachment. Messages in the attachment with the bigger priority will be used.
     * @param id               the id of the attachment.
     */
    public LanguageAttachment(
            ResourceProvider resourceProvider,
            Map<String, String> attachment,
            int priority,
            int id
    ) {
        this.resourceProvider = resourceProvider;
        this.attachment = Map.copyOf(attachment);
        this.priority = priority;
        this.id = id;
    }

    /**
     * Creates the theme attachment. Its ID is generated automatically.
     *
     * @param resourceProvider the provider of the attachment.
     * @param attachment       the data.
     *                         This map will be copied. Further modifications to the given map won't affect the attachment.
     * @param priority         the priority of the attachment. Messages in the attachment with the bigger priority will be used.
     */
    public LanguageAttachment(
            ResourceProvider resourceProvider,
            Map<String, String> attachment,
            int priority
    ) {
        this(resourceProvider, attachment, priority, ID_GENERATOR.getAndIncrement());
    }


    @Override
    public int compareTo(@NotNull LanguageAttachment o) {
        int p = priority - o.priority;
        return p == 0 ? id - o.id : p;
    }
}
