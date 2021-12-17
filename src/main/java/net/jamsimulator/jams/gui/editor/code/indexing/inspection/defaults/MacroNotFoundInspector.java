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

package net.jamsimulator.jams.gui.editor.code.indexing.inspection.defaults;

import net.jamsimulator.jams.gui.editor.code.indexing.element.basic.EditorElementMacroCallMnemonic;
import net.jamsimulator.jams.gui.editor.code.indexing.inspection.Inspection;
import net.jamsimulator.jams.gui.editor.code.indexing.inspection.InspectionLevel;
import net.jamsimulator.jams.gui.editor.code.indexing.inspection.Inspector;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.manager.ResourceProvider;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MacroNotFoundInspector extends Inspector<EditorElementMacroCallMnemonic> {

    public static final String NAME = "macro_not_found";

    public MacroNotFoundInspector(ResourceProvider provider) {
        super(provider, NAME, EditorElementMacroCallMnemonic.class);
    }

    @Override
    public Set<Inspection> inspectImpl(EditorElementMacroCallMnemonic element) {
        var set = new HashSet<Inspection>();
        for (var reference : element.getReferences()) {
            // Search on the file:
            var macro = element.getIndex().getReferencedElement(reference, element.getReferencingScope());
            if (macro.isPresent()) continue;

            // Search on global context:
            var global = element.getIndex().getGlobalIndex();
            if (global.isPresent()) {
                macro = global.get().searchReferencedElement(reference);
                if (macro.isPresent()) continue;
            }
            set.add(macroNotFound(reference.identifier()));
        }
        return set;
    }


    private Inspection macroNotFound(String identifier) {
        var replacements = Map.of("{MACRO}", identifier);

        return new Inspection(this, InspectionLevel.ERROR,
                Messages.EDITOR_ERROR_MACRO_NOT_FOUND, replacements);
    }
}
