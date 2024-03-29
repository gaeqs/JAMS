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
import net.jamsimulator.jams.gui.editor.code.CodeFileEditor;
import net.jamsimulator.jams.gui.editor.code.indexing.EditorIndex;
import net.jamsimulator.jams.gui.editor.holder.FileEditorTab;
import net.jamsimulator.jams.gui.image.icon.IconData;
import net.jamsimulator.jams.manager.ResourceProvider;

public class TextFileType extends FileType {


    /**
     * Creates a text file type.
     *
     * @param provider   the provider.
     * @param name       the name.
     * @param iconName   the name of the icon.
     * @param extensions the extensions.
     */
    public TextFileType(ResourceProvider provider, String name, IconData iconName, String... extensions) {
        super(provider, name, iconName, extensions);
    }

    @Override
    public FileEditor createDisplayTab(FileEditorTab tab) {
        return new CodeFileEditor(tab) {
            @Override
            protected EditorIndex generateIndex() {
                return null;
            }

            @Override
            protected boolean useTabCharacter() {
                return true;
            }
        };
    }
}
