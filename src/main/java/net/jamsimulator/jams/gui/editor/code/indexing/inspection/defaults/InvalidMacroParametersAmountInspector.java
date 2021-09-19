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

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InvalidMacroParametersAmountInspector extends Inspector<EditorElementMacroCallMnemonic> {

    public static final String NAME = "invalid_macro_parameters_amount";

    public InvalidMacroParametersAmountInspector(ResourceProvider provider) {
        super(provider, NAME, EditorElementMacroCallMnemonic.class);
    }

    @Override
    public Set<Inspection> inspectImpl(EditorElementMacroCallMnemonic element) {
        var macroCall = element.getParent().orElse(null);
        if (macroCall == null) return Collections.emptySet();
        var parameters = macroCall.size() - 1;

        var set = new HashSet<Inspection>();
        for (var reference : element.getReferences()) {
            // Search on the file:
            var macro = element.getIndex().getReferencedElement(reference, false);
            if (macro.isPresent()) {
                if (macro.get().parameters() != parameters) {
                    set.add(invalidParametersAmount(macro.get().getIdentifier(),
                            parameters, macro.get().parameters()));
                }
                continue;
            }

            // Search on global context:
            var global = element.getIndex().getGlobalIndex();
            if (global.isPresent()) {
                macro = global.get().searchReferencedElement(reference);
                if (macro.isPresent()) {
                    if (macro.get().parameters() != parameters) {
                        set.add(invalidParametersAmount(macro.get().getIdentifier(),
                                parameters, macro.get().parameters()));
                    }
                }
            }
        }
        return set;
    }


    private Inspection invalidParametersAmount(String macro, int found, int expected) {
        var replacements = Map.of(
                "{MACRO}", macro,
                "{FOUND}", String.valueOf(found),
                "{EXPECTED}", String.valueOf(expected)
        );

        return new Inspection(this, InspectionLevel.ERROR,
                Messages.EDITOR_MIPS_ERROR_INVALID_MACRO_PARAMETERS_AMOUNT, replacements);
    }
}
