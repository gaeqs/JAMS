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

package net.jamsimulator.jams.project;

import net.jamsimulator.jams.collection.Bag;
import net.jamsimulator.jams.event.EventBroadcast;
import net.jamsimulator.jams.gui.editor.FileEditorHolder;

import java.io.File;
import java.util.Set;

public interface FilesToAssemble extends EventBroadcast {

    Project getProject();

    boolean supportsGlobalLabels();

    Bag<String> getGlobalLabels();

    Set<File> getFiles();

    boolean containsFile(File file);

    void addFile(File file, boolean refreshGlobalLabels);

    void addFile(File file, FileEditorHolder holder, boolean refreshGlobalLabels);

    void removeFile(File file);

    void refreshGlobalLabels();

    void checkFiles();
}
