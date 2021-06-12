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

package net.jamsimulator.jams.gui.editor;


import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.gui.ActionRegion;

/**
 * Represents a file editor.
 */
public interface FileEditor extends ActionRegion {

    /**
     * Returns the {@link FileEditorTab} of this editor.
     *
     * @return the {@link FileEditorTab}.
     */
    FileEditorTab getTab();

    /**
     * This method is executed when the file editor is closed.
     */
    void onClose();

    /**
     * This method is executed when the file should be saved.
     */
    void save();

    /**
     * This method is executed when the file should be reloaded.
     */
    void reload();

    /**
     * Adds this editor to the given {@link AnchorPane}.
     *
     * @param tabAnchorPane the {@link AnchorPane} inside the tab.
     */
    void addNodesToTab(AnchorPane tabAnchorPane);
}
