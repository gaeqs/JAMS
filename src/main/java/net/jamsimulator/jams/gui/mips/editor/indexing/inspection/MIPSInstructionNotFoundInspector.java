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
import net.jamsimulator.jams.gui.mips.editor.indexing.element.MIPSEditorInstruction;
import net.jamsimulator.jams.gui.mips.editor.indexing.element.MIPSEditorInstructionMnemonic;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.manager.ResourceProvider;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class MIPSInstructionNotFoundInspector extends Inspector<MIPSEditorInstructionMnemonic> {

    public static final String NAME = "instruction_not_found";

    public MIPSInstructionNotFoundInspector(ResourceProvider provider) {
        super(provider, NAME, MIPSEditorInstructionMnemonic.class);
    }

    @Override
    public Set<Inspection> inspectImpl(MIPSEditorInstructionMnemonic element) {
        var instruction = element.getParent().orElse(null) instanceof MIPSEditorInstruction i ? i : null;
        if (instruction == null) return Collections.emptySet();
        return instruction.getInstruction().isEmpty() ? Set.of(instructionNotFound(element)) : Collections.emptySet();
    }


    private Inspection instructionNotFound(MIPSEditorInstructionMnemonic instruction) {
        var replacements = Map.of("{INSTRUCTION}", instruction.getIdentifier());
        return new Inspection(this, InspectionLevel.ERROR,
                Messages.EDITOR_MIPS_ERROR_INSTRUCTION_NOT_FOUND, replacements);
    }
}
