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

package net.jamsimulator.jams.gui.mips.editor.indexing.inspection;

import net.jamsimulator.jams.gui.editor.code.indexing.element.basic.EditorElementLabel;
import net.jamsimulator.jams.gui.editor.code.indexing.inspection.Inspection;
import net.jamsimulator.jams.gui.editor.code.indexing.inspection.InspectionLevel;
import net.jamsimulator.jams.gui.editor.code.indexing.inspection.Inspector;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.utils.LabelUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class MIPSIllegalLabelInspector extends Inspector<EditorElementLabel> {

    public static final String NAME = "illegal_label";

    public MIPSIllegalLabelInspector(ResourceProvider provider) {
        super(provider, NAME, EditorElementLabel.class);
    }

    @Override
    public Set<Inspection> inspectImpl(EditorElementLabel element) {
        return LabelUtils.isLabelDeclarationLegal(element.getIdentifier()) ? Collections.emptySet() : Set.of(illegalLabel(element));
    }


    private Inspection illegalLabel(EditorElementLabel label) {
        var replacements = Map.of("{LABEL}", label.getIdentifier());
        return new Inspection(this, InspectionLevel.ERROR,
                Messages.EDITOR_MIPS_ERROR_ILLEGAL_LABEL, replacements);
    }
}
