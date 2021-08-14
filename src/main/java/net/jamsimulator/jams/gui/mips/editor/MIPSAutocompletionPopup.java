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
import net.jamsimulator.jams.gui.editor.popup.AutocompletionPopup;
import net.jamsimulator.jams.gui.image.icon.IconData;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.mips.editor.element.*;
import net.jamsimulator.jams.mips.directive.Directive;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.pseudo.PseudoInstruction;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.project.mips.MIPSProject;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.Collections;
import java.util.HashSet;
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

    private final MIPSFileElements mipsElements;
    private MIPSCodeElement element;

    public MIPSAutocompletionPopup(MIPSFileEditor display) {
        super(display);
        this.mipsElements = display.getElements();
    }

    @Override
    public MIPSFileEditor getDisplay() {
        return (MIPSFileEditor) super.getDisplay();
    }

    @Override
    public void execute(int caretOffset, boolean autocompleteIfOne) {
        int caretPosition = display.getCaretPosition() + caretOffset;
        if (caretPosition <= 0) return;
        element = mipsElements.getElementAt(caretPosition - 1).orElse(null);
        if (element == null) {
            hide();
            return;
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
        elements.clear();

        var to = caretPosition - element.getStartIndex();
        var start = element.getSimpleText();

        if (to > 0 && to < start.length()) {
            start = start.substring(0, caretPosition - element.getStartIndex());
        }

        if (element instanceof MIPSDirective)
            start = refreshDirective(start);
        else if (element instanceof MIPSDirectiveParameter) {
            start = refreshDisplayDirectiveParameter(start);
        } else if (element instanceof MIPSInstruction)
            start = refreshInstructionsMacrosAndDirectives(start);
        else if (element instanceof MIPSInstructionParameterPart)
            start = refreshDisplayInstructionParameterPart(start);

        sortAndShowElements(start);

        if (isEmpty()) return;
        selectedIndex = 0;
        refreshSelected();
    }

    protected String refreshDirective(String start) {
        MIPSProject project = getDisplay().getProject().orElse(null);
        if (project == null) return start;

        var space = Jams.getMainConfiguration()
                .getEnum(MIPSSpaces.class, "editor.mips.space_after_directive")
                .orElse(MIPSSpaces.SPACE).getValue();

        String directive = start.substring(1);
        addElements(project.getData().getDirectiveSet().getDirectives().stream().filter(target -> target.getName().startsWith(directive)),
                Directive::getName, d -> "." + d.getName() + (d.hasParameters() ? space : ""), 0, ICON_DIRECTIVE);
        return directive;
    }

    protected String refreshInstructionsMacrosAndDirectives(String start) {
        MIPSProject project = getDisplay().getProject().orElse(null);
        if (project == null) return start;


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
        addElements(mipsElements.getMacros().stream().filter(target -> target.getName().startsWith(directive)),
                MIPSMacro::getName, m -> m.getName() + " (", 0, ICON_MACRO);

        return directive;
    }

    protected String refreshDisplayInstructionParameterPart(String start) {
        MIPSProject project = getDisplay().getProject().orElse(null);
        if (project == null) return start;

        var part = (MIPSInstructionParameterPart) element;
        var parameter = part.getParameter();
        int parameterIndex = part.getParameter().getIndex();

        var line = getDisplay().getElements().getLineWithPosition(element.getStartIndex());
        var compatibleInstructions = line.getInstruction()
                .map(target -> target.getCompatibleInstructions(mipsElements, parameterIndex))
                .orElse(Collections.emptySet());

        boolean hasLabels = false, hasRegisters = false;

        for (Instruction instruction : compatibleInstructions) {
            var partStartIndex = new AtomicInteger();
            var partType = instruction.getParameters()[parameterIndex]
                    .getPartAt(display.getCaretPosition() - parameter.getStart(), parameter.getText(), partStartIndex);

            var partStart = parameter.getText().substring(partStartIndex.get(),
                    getDisplay().getCaretPosition() - parameter.getStart()).toLowerCase();

            switch (partType) {
                case LABEL:
                    if (hasLabels) break;
                    Set<String> labels = new HashSet<>(mipsElements.getLabels());
                    mipsElements.getFilesToAssemble().ifPresent(files -> labels.addAll(files.getGlobalLabels()));
                    addElements(labels.stream().filter(target -> target.toLowerCase().startsWith(partStart)), s -> s, s -> s, partStartIndex.get(), ICON_LABEL);
                    hasLabels = true;
                    break;
                case REGISTER:
                    if (hasRegisters) break;
                    Set<String> names = project.getData().getRegistersBuilder().getRegistersNames();
                    Set<Character> starts = project.getData().getRegistersBuilder().getValidRegistersStarts();

                    starts.forEach(c -> addElements(names.stream()
                            .filter(target -> target.toLowerCase().startsWith(partStart)
                                    || (c + target.toLowerCase()).startsWith(partStart)), s -> c + s, s -> c + s, partStartIndex.get(), ICON_REGISTER));

                    hasRegisters = true;
                    break;
                case STRING:
                case IMMEDIATE:
                default:
                    break;
            }
        }
        return start;
    }

    protected String refreshDisplayDirectiveParameter(String start) {
        MIPSProject project = getDisplay().getProject().orElse(null);
        if (project == null || element == null) return start;

        var parameter = (MIPSDirectiveParameter) element;
        var directive = parameter.getDirective().getDirective();

        //Checks whether and what space should add.
        var shouldAddSpace = directive.isPresent()
                && (directive.get().canRepeatLastParameter() || directive.get().getParametersAmount() - 1 > parameter.getIndex());
        var space = shouldAddSpace
                ? Jams.getMainConfiguration()
                .getEnum(MIPSSpaces.class, "editor.mips.space_after_directive_parameter")
                .orElse(MIPSSpaces.SPACE).getValue()
                : "";

        var offset = getDisplay().getCaretPosition() - parameter.getStartIndex();
        var parameterStart = parameter.getText().substring(0, offset).toLowerCase();

        switch (parameter.getType()) {
            case LABEL, INT_OR_LABEL -> {
                Set<String> labels = new HashSet<>(mipsElements.getLabels());
                mipsElements.getFilesToAssemble().ifPresent(files -> labels.addAll(files.getGlobalLabels()));
                addElements(labels.stream().filter(target -> target.toLowerCase().startsWith(parameterStart)), s -> s, s -> s + space, 0, ICON_LABEL);
            }
        }

        return start;
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
        MIPSCodeElement element = mipsElements.getElementAt(caretPosition - 1).orElse(null);
        if (element == null) return;
        if (element.getText().substring(0, caretPosition - element.getStartIndex()).equals(replacement)) return;

        display.replaceText(element.getStartIndex() + selected.getOffset(), caretPosition, replacement);
    }
}
