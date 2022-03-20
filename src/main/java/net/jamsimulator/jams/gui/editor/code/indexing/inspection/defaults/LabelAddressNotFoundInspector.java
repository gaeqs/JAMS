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

import net.jamsimulator.jams.gui.editor.code.indexing.element.basic.EditorElementLabel;
import net.jamsimulator.jams.gui.editor.code.indexing.element.line.EditorIndexedLine;
import net.jamsimulator.jams.gui.editor.code.indexing.inspection.Inspection;
import net.jamsimulator.jams.gui.editor.code.indexing.inspection.InspectionLevel;
import net.jamsimulator.jams.gui.editor.code.indexing.inspection.Inspector;
import net.jamsimulator.jams.gui.editor.code.indexing.line.EditorLineIndex;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.manager.ResourceProvider;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class LabelAddressNotFoundInspector extends Inspector<EditorElementLabel> {

    public static final String NAME = "label_address_not_found";

    public LabelAddressNotFoundInspector(ResourceProvider provider) {
        super(provider, NAME, EditorElementLabel.class);
    }

    @Override
    public Set<Inspection> inspectImpl(EditorElementLabel element) {
        var line = element.getParentOfType(EditorIndexedLine.class).orElse(null);
        if (line == null) return Collections.emptySet();
        var scope = element.getReferencingScope();
        var lines = ((EditorLineIndex<?>) element.getIndex()).getLines();

        var linesSubList = lines.subList(line.getNumber(), lines.size());
        for (var current : linesSubList) {
            if (!current.getReferencedScope().equals(scope))
                return addressNotFoundInspection();
            if (current.canBeReferencedByALabel())
                return Collections.emptySet();
        }
        return addressNotFoundInspection();
    }

    private Set<Inspection> addressNotFoundInspection() {
        return Set.of(new Inspection(this, InspectionLevel.ERROR,
                Messages.EDITOR_ERROR_LABEL_ADDRESS_NOT_FOUND, Map.of()));
    }
}
