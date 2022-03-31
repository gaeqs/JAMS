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

package net.jamsimulator.jams.gui.mips.editor;

import net.jamsimulator.jams.gui.editor.code.autocompletion.AutocompletionCandidate;
import net.jamsimulator.jams.gui.editor.code.autocompletion.AutocompletionPopupController;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.basic.EditorElementLabel;
import net.jamsimulator.jams.gui.editor.code.indexing.element.basic.EditorElementMacro;
import net.jamsimulator.jams.gui.editor.code.indexing.element.basic.EditorElementMacroCallMnemonic;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.mips.editor.indexing.element.*;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.project.mips.MIPSProject;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class MIPSAutocompletionPopupController extends AutocompletionPopupController {

    private final MIPSProject project;

    private final Set<AutocompletionCandidate<?>> instructions = new HashSet<>();
    private final Set<AutocompletionCandidate<?>> directives = new HashSet<>();

    public MIPSAutocompletionPopupController(MIPSProject project) {
        this.project = project;

        for (var instruction : project.getData().getInstructionSet().getInstructions()) {
            instructions.add(new AutocompletionCandidate<>(
                    instruction,
                    instruction.getMnemonic(),
                    instruction.getMnemonic() + (instruction.getParameters().length > 0 ? " " : ""),
                    List.of(
                            parseParameters(instruction.getParameters()),
                            instruction.getName()
                    ),
                    Icons.AUTOCOMPLETION_INSTRUCTION
            ));
        }

        for (var directive : project.getData().getDirectiveSet().getDirectives()) {
            directives.add(new AutocompletionCandidate<>(
                    directive,
                    "." + directive.getName(),
                    "." + directive.getName(),
                    Collections.emptyList(),
                    Icons.AUTOCOMPLETION_DIRECTIVE
            ));
        }
    }

    @Override
    public boolean isCandidateValidForContext(EditorIndexedElement context, AutocompletionCandidate<?> candidate) {
        return true;
    }

    @Override
    public void refreshCandidates(EditorIndexedElement context, int caretPosition) {
        candidates.clear();

        if (context instanceof MIPSEditorInstructionMnemonic) {
            candidates.addAll(instructions);
            candidates.addAll(directives);
            addMacros(context);
        } else if (context instanceof MIPSEditorDirectiveMnemonic) {
            candidates.addAll(directives);
        } else if (context instanceof EditorElementMacroCallMnemonic) {
            addMacros(context);
        } else if (context instanceof MIPSEditorInstructionParameterPart part) {
            addPartTypes(part, caretPosition);
        }

        if (!(context instanceof MIPSEditorDirectiveMacroParameter)) {
            var macro = context.getReferencingScope().macro();
            if (macro != null) {
                addMacroParameters(macro);
            }
        }
    }


    private void addMacros(EditorIndexedElement context) {
        context.getIndex().withLock(false, index -> {
            for (var macro : index.getReferencedElementsOfType(EditorElementMacro.class, context.getReferencingScope())) {
                candidates.add(new AutocompletionCandidate<>(
                        macro,
                        macro.getText(),
                        macro.getText() + " (",
                        macro.getParameterNames(),
                        Icons.AUTOCOMPLETION_MACRO
                ));
            }
        });

        context.getIndex().getGlobalIndex().ifPresent(files -> {
            for (var macro : files.searchReferencedElementsOfType(EditorElementMacro.class)) {
                candidates.add(new AutocompletionCandidate<>(
                        macro,
                        macro.getText(),
                        macro.getText() + " (",
                        macro.getParameterNames(),
                        Icons.AUTOCOMPLETION_MACRO
                ));
            }
        });
    }

    private void addPartTypes(MIPSEditorInstructionParameterPart context, int caretPosition) {
        var parameter = (MIPSEditorInstructionParameter) context.getParent().orElse(null);
        if (parameter == null) return;
        var ins = (MIPSEditorInstruction) parameter.getParent().orElse(null);
        if (ins == null) return;
        int parameterIndex = parameter.indexInParent() - 1;

        var compatibleInstructions = ins.getCompatibleInstructions(parameterIndex);
        boolean hasLabels = false, hasRegisters = false;

        for (Instruction instruction : compatibleInstructions) {
            var partStartIndex = new AtomicInteger();

            var partType = instruction.getParameters()[parameterIndex]
                    .getPartAt(caretPosition - parameter.getStart(), parameter.getText(), partStartIndex);

            switch (partType) {
                case LABEL -> {
                    if (hasLabels) break;

                    for (var label : context.getIndex().withLockF(false, index -> index
                            .getReferencedElementsOfType(EditorElementLabel.class, context.getReferencingScope()))) {
                        candidates.add(new AutocompletionCandidate<>(
                                label,
                                label.getIdentifier(),
                                label.getIdentifier(),
                                Collections.emptyList(),
                                Icons.AUTOCOMPLETION_LABEL
                        ));
                    }

                    context.getIndex().getGlobalIndex().ifPresent(files -> {
                        for (var label : files.searchReferencedElementsOfType(EditorElementLabel.class)) {
                            candidates.add(new AutocompletionCandidate<>(
                                    label,
                                    label.getIdentifier(),
                                    label.getIdentifier(),
                                    Collections.emptyList(),
                                    Icons.AUTOCOMPLETION_LABEL
                            ));
                        }
                    });
                    hasLabels = true;
                }
                case REGISTER -> {
                    if (hasRegisters) break;
                    Set<String> names = project.getData().getRegistersBuilder().getRegistersNames();
                    Set<Character> starts = project.getData().getRegistersBuilder().getValidRegistersStarts();
                    var firstStart = starts.stream().findFirst().orElse('$');

                    var preParts = parameter.getText().substring(0, partStartIndex.get());
                    for (var name : names) {
                        for (var start : starts) {
                            var key = start + name;
                            candidates.add(new AutocompletionCandidate<>(
                                    key,
                                    preParts + key,
                                    preParts + key,
                                    Collections.emptyList(),
                                    Icons.AUTOCOMPLETION_REGISTER
                            ));
                        }
                        candidates.add(new AutocompletionCandidate<>(
                                name,
                                preParts + name,
                                preParts + firstStart + name,
                                Collections.emptyList(),
                                Icons.AUTOCOMPLETION_REGISTER
                        ));
                    }
                    hasRegisters = true;
                }
            }
        }
    }

    private void addMacroParameters(EditorElementMacro macro) {
        for (String parameterName : macro.getParameterNames()) {
            candidates.add(new AutocompletionCandidate<>(
                    parameterName,
                    parameterName,
                    parameterName,
                    List.of(macro.getText()),
                    Icons.AUTOCOMPLETION_REGISTER
            ));
        }
    }

    private String parseParameters(ParameterType[] types) {
        StringBuilder builder = new StringBuilder();
        for (ParameterType type : types) {
            builder.append(type.getExample()).append(" ");
        }
        return builder.length() == 0 ? " " : builder.substring(0, builder.length() - 1);
    }
}
