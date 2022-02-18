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

package net.jamsimulator.jams.gui.explorer.folder;

import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class FolderExplorerDragAndDropManagement {

    public static final String EXPLORER_PROTOCOL = "jams_folder_explorer";
    public static final String URL_PROTOCOL = "file";

    public static void manageDragFromElements(Dragboard dragboard, List<ExplorerElement> elements) {
        List<File> files = new ArrayList<>();
        for (ExplorerElement element : elements) {
            if (element instanceof ExplorerFolder)
                files.add(((ExplorerFolder) element).getFolder());
            else if (element instanceof ExplorerFile)
                files.add(((ExplorerFile) element).getFile());
        }
        manageDrag(dragboard, files);
    }

    public static void manageDrag(Dragboard dragboard, List<File> files) {
        ClipboardContent content = new ClipboardContent();
        content.putFiles(files);

        StringBuilder builder = new StringBuilder();
        for (File element : files) {
            builder.append(EXPLORER_PROTOCOL).append(':')
                    .append(element.getAbsolutePath())
                    .append('\n');
        }
        content.putString(builder.length() == 0 ? "" : builder.substring(0, builder.length() - 1));
        dragboard.setContent(content);
    }

    public static void manageDrop(Dragboard content, File folder, BiConsumer<File, File> moveAction) {
        boolean move = content.getTransferModes().contains(TransferMode.MOVE);
        if (content.hasUrl()) {
            String string = content.getUrl();
            if (string.startsWith(URL_PROTOCOL + ":")) {
                manageDropFromURL(string, folder, move, moveAction);
                return;
            }
        }

        if (content.hasString()) {
            String string = content.getString();
            if (string.startsWith(EXPLORER_PROTOCOL + ":")) {
                manageDropFromString(string, folder, move, moveAction);
                return;
            }
        }

        List<File> files = content.getFiles();
        for (File file : files) {
            manageFileDrop(file, folder, move, moveAction);
        }
    }

    private static void manageDropFromString(String string, File folder, boolean move, BiConsumer<File, File> moveAction) {
        String[] filesArray = string.split("\n");
        for (String path : filesArray) {
            if (path.isEmpty()) continue;
            try {
                manageFileDrop(new File(path.substring(EXPLORER_PROTOCOL.length() + 1)), folder, move, moveAction);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void manageDropFromURL(String url, File folder, boolean move, BiConsumer<File, File> moveAction) {
        String[] filesArray = url.split("\n");
        for (String path : filesArray) {
            if (path.isEmpty()) continue;
            try {
                var file = new File(path.substring(URL_PROTOCOL.length() + 2).trim());
                manageFileDrop(file, folder, move, moveAction);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void manageFileDrop(File file, File folder, boolean move, BiConsumer<File, File> moveAction) {
        if (!file.exists() || folder.getPath().startsWith(file.getPath()) || file.getParentFile().equals(folder)) {
            return;
        }

        if (move ? !FileUtils.moveFileToFolder(folder, file, moveAction) : !FileUtils.copyFileToFolder(folder, file)) {
            new IllegalStateException("Error while copying file " + file + ".").printStackTrace();
        }
    }

}
