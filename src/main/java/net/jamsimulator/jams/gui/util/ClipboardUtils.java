/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.gui.util;

import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import net.jamsimulator.jams.gui.popup.PasteNewNameWindow;
import net.jamsimulator.jams.utils.FileUtils;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.util.*;

public class ClipboardUtils {

    public static void copy(File... files) {
        Validate.hasNoNulls(files, "Files cannot be null!");
        Validate.isTrue(Arrays.stream(files).allMatch(File::exists), "File must exist!");

        Map<DataFormat, Object> contentMap = new HashMap<>();
        contentMap.put(DataFormat.FILES, new ArrayList<>(Arrays.asList(files)));

        Clipboard.getSystemClipboard().setContent(contentMap);

    }

    public static void copy(Collection<File> files) {
        Validate.notNull(files, "Files cannot be null!");
        Validate.isTrue(files.stream().allMatch(File::exists), "File must exist!");

        Map<DataFormat, Object> contentMap = new HashMap<>();
        contentMap.put(DataFormat.FILES, new ArrayList<>(files));

        Clipboard.getSystemClipboard().setContent(contentMap);

    }

    public static void cut(File file) {
        Validate.notNull(file, "File cannot be null!");
        Validate.isTrue(file.exists(), "File must exist!");

        Map<DataFormat, Object> contentMap = new HashMap<>();
        List<File> files = new ArrayList<>();
        files.add(file);
        contentMap.put(DataFormat.FILES, files);

        Clipboard.getSystemClipboard().setContent(contentMap);
    }

    public static void paste(File folder) {
        Validate.notNull(folder, "Folder cannot be null!");
        Validate.isTrue(folder.isDirectory(), "Folder must be a directory!");

        Clipboard clipboard = Clipboard.getSystemClipboard();

        if (!clipboard.hasContent(DataFormat.FILES)) return;
        clipboard.getContentTypes().forEach(System.out::println);
        List<File> files = clipboard.getFiles();

        files.forEach(file -> {
            var to = new File(folder, file.getName());
            if (to.exists()) {
                var optional = PasteNewNameWindow.open(to);
                if (optional.isEmpty()) return;
                to = new File(folder, optional.get());
            }

            FileUtils.copyFile(file, to);
        });
    }
}
