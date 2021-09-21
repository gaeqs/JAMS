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

import net.jamsimulator.jams.gui.editor.code.indexing.inspection.Inspection;
import net.jamsimulator.jams.gui.editor.code.indexing.inspection.InspectionLevel;
import net.jamsimulator.jams.gui.editor.code.indexing.inspection.Inspector;
import net.jamsimulator.jams.gui.mips.editor.indexing.element.MIPSEditorDirective;
import net.jamsimulator.jams.gui.mips.editor.indexing.element.MIPSEditorDirectiveMnemonic;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.mips.directive.defaults.DirectiveEqv;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class MIPSEqvUseInspector extends Inspector<MIPSEditorDirectiveMnemonic> {

    public static final String NAME = "eqv_use";

    public MIPSEqvUseInspector(ResourceProvider provider) {
        super(provider, NAME, MIPSEditorDirectiveMnemonic.class);
    }

    @Override
    public Set<Inspection> inspectImpl(MIPSEditorDirectiveMnemonic element) {
        var directive = element.getParentOfType(MIPSEditorDirective.class)
                .flatMap(MIPSEditorDirective::getDirective).orElse(null);
        return directive instanceof DirectiveEqv ? Set.of(eqvUse()) : Collections.emptySet();
    }


    private Inspection eqvUse() {
        return new Inspection(this, InspectionLevel.WARNING, Messages.EDITOR_MIPS_WARNING_EQV_USE, Map.of());
    }
}
