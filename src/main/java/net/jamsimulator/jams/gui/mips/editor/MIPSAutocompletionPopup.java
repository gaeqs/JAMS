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

package net.jamsimulator.jams.gui.mips.editor;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.ElementScope;
import net.jamsimulator.jams.gui.editor.code.indexing.element.basic.EditorElementLabel;
import net.jamsimulator.jams.gui.editor.code.indexing.element.basic.EditorElementMacro;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorElementReference;
import net.jamsimulator.jams.gui.editor.code.popup.AutocompletionPopup;
import net.jamsimulator.jams.gui.image.icon.IconData;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.mips.editor.indexing.MIPSEditorIndex;
import net.jamsimulator.jams.gui.mips.editor.indexing.element.*;
import net.jamsimulator.jams.mips.directive.Directive;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.pseudo.PseudoInstruction;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.project.mips.MIPSProject;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class MIPSAutocompletionPopup extends AutocompletionPopup {

    private static final IconData ICON_INSTRUCTION = Icons.AUTOCOMPLETION_INSTRUCTION;
    private static final IconData ICON_PSEUDO_INSTRUCTION = Icons.AUTOCOMPLETION_PSEUDO_INSTRUCTION;
    private static final IconData ICON_DIRECTIVE = Icons.AUTOCOMPLETION_DIRECTIVE;
    private static final IconData ICON_LABEL = Icons.AUTOCOMPLETION_LABEL;
    private static final IconData ICON_REGISTER = Icons.AUTOCOMPLETION_REGISTER;
    private static final IconData ICON_MACRO = Icons.AUTOCOMPLETION_MACRO;
    private static final IconData ICON_MACRO_PARAMETER = Icons.AUTOCOMPLETION_MACRO_PARAMETER;

    private final MIPSEditorIndex index;
    private EditorIndexedElement element;

    public MIPSAutocompletionPopup(MIPSFileEditor display) {
        super(display);
        this.index = display.getIndex();
    }

    @Override
    public MIPSFileEditor getDisplay() {
        return (MIPSFileEditor) super.getDisplay();
    }

    @Override
    public void execute(int caretOffset, boolean autocompleteIfOne) {
        int caretPosition = display.getCaretPosition() + caretOffset;
        if (caretPosition <= 0) return;
        try {
            index.lock(false);
            element = index.getElementAt(caretPosition - 1).orElse(null);
            if (element == null) {
                hide();
                return;
            }
        } finally {
            index.unlock(false);
        }

        Platform.runLater(() -> {
            refreshContents(caretPosition);
            if (isEmpty()) {
                hide();
                return;
            }
            if (autocompleteIfOne && size() == 1) {
                hide();
                autocomplete();
            } else {
                Bounds bounds = display.getCaretBounds().orElse(null);
                if (bounds == null) return;

                var zoomX = display.getZoom().getZoom().getX();
                var zoomY = display.getZoom().getZoom().getY();

                if (zoomX < 1 || zoomY < 1) {
                    zoomX = zoomY = 1;
                } else {
                    zoomX = zoomX * 0.5 + 0.5;
                    zoomY = zoomY * 0.5 + 0.5;
                }

                scroll.setScaleX(zoomX);
                scroll.setScaleY(zoomY);

                show(display, bounds.getMinX(), bounds.getMinY()
                        + 20 * display.getZoom().getZoom().getY());
            }

        });
    }

    @Override
    public void refreshContents(int caretPosition) {
        try {
            index.lock(false);
            elements.clear();

            var to = caretPosition - element.getStart();
            var start = element.getIdentifier();

            if (to > 0 && to < start.length()) {
                start = start.substring(0, caretPosition - element.getStart());
            }

            if (element instanceof MIPSEditorDirectiveMnemonic)
                start = refreshDirective(start);
            else if (element instanceof MIPSEditorDirectiveParameter) {
                start = refreshDisplayDirectiveParameter(start);
            } else if (element instanceof MIPSEditorInstructionMnemonic)
                start = refreshInstructionsMacrosAndDirectives(start);
            else if (element instanceof MIPSEditorInstructionParameterPart)
                start = refreshDisplayInstructionParameterPart(start);

            addMacroParameters();

            sortAndShowElements(start);

            if (isEmpty()) return;
            selectedIndex = 0;
            refreshSelected();
        } finally {
            index.unlock(false);
        }
    }

    protected String refreshDirective(String start) {
        if (!(getDisplay().getProject() instanceof MIPSProject project)) return start;

        var space = Jams.getMainConfiguration()
                .getEnum(MIPSSpaces.class, "editor.mips.space_after_directive")
                .orElse(MIPSSpaces.SPACE).getValue();

        addElements(project.getData().getDirectiveSet().getDirectives().stream().filter(target -> target.getName().startsWith(start)),
                Directive::getName, d -> "." + d.getName() + (d.hasParameters() ? space : ""), 0, ICON_DIRECTIVE);
        return start;
    }

    protected String refreshInstructionsMacrosAndDirectives(String start) {
        if (!(getDisplay().getProject() instanceof MIPSProject project)) return start;

        var space = Jams.getMainConfiguration()
                .getEnum(MIPSSpaces.class, "editor.mips.space_after_instruction")
                .orElse(MIPSSpaces.SPACE).getValue();

        Stream<Instruction> pseudoStream = project.getData().getInstructionSet().getInstructions().stream()
                .filter(target -> target.getMnemonic().startsWith(start.toLowerCase()))
                .filter(target -> target instanceof PseudoInstruction);

        Stream<Instruction> nonPseudoStream = project.getData().getInstructionSet().getInstructions().stream()
                .filter(target -> target.getMnemonic().startsWith(start.toLowerCase()))
                .filter(target -> !(target instanceof PseudoInstruction));


        addElements(nonPseudoStream, i -> i.getMnemonic() + " \t"
                + StringUtils.addSpaces(parseParameters(i.getParameters()), 25, true)
                + i.getName(), i -> i.getMnemonic() + (i.hasParameters() ? space : ""), 0, ICON_INSTRUCTION);

        addElements(pseudoStream, i -> i.getMnemonic() + " \t"
                + StringUtils.addSpaces(parseParameters(i.getParameters()), 25, true)
                + i.getName(), i -> i.getMnemonic() + (i.hasParameters() ? space : ""), 0, ICON_PSEUDO_INSTRUCTION);

        //Add directives too!
        String directive = start.startsWith(".") ? start.substring(1) : start;
        addElements(project.getData().getDirectiveSet().getDirectives().stream().filter(target -> target.getName().startsWith(directive)),
                Directive::getName, d -> "." + d.getName() + (d.hasParameters() ? space : ""), 0, ICON_DIRECTIVE);

        // And macros!

        var macros =
                index.getReferencedElementsOfType(MIPSEditorDirectiveMacroName.class, element.getReferencingScope());
        index.getGlobalIndex().ifPresent(files ->
                macros.addAll(files.searchReferencedElementsOfType(MIPSEditorDirectiveMacroName.class)));

        addElements(macros.stream().filter(target -> target.getIdentifier().startsWith(directive)),
                MIPSEditorDirectiveMacroName::getIdentifier, m -> m.getIdentifier() + " (", 0, ICON_MACRO);

        return directive;
    }

    protected String refreshDisplayInstructionParameterPart(String start) {
        var project = getDisplay().getProject() instanceof MIPSProject p ? p : null;
        if (project == null) return start;

        var part = (MIPSEditorInstructionParameterPart) element;
        var parameter = (MIPSEditorInstructionParameter) part.getParent().orElse(null);
        if (parameter == null) return start;
        var ins = (MIPSEditorInstruction) parameter.getParent().orElse(null);
        if (ins == null) return start;
        int parameterIndex = parameter.indexInParent() - 1;

        var compatibleInstructions = ins.getCompatibleInstructions(parameterIndex);
        boolean hasLabels = false, hasRegisters = false;

        for (Instruction instruction : compatibleInstructions) {
            var partStartIndex = new AtomicInteger();
            var partType = instruction.getParameters()[parameterIndex]
                    .getPartAt(display.getCaretPosition() - parameter.getStart(), parameter.getText(), partStartIndex);

            var partStart = parameter.getText().substring(partStartIndex.get(),
                    getDisplay().getCaretPosition() - parameter.getStart()).toLowerCase();

            switch (partType) {
                case LABEL -> {
                    if (hasLabels) break;
                    var labels =
                            index.getReferencedElementsOfType(EditorElementLabel.class, element.getReferencingScope());
                    index.getGlobalIndex().ifPresent(files ->
                            labels.addAll(files.searchReferencedElementsOfType(EditorElementLabel.class)));
                    addElements(labels.stream().filter(target ->
                                    target.getIdentifier().toLowerCase().startsWith(partStart)),
                            EditorElementLabel::getIdentifier, EditorElementLabel::getIdentifier,
                            partStartIndex.get(), ICON_LABEL);
                    hasLabels = true;
                }
                case REGISTER -> {
                    if (hasRegisters) break;
                    Set<String> names = project.getData().getRegistersBuilder().getRegistersNames();
                    Set<Character> starts = project.getData().getRegistersBuilder().getValidRegistersStarts();
                    starts.forEach(c -> addElements(names.stream()
                            .filter(target -> target.toLowerCase().startsWith(partStart)
                                    || (c + target.toLowerCase()).startsWith(partStart)), s -> c + s, s -> c + s,
                            partStartIndex.get(), ICON_REGISTER));
                    hasRegisters = true;
                }
            }
        }
        return start;
    }

    protected String refreshDisplayDirectiveParameter(String start) {
        if (!(getDisplay().getProject() instanceof MIPSProject project)) return start;
        if (element == null) return start;

        var parameter = (MIPSEditorDirectiveParameter) element;
        var editorDirective =
                parameter.getParent().orElse(null) instanceof MIPSEditorDirective d ? d : null;
        if (editorDirective == null) return start;
        var directive = editorDirective.getDirective();

        //Checks whether and what space should add.
        var shouldAddSpace = directive.isPresent()
                && (directive.get().canRepeatLastParameter() ||
                directive.get().getParametersAmount() - 1 > parameter.indexInParent());
        var space = shouldAddSpace
                ? Jams.getMainConfiguration()
                .getEnum(MIPSSpaces.class, "editor.mips.space_after_directive_parameter")
                .orElse(MIPSSpaces.SPACE).getValue()
                : "";

        var offset = getDisplay().getCaretPosition() - parameter.getStart();
        var parameterStart = parameter.getText().substring(0, offset).toLowerCase();

        switch (parameter.getType()) {
            case LABEL, INT_OR_LABEL -> {
                var labels =
                        index.getReferencedElementsOfType(EditorElementLabel.class, element.getReferencingScope());
                index.getGlobalIndex().ifPresent(files ->
                        labels.addAll(files.searchReferencedElementsOfType(EditorElementLabel.class)));
                addElements(labels.stream().filter(target -> target.getIdentifier().toLowerCase().startsWith(parameterStart)),
                        EditorElementLabel::getIdentifier, s -> s.getIdentifier() + space, 0, ICON_LABEL);
            }
        }

        return start;
    }

    protected void addMacroParameters() {
        var scope = element.getReferencingScope();
        if (scope.type() == ElementScope.Type.MACRO) {
            // Add macros!
            var reference = new EditorElementReference<>(EditorElementMacro.class, scope.macroIdentifier());
            var macro = index.getReferencedElement(reference, scope);
            if (macro.isEmpty()) return;

            var offset = getDisplay().getCaretPosition() - element.getStart();
            var startWithPercentage = element.getIdentifier().startsWith("%");
            var id = startWithPercentage
                    ? element.getIdentifier().toLowerCase().substring(0, offset)
                    : "%" + element.getIdentifier().toLowerCase().substring(0, offset);

            addElements(macro.get().getParameters().stream().filter(it -> it.toLowerCase().startsWith(id)),
                    it -> it, it -> it, 0, ICON_MACRO_PARAMETER);

        }
    }


    protected String parseParameters(ParameterType[] types) {
        StringBuilder builder = new StringBuilder();
        for (ParameterType type : types) {
            builder.append(type.getExample()).append(" ");
        }
        return builder.length() == 0 ? " " : builder.substring(0, builder.length() - 1);
    }

    @Override
    public void autocomplete() {
        if (isEmpty()) return;
        String replacement = selected.getAutocompletion();

        int caretPosition = display.getCaretPosition();
        if (caretPosition == 0) return;

        var element = index.withLockF(false,
                i -> i.getElementAt(caretPosition - 1).orElse(null));
        if (element == null) return;
        if (element.getText().substring(0, caretPosition - element.getStart()).equals(replacement)) return;

        display.replaceText(element.getStart() + selected.getOffset(), caretPosition, replacement);
    }
}
