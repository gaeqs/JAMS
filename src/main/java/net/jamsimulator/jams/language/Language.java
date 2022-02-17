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

import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.manager.ManagerResource;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.utils.Validate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a language. A language contains can be interpreted as a map that links language nodes
 * to their message in the represented language.
 * <p>
 * A language file usually has a YAML format.
 * You can use several styling options in these messages using the xml tag syntax.
 * <p>
 * The valid options are: b, code, u, i, sub.
 */
public class Language implements ManagerResource {

    private final ResourceProvider resourceProvider;
    private final String name;
    private final Map<String, String> baseMessages;
    private final SortedSet<LanguageAttachment> attachments;

    private Map<String, String> messages;
    private boolean dirty;

    /**
     * Creates a language.
     *
     * @param resourceProvider the provider of the language.
     * @param name             the name of the language.
     * @param baseMessages     the base messages of the language, as a {@link Configuration}.
     */
    public Language(ResourceProvider resourceProvider, String name, Configuration baseMessages) {
        Validate.notNull(resourceProvider, "Resource provider cannot be null!");
        Validate.notNull(name, "Name cannot be null!");
        Validate.notNull(baseMessages, "Base data cannot be null!");
        this.resourceProvider = resourceProvider;
        this.name = name;
        this.baseMessages = baseMessages.getAll(true).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().toString()));
        this.attachments = new TreeSet<>();

        this.messages = this.baseMessages;
        this.dirty = false;
    }

    /**
     * Creates a language.
     *
     * @param resourceProvider the provider of the language.
     * @param name             the name of the language.
     * @param baseMessages     the base messages of the language, as a {@link Map}.
     */
    public Language(ResourceProvider resourceProvider, String name, Map<String, String> baseMessages) {
        Validate.notNull(resourceProvider, "Resource provider cannot be null!");
        Validate.notNull(name, "Name cannot be null!");
        Validate.notNull(baseMessages, "Base data cannot be null!");
        this.resourceProvider = resourceProvider;
        this.name = name;
        this.baseMessages = Map.copyOf(baseMessages);
        this.attachments = new TreeSet<>();

        this.messages = this.baseMessages;
        this.dirty = false;
    }

    @Override
    public ResourceProvider getResourceProvider() {
        return resourceProvider;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns a {@link Map} with all the messages without attachments of this language.
     * <p>
     * This {@link Map} is unmodifiable.
     *
     * @return the {@link Map}.
     */
    public Map<String, String> getBaseMessages() {
        return baseMessages;
    }

    /**
     * Returns a {@link Map} with all the messages of this language.
     * <p>
     * This {@link Map} is unmodifiable. This {@link Map} won't reflect changes to the attachments,
     * as this {@link Map} is recreated every time the language changes.
     *
     * @return the {@link Map}.
     */
    public Map<String, String> getMessages() {
        refresh();
        return messages;
    }

    /**
     * Returns a {@link SortedSet} with all the attachments inside this language.
     *
     * @return the {@link SortedSet}.
     */
    public SortedSet<LanguageAttachment> getAttachments() {
        return Collections.unmodifiableSortedSet(attachments);
    }

    /**
     * Adds an attachment to this language.
     * <p>
     * You may like to call {@link LanguageManager#refresh()} after finishing modifications.
     *
     * @param attachment the attachment.
     * @return whether the attachment was added.
     */
    public boolean addAttachment(LanguageAttachment attachment) {
        Validate.notNull(attachment, "Attachment cannot be null!");
        if (attachments.add(attachment)) {
            dirty = true;
            return true;
        }
        return false;
    }

    /**
     * Removes an attachment from this language.
     * <p>
     * You may like to call {@link LanguageManager#refresh()} after finishing modifications.
     *
     * @param attachment the attachment.
     * @return whether the attachment was removed.
     */
    public boolean removeAttachment(LanguageAttachment attachment) {
        Validate.notNull(attachment, "Attachment cannot be null!");
        if (attachments.remove(attachment)) {
            dirty = true;
            return true;
        }
        return false;
    }

    /**
     * Removes all attachments from this language that matches the given provider.
     * <p>
     * You may like to call {@link LanguageManager#refresh()} after finishing modifications.
     *
     * @param provider the resource provider.
     * @return whether any attachment was removed.
     */
    public boolean removeAttachmentsOf(ResourceProvider provider) {
        Validate.notNull(provider, "Provider cannot be null!");
        if (attachments.removeIf(it -> provider.equals(it.resourceProvider()))) {
            dirty = true;
            return true;
        }
        return false;
    }

    /**
     * Returns the message linked to the given node if present.
     *
     * @param node the node.
     * @return the message if present.
     */
    public Optional<String> get(String node) {
        refresh();
        return Optional.ofNullable(messages.get(node));
    }

    /**
     * Returns the message linked to the given node or an empty string if not found.
     *
     * @param node the node.
     * @return the message or an empty string.
     */
    public String getOrEmpty(String node) {
        refresh();
        return messages.getOrDefault(node, "");
    }

    /**
     * Returns the message linked to the given node or the message linked to the default language.
     * If the default language doesn't contain the language node neither, an empty string is returned.
     *
     * @param node the node.
     * @return the message or an empty string.
     */
    public String getOrDefault(String node) {
        refresh();
        String string = messages.get(node);
        if (string != null) return string;
        return Manager.ofD(Language.class).getDefault().getOrEmpty(node);
    }

    private void refresh() {
        if (!dirty) return;
        if (attachments.isEmpty()) {
            messages = baseMessages;
            return;
        }

        var map = new HashMap<>(baseMessages);
        attachments.forEach(attachment -> map.putAll(attachment.attachment()));
        messages = Collections.unmodifiableMap(map);
        dirty = false;
    }

}
