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

package net.jamsimulator.jams.file;

import net.jamsimulator.jams.gui.editor.FileEditor;
import net.jamsimulator.jams.gui.editor.FileEditorTab;
import net.jamsimulator.jams.gui.image.icon.IconData;
import net.jamsimulator.jams.utils.Labeled;
import net.jamsimulator.jams.utils.Validate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a file type. A file type contains a name and a collection of all supported extensions.
 * <p>
 * Remember that if two file types contains the same extension, and they're inside
 * the same manager some functions will cause unpredictable results.
 */
public abstract class FileType implements Labeled {

    public static final int IMAGE_SIZE = 16;


    private final String name;
    private final Set<String> extensions;
    private final IconData iconData;

    /**
     * Creates a file type.
     *
     * @param name       the name.
     * @param iconData   the name of the icon.
     * @param extensions the extensions.
     */
    public FileType(String name, IconData iconData, String... extensions) {
        Validate.notNull(name, "Name cannot be null!");
        Validate.hasNoNulls(extensions, "There must be no null extensions!");
        this.name = name;
        this.extensions = new HashSet<>();
        this.extensions.addAll(Arrays.asList(extensions));
        this.iconData = iconData;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns a mutable collection with all extensions.
     * <p>
     * Remember that if two file types contains the same extension, and they're inside
     * the same manager some functions will cause unpredictable results.
     * <p>
     * Extensions are case-insensitive.
     *
     * @return the extensions.
     */
    public Set<String> getExtensions() {
        return extensions;
    }

    /**
     * Returns the given extension from this file type.
     *
     * @param extension the extension.
     * @return whether the extension was removed.
     * @see #getExtensions()
     */
    public boolean removeExtension(String extension) {
        return extensions.removeIf(target -> target.equalsIgnoreCase(extension));
    }

    /**
     * Returns whether this file type supports the given file extension.
     *
     * @param extension the extension
     * @return whether this file type supports the given file extension.
     */
    public boolean supportsExtension(String extension) {
        return extensions.stream().anyMatch(target -> target.equalsIgnoreCase(extension));
    }

    public IconData getIcon() {
        return iconData;
    }

    /**
     * Creates a {@link FileEditorTab} for the given {@link FileEditorTab}.
     *
     * @param tab the {@link FileEditorTab}.
     * @return the {@link FileEditor}.
     */
    public abstract FileEditor createDisplayTab(FileEditorTab tab);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileType fileType = (FileType) o;
        return name.equals(fileType.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
