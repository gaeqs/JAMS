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

package net.jamsimulator.jams.gui.util;

import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.editor.code.CodeFileEditor;
import net.jamsimulator.jams.gui.editor.holder.FileEditorHolderHolder;
import net.jamsimulator.jams.gui.editor.holder.FileEditorTab;
import net.jamsimulator.jams.utils.FileUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public class CodeFileEditorUtils {

    public static Optional<CodeFileEditor> getFocusedCodeFileEditor() {
        var project = JamsApplication.getProjectsTabPane().getFocusedProject().orElse(null);
        if (project == null) return Optional.empty();

        var node = project.getProjectTabPane().getSelectionModel().getSelectedItem().getContent();
        if (!(node instanceof FileEditorHolderHolder holder)) return Optional.empty();

        var editor = holder.getFileEditorHolder().getLastFocusedEditor().orElse(null);
        if (!(editor instanceof CodeFileEditor fileEditor)) return Optional.empty();
        return Optional.of(fileEditor);
    }

    public static String read(FileEditorTab tab) {
        if (tab == null) return "";
        try {
            return FileUtils.readAll(tab.getFile());
        } catch (IOException ex) {
            StringWriter writer = new StringWriter();
            ex.printStackTrace(new PrintWriter(writer));
            return writer.toString();
        }
    }
}
