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

package net.jamsimulator.jams.gui.editor.code.indexing.inspection.defaults;

import net.jamsimulator.jams.gui.editor.code.indexing.element.basic.EditorElementMacro;
import net.jamsimulator.jams.gui.editor.code.indexing.inspection.Inspection;
import net.jamsimulator.jams.gui.editor.code.indexing.inspection.InspectionLevel;
import net.jamsimulator.jams.gui.editor.code.indexing.inspection.Inspector;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.manager.ResourceProvider;

import java.util.Collections;
import java.util.Set;

public class BadMacroFormatInspector extends Inspector<EditorElementMacro> {

    public static final String NAME = "bad_macro_format";

    public BadMacroFormatInspector(ResourceProvider provider) {
        super(provider, NAME, EditorElementMacro.class);
    }

    @Override
    protected Set<Inspection> inspectImpl(EditorElementMacro element) {
        var raw = element.getRawParameters();
        if (raw.isEmpty()) return badFormat();

        if (raw.size() == 1) {
            var param = raw.get((0));
            if (param.startsWith("(") && param.endsWith(")")) return Collections.emptySet();
            return badFormat();
        }

        var first = raw.get(0);
        var last = raw.get(raw.size() - 1);

        return first.startsWith("(") && last.endsWith(")") ? Collections.emptySet() : badFormat();
    }

    private Set<Inspection> badFormat() {
        return Set.of(new Inspection(this, InspectionLevel.ERROR,
                Messages.EDITOR_ERROR_BAD_MACRO_FORMAT, Collections.emptyMap()));
    }
}
