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

package net.jamsimulator.jams.gui.mips.editor.index.element;

import net.jamsimulator.jams.gui.editor.code.indexing.EditorIndex;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedParentElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedParentElementImpl;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.pseudo.PseudoInstruction;
import net.jamsimulator.jams.project.mips.MIPSProject;
import net.jamsimulator.jams.utils.InstructionUtils;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class MIPSEditorInstruction extends EditorIndexedParentElementImpl {

    private Instruction instruction;

    public MIPSEditorInstruction(EditorIndex index, EditorIndexedParentElement parent,
                                 int start, String text) {
        super(index, parent, start, text);
        parseText();
    }

    public Optional<Instruction> getInstruction() {
        return Optional.ofNullable(instruction);
    }

    public Set<Instruction> getCompatibleInstructions(int upTo) {
        if (!(index.getProject() instanceof MIPSProject project)) return Collections.emptySet();

        var instructionSet = project.getData().getInstructionSet();
        var registerBuilder = project.getData().getRegistersBuilder();

        var instructions = instructionSet.getInstructionByMnemonic(getIdentifier());

        int i = 0;
        Instruction current;
        for (var parameter : elements.subList(1, elements.size())) {
            if (i == upTo) return instructions;
            var iterator = instructions.iterator();
            while (iterator.hasNext()) {
                current = iterator.next();
                if (current.getParameters().length <= i
                        //        || !current.getParameters()[i].match(parameter.getReplacedText(), registerBuilder)) {
                        || !current.getParameters()[i].match(parameter.getText(), registerBuilder)) {
                    iterator.remove();
                }
            }

            i++;
        }

        return instructions;
    }

    protected void parseText() {
        if (!(index.getProject() instanceof MIPSProject project)) return;
        var instructionSet = project.getData().getInstructionSet();
        var registersBuilder = project.getData().getRegistersBuilder();

        var raw = text;
        var trim = raw.trim();
        var offset = trim.isEmpty() ? 0 : raw.indexOf(trim.charAt(0));
        var mnemonicIndex = StringUtils.indexOf(trim, ' ', ',', '\t');
        var mnemonic = mnemonicIndex == -1 ? trim : trim.substring(0, mnemonicIndex);

        if (mnemonicIndex == -1) {
            instruction = instructionSet.getInstructionByMnemonic(mnemonic)
                    .stream().filter(i -> !i.hasParameters()).findAny().orElse(null);
            parseMnemonic(trim, offset, instruction instanceof PseudoInstruction);
            return;
        }

        raw = trim.substring(mnemonicIndex + 1);
        int parametersStart = mnemonicIndex + 1;

        var parametersReference = new AtomicReference<List<String>>();
        var instructions = instructionSet.getInstructionByMnemonic(mnemonic);
        var best = InstructionUtils
                .getBestInstruction(instructions, parametersReference, registersBuilder, raw);

        instruction = best.orElse(null);

        if (best.isPresent()) {
            parseMnemonic(mnemonic, offset, best.get() instanceof PseudoInstruction);
            // Complex split
            offset = 0;
            int i = 0;
            for (String rawParameter : parametersReference.get()) {
                var type = best.get().getParameters()[i];
                offset = raw.indexOf(rawParameter.charAt(0), offset);
                elements.add(new MIPSEditorInstructionParameter(index, this,
                        start + parametersStart + offset, rawParameter, type));
                offset += rawParameter.length();
                i++;
            }
        } else {
            parseMnemonic(mnemonic, offset, false);
            // Simple split
            var parts = StringUtils.multiSplitIgnoreInsideStringWithIndex(
                    text, false, " ", ",", "\t"
            );
            if (parts.isEmpty()) return;
            var parameters = parts.entrySet().stream()
                    .sorted(Comparator.comparingInt(Map.Entry::getKey)).toList();

            for (var entry : parameters) {
                var parameter = new MIPSEditorInstructionParameter(index, this,
                        start + parametersStart + entry.getKey(), entry.getValue(), null);
                elements.add(parameter);
            }
        }
    }

    protected void parseMnemonic(String mnemonic, int offset, boolean pseudo) {
        elements.add(new MIPSEditorInstructionMnemonic(index, this,
                start + offset, mnemonic, pseudo));
    }

}
