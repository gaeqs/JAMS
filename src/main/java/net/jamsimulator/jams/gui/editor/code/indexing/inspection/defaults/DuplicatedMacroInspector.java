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

import net.jamsimulator.jams.gui.editor.code.indexing.element.ElementScope;
import net.jamsimulator.jams.gui.editor.code.indexing.element.basic.EditorElementMacro;
import net.jamsimulator.jams.gui.editor.code.indexing.element.line.EditorIndexedLine;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorElementReference;
import net.jamsimulator.jams.gui.editor.code.indexing.inspection.Inspection;
import net.jamsimulator.jams.gui.editor.code.indexing.inspection.InspectionLevel;
import net.jamsimulator.jams.gui.editor.code.indexing.inspection.Inspector;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.manager.ResourceProvider;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class DuplicatedMacroInspector extends Inspector<EditorElementMacro> {

    public static final String NAME = "duplicated_macro";

    public DuplicatedMacroInspector(ResourceProvider provider) {
        super(provider, NAME, EditorElementMacro.class);
    }

    @Override
    public Set<Inspection> inspectImpl(EditorElementMacro element) {
        var scope = element.getReferencedScope();
        // Let's start getting the reference of this label.
        var reference = (EditorElementReference<? extends EditorElementMacro>) element.getReference();

        // Now we search all labels with the same reference. (Same identifier / name)
        var elements = element.getIndex()
                .getReferencedElements(reference, element.getReferencingScope());
        // Do we have more than one label? Then there's a duplicated label.
        for (var duplicated : elements) {
            if (duplicated == element) continue;
            var duplicatedScope = duplicated.getReferencedScope();
            if (duplicatedScope.equals(scope)) {
                return Set.of(duplicateMacro(element, duplicated));
            }
            if (duplicatedScope.canBeReachedFrom(scope)) {
                return Set.of(shadowedMacro(element, duplicated));
            }
        }

        // We have only one label in our index! Let's check if there's an index in the global index
        // that declares a global label with the same reference.
        var optional = element.getIndex().getGlobalIndex();

        // First we have to check if this index is registered in the global index of our project.
        if (optional.isPresent()) {
            var global = optional.get();
            elements = global.searchReferencedElements(reference);

            for (var duplicated : elements) {
                if (duplicated == element) continue;
                var duplicatedScope = ElementScope.GLOBAL;
                if (duplicatedScope.equals(scope)) {
                    return Set.of(duplicateGlobalMacro(element, duplicated));
                }
                if (duplicatedScope.canBeReachedFrom(scope)) {
                    return Set.of(shadowedGlobalMacro(element, duplicated));
                }
            }
        }

        return Collections.emptySet();
    }


    private Inspection duplicateMacro(EditorElementMacro macro, EditorElementMacro other) {
        var replacements = Map.of(
                "{MACRO}", macro.getText(),
                "{PARAMETERS}", String.valueOf(macro.getParameterAmount()),
                "{LINE}", other == null ? "-" : other.getParentOfType(EditorIndexedLine.class)
                        .map(it -> it.getNumber() + 1).map(Object::toString).orElse("-")
        );

        return new Inspection(this, InspectionLevel.ERROR,
                Messages.EDITOR_ERROR_DUPLICATE_MACRO, replacements);
    }

    private Inspection duplicateGlobalMacro(EditorElementMacro macro, EditorElementMacro other) {
        var replacements = Map.of(
                "{MACRO}", macro.getText(),
                "{PARAMETERS}", String.valueOf(macro.getParameterAmount()),
                "{FILE}", other == null ? "-" : other.getIndex().getName(),
                "{LINE}", other == null ? "-" : other.getParentOfType(EditorIndexedLine.class)
                        .map(it -> it.getNumber() + 1).map(Object::toString).orElse("-")
        );

        return new Inspection(this, InspectionLevel.ERROR,
                Messages.EDITOR_ERROR_DUPLICATE_GLOBAL_MACRO, replacements);
    }

    private Inspection shadowedMacro(EditorElementMacro macro, EditorElementMacro other) {
        var replacements = Map.of(
                "{MACRO}", macro.getText(),
                "{PARAMETERS}", String.valueOf(macro.getParameterAmount()),
                "{LINE}", other == null ? "-" : other.getParentOfType(EditorIndexedLine.class)
                        .map(it -> it.getNumber() + 1).map(Object::toString).orElse("-")
        );

        return new Inspection(this, InspectionLevel.WARNING,
                Messages.EDITOR_WARNING_SHADOWED_MACRO, replacements);
    }

    private Inspection shadowedGlobalMacro(EditorElementMacro macro, EditorElementMacro other) {
        var replacements = Map.of(
                "{MACRO}", macro.getText(),
                "{PARAMETERS}", String.valueOf(macro.getParameterAmount()),
                "{FILE}", other == null ? "-" : other.getIndex().getName(),
                "{LINE}", other == null ? "-" : other.getParentOfType(EditorIndexedLine.class)
                        .map(it -> it.getNumber() + 1).map(Object::toString).orElse("-")
        );

        return new Inspection(this, InspectionLevel.WARNING,
                Messages.EDITOR_WARNING_SHADOWED_GLOBAL_MACRO, replacements);
    }

}
