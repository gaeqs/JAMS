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

package net.jamsimulator.jams.gui.explorer.folder;

import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

        StringBuilder builder = new StringBuilder(EXPLORER_PROTOCOL);
        for (File element : files) {
            builder.append(':');
            builder.append(element.getAbsolutePath());
        }
        content.putString(builder.toString());
        dragboard.setContent(content);
    }

    public static void manageDrop(Dragboard content, File folder) {
        boolean move = content.getTransferModes().contains(TransferMode.MOVE);
        if (content.hasUrl()) {
            String string = content.getUrl();
            if (string.startsWith(URL_PROTOCOL + ":")) {
                manageDropFromURL(string, folder, move);
                return;
            }
        }

        if (content.hasString()) {
            String string = content.getString();
            if (string.startsWith(EXPLORER_PROTOCOL + ":")) {
                manageDropFromString(string, folder, move);
                return;
            }
        }

        List<File> files = content.getFiles();
        for (File file : files) {
            if (move ? !FileUtils.moveFile(folder, file) : !FileUtils.copyFile(folder, file)) {
                System.err.println("Error while copying file " + file + ".");
            }
        }
    }

    private static void manageDropFromString(String string, File folder, boolean move) {
        String files = string.substring(EXPLORER_PROTOCOL.length() + 1);
        if (files.isEmpty()) return;
        String[] filesArray = files.split("\n");
        for (String path : filesArray) {
            if (path.isEmpty()) continue;
            try {
                manageFileDrop(new File(path), folder, move);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void manageDropFromURL(String url, File folder, boolean move) {
        if (url.isEmpty()) return;
        String[] filesArray = url.split("\n");
        for (String path : filesArray) {
            if (path.isEmpty()) continue;
            try {
                var file = new File(path.substring(URL_PROTOCOL.length() + 2).trim());
                manageFileDrop(file, folder, move);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void manageFileDrop(File file, File folder, boolean move) {
        if (!file.exists() || folder.getPath().startsWith(file.getPath())) {
            return;
        }

        if (move ? !FileUtils.moveFile(folder, file) : !FileUtils.copyFile(folder, file)) {
            System.err.println("Error while copying file " + file + ".");
        } else {
            if (!move && !file.delete() || move && file.exists()) {
                System.err.println("Error while copying file " + file + ".");
            }
        }
    }

}
