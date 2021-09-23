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

import net.jamsimulator.jams.gui.editor.code.indexing.element.basic.EditorElementLabel;
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

public class DuplicatedLabelInspector extends Inspector<EditorElementLabel> {

    public static final String NAME = "duplicated_label";

    public DuplicatedLabelInspector(ResourceProvider provider) {
        super(provider, NAME, EditorElementLabel.class);
    }

    @Override
    public Set<Inspection> inspectImpl(EditorElementLabel element) {
        // Let's start getting the reference of this label.
        var reference = (EditorElementReference<? extends EditorElementLabel>) element.getReference();

        // Now we search all labels with the same reference. (Same identifier / name)
        var elements = element.getIndex().getReferencedElements(reference, element.getScope());

        // Do we have more than one label? Then there's a duplicated label.
        if (elements.size() > 1) {
            var other = elements.stream().filter(it -> it != element).findAny().orElse(null);
            // Return the inspection.
            return Set.of(duplicateLabel(element, other));
        }

        // We have only one label in our index! Let's check if there's an index in the global index
        // that declares a global label with the same reference.
        var optional = element.getIndex().getGlobalIndex();

        // First we have to check if this index is registered in the global index of our project.
        if (optional.isPresent()) {
            var global = optional.get();
            elements = global.searchReferencedElements(reference);

            // If the elements is empty or the element only contains our element, then there's no duplicated labels.
            if (!elements.isEmpty() && (elements.size() != 1 || !elements.contains(element))) {
                var other = elements.stream().filter(it -> it != element).findAny().orElse(null);

                // Return the inspection.
                return Set.of(duplicateGlobalLabel(element, other));
            }
        }

        return Collections.emptySet();
    }


    private Inspection duplicateLabel(EditorElementLabel label, EditorElementLabel other) {
        var replacements = Map.of(
                "{LABEL}", label.getIdentifier(),
                "{LINE}", other == null ? "-" : other.getParentOfType(EditorIndexedLine.class)
                        .map(it -> it.getNumber() + 1).map(Object::toString).orElse("-")
        );

        return new Inspection(this, InspectionLevel.ERROR,
                Messages.EDITOR_ERROR_DUPLICATE_LABEL, replacements);
    }

    private Inspection duplicateGlobalLabel(EditorElementLabel label, EditorElementLabel other) {
        var replacements = Map.of(
                "{LABEL}", label.getIdentifier(),
                "{FILE}", other == null ? "-" : other.getIndex().getName(),
                "{LINE}", other == null ? "-" : other.getParentOfType(EditorIndexedLine.class)
                        .map(it -> it.getNumber() + 1).map(Object::toString).orElse("-")
        );

        return new Inspection(this, InspectionLevel.ERROR,
                Messages.EDITOR_ERROR_DUPLICATE_GLOBAL_LABEL, replacements);
    }
}
