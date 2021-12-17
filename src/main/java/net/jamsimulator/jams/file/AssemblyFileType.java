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
import net.jamsimulator.jams.gui.editor.holder.FileEditorTab;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.mips.editor.MIPSFileEditor;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.project.ProjectType;
import net.jamsimulator.jams.project.mips.MIPSProjectType;
import net.jamsimulator.jams.utils.Validate;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class AssemblyFileType extends FileType {

    public static final String NAME = "Assembly";

    private final Map<ProjectType<?>, Function<FileEditorTab, FileEditor>> builders = new HashMap<>();

    AssemblyFileType(ResourceProvider provider) {
        super(provider, NAME, Icons.FILE_ASSEMBLY, "asm", "s");
        builders.put(MIPSProjectType.INSTANCE, MIPSFileEditor::new);
    }

    public void addBuilder(ProjectType<?> type, Function<FileEditorTab, FileEditor> builder) {
        Validate.notNull(type, "Type cannot be null!");
        Validate.notNull(builder, "Builder cannot be null!");
        builders.put(type, builder);
    }

    public void removeBuilder(ProjectType<?> type) {
        Validate.notNull(type, "Type cannot be null!");
        builders.remove(type);
    }

    @Override
    public FileEditor createDisplayTab(FileEditorTab tab) {
        var builder = builders.get(tab.getWorkingPane().getProjectTab().getProject().getType());
        return builder == null ? new MIPSFileEditor(tab) : builder.apply(tab);
    }
}
