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

package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.file.AssemblyFileType;
import net.jamsimulator.jams.file.FileType;
import net.jamsimulator.jams.file.ImageFileType;
import net.jamsimulator.jams.file.TextFileType;
import net.jamsimulator.jams.file.event.FileTypeRegisterEvent;
import net.jamsimulator.jams.file.event.FileTypeUnregisterEvent;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.util.Optional;

/**
 * This singleton stores all {@link FileType}s that JAMS may use.
 * <p>
 * To register a {@link FileType} use {@link Manager#add(Labeled)}}.
 * To unregister a {@link FileType} use {@link #remove(Object)}.
 */
public class FileTypeManager extends Manager<FileType> {

    public static final FileTypeManager INSTANCE = new FileTypeManager();

    private FileType unknownType, folderType;

    private FileTypeManager() {
        super(FileTypeRegisterEvent.Before::new, FileTypeRegisterEvent.After::new,
                FileTypeUnregisterEvent.Before::new, FileTypeUnregisterEvent.After::new);
    }

    /**
     * Returns the unknown {@link FileType} instance. This {@link FileType} is not registered in
     * this manager, and can be only returned by this method.
     *
     * @return the unknown {@link FileType}.
     */
    public FileType getUnknownType() {
        return unknownType;
    }

    /**
     * Returns the {@link FileType} representing folders. This {@link FileType} is not registered in
     * this manager, but it's used by it in the method {@link #getByFile(File)}.
     *
     * @return the {@link FileType}.
     */
    public FileType getFolderType() {
        return folderType;
    }

    /**
     * Returns the {@link FileType} that supports the given extension, if present.
     * <p>
     * If two or more {@link FileType}s support the given extension, any of them will be returned.
     *
     * @param extension the extension.
     * @return the {@link FileType}, if present.
     */
    public Optional<FileType> getByExtension(String extension) {
        return stream().filter(target -> target.supportsExtension(extension)).findAny();
    }

    /**
     * Returns that {@link FileType} that supports the extension of the given {@link File}, if present.
     * <p>
     * If two or more {@link FileType}s support the given extension, any of them will be returned.
     *
     * @param file the file.
     * @return the {@link FileType}, if present.
     */
    public Optional<FileType> getByFile(File file) {
        Validate.notNull(file, "File cannot be null!");

        if (file.isDirectory()) return Optional.of(folderType);

        String name = file.getName();
        int lastIndex = name.lastIndexOf(".");
        if (lastIndex == -1 || lastIndex == name.length()) return Optional.empty();
        String extension = name.substring(lastIndex + 1);
        return getByExtension(extension);
    }

    /**
     * Assigns the given extension to the {@link FileType} that matches the given name.
     * This removes the extension from all {@link FileType}s inside this manager if the
     * required {@link FileType} is found.
     *
     * @param name      the name of the {@link FileType}.
     * @param extension the extension.
     * @return whether the {@link FileType} was found and the extension was assigned.
     */
    public boolean assign(String name, String extension) {
        Optional<FileType> type = get(extension);
        if (type.isEmpty()) return false;

        forEach(target -> target.removeExtension(extension));
        type.get().getExtensions().add(name);
        return true;
    }

    @Override
    protected void loadDefaultElements() {
        unknownType = new TextFileType("Unknown", Icons.FILE_UNKNOWN);
        folderType = new TextFileType("Folder", Icons.FILE_FOLDER);

        add(unknownType);
        add(folderType);
        add(new TextFileType("Text", Icons.FILE_TEXT, "txt"));
        add(AssemblyFileType.INSTANCE);
        add(new ImageFileType("Image", Icons.FILE_IMAGE, "png", "gif", "jpg", "jpeg", "tiff"));
    }

}
