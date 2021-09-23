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
import net.jamsimulator.jams.gui.editor.code.indexing.element.ElementScope;
import net.jamsimulator.jams.gui.editor.code.indexing.element.line.EditorIndexedLine;
import net.jamsimulator.jams.gui.editor.code.indexing.inspection.Inspector;
import net.jamsimulator.jams.gui.editor.code.indexing.line.EditorLineIndex;
import net.jamsimulator.jams.gui.editor.holder.FileEditorTab;
import net.jamsimulator.jams.gui.image.icon.IconData;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.task.LanguageTask;

import java.util.Optional;
import java.util.Set;

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
                var inspections = Set.<Inspector<?>>of();
                var index = new EditorLineIndex<>(tab.getWorkingPane().getProjectTab().getProject(),
                        tab.getFile().getName(), inspections) {
                    @Override
                    protected EditorIndexedLine generateNewLine(int start, int number, String text, ElementScope scope) {
                        return new EditorIndexedLine(this, scope, start, number, text) {
                            @Override
                            public boolean isMacroStart() {
                                return false;
                            }

                            @Override
                            public boolean isMacroEnd() {
                                return false;
                            }

                            @Override
                            public Optional<String> getDefinedMacroIdentifier() {
                                return Optional.empty();
                            }
                        };
                    }
                };

                tab.getWorkingPane().getProjectTab().getProject()
                        .getTaskExecutor().execute(new LanguageTask<>(Messages.EDITOR_INDEXING) {
                            @Override
                            protected Void call() {
                                index.withLock(true, i -> i.indexAll(getText()));
                                return null;
                            }
                        });

                return index;
            }
        };
    }
}
