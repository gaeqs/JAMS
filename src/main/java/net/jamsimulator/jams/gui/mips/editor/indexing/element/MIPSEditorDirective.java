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

package net.jamsimulator.jams.gui.mips.editor.indexing.element;

import javafx.util.Pair;
import net.jamsimulator.jams.gui.editor.code.indexing.EditorIndex;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedParentElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedParentElementImpl;
import net.jamsimulator.jams.gui.editor.code.indexing.element.ElementScope;
import net.jamsimulator.jams.gui.editor.code.indexing.element.basic.EditorElementMacro;
import net.jamsimulator.jams.gui.editor.code.indexing.element.basic.EditorElementMacroParameter;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.mips.directive.Directive;
import net.jamsimulator.jams.mips.directive.defaults.DirectiveEqv;
import net.jamsimulator.jams.mips.directive.defaults.DirectiveGlobl;
import net.jamsimulator.jams.mips.directive.defaults.DirectiveLab;
import net.jamsimulator.jams.mips.directive.defaults.DirectiveMacro;
import net.jamsimulator.jams.project.mips.MIPSProject;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class MIPSEditorDirective extends EditorIndexedParentElementImpl {

    private Directive directive;

    public MIPSEditorDirective(EditorIndex index, ElementScope scope, EditorIndexedParentElement parent,
                               int start, String text) {
        super(index, scope, parent, start, text, Messages.MIPS_ELEMENT_DIRECTIVE);
        parseText();
    }

    @Override
    public String getIdentifier() {
        return text.substring(1);
    }

    @Override
    public void changeScope(ElementScope scope) {
        super.changeScope(scope);
        if (size() > 1 && getElement(1) instanceof EditorElementMacro macro) {
            var macroScope = macro.getMacroScope();
            elements.stream().filter(it -> it instanceof EditorElementMacroParameter)
                    .forEach(it -> it.changeScope(macroScope));
        }
    }

    public Optional<Directive> getDirective() {
        return Optional.ofNullable(directive);
    }

    public boolean isValidEquivalent() {
        return directive instanceof DirectiveEqv && elements.size() == 2;
    }

    protected void parseText() {
        var parts = StringUtils.multiSplitIgnoreInsideStringWithIndex(text, false, " ", ",", "\t");
        if (parts.isEmpty()) return;

        //Sorts all entries by their indices.
        var stringParameters = parts.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey)).collect(Collectors.toList());

        //The first entry is the directive itself.
        var first = stringParameters.get(0);

        if (index.getProject() instanceof MIPSProject project) {
            var set = project.getData().getDirectiveSet();
            directive = set.getDirective(first.getValue().substring(1)).orElse(null);
        } else {
            directive = null;
        }

        elements.add(parseMnemonic(start + first.getKey(), first.getValue()));
        stringParameters.remove(0);


        //Adds all parameters.
        parseParameters(stringParameters);
    }

    protected void parseParameters(List<Map.Entry<Integer, String>> parameters) {
        if (parameters.isEmpty()) return;
        if (directive instanceof DirectiveMacro) {
            // Special case

            var mnemonic = parameters.remove(0);

            var sanitized = new ArrayList<Pair<Integer, String>>(parameters.size());
            int i = 0;
            for (var entry : parameters) {
                var parameter = sanityMacroParameter(
                        i++, entry.getKey(),
                        parameters.size(), entry.getValue());
                if (parameter != null) {
                    sanitized.add(parameter);
                }
            }

            var mnemonicElement = new MIPSEditorDirectiveMacroName(
                    index,
                    scope,
                    this,
                    start + mnemonic.getKey(),
                    mnemonic.getValue(),
                    sanitized.size(),
                    parameters.stream().map(Map.Entry::getValue).toList()
            );
            elements.add(mnemonicElement);

            var macroScope = mnemonicElement.getMacroScope();
            for (var parameter : sanitized) {
                elements.add(new MIPSEditorDirectiveMacroParameter(
                        index,
                        macroScope,
                        this,
                        start + parameter.getKey(),
                        parameter.getValue()
                ));
            }
        } else {
            for (var entry : parameters) {
                var parameter = parseParameter(start + entry.getKey(), entry.getValue());
                if (parameter != null) {
                    elements.add(parameter);
                }
            }
        }
    }

    protected MIPSEditorDirectiveMnemonic parseMnemonic(int start, String mnemonic) {
        return new MIPSEditorDirectiveMnemonic(index, scope, this, start, mnemonic);
    }

    protected MIPSEditorDirectiveParameter parseParameter(int start, String parameter) {
        if (directive instanceof DirectiveLab) {
            return new MIPSEditorDirectiveLabParameter(this.index, scope, this, start, parameter);
        }
        if (directive instanceof DirectiveGlobl) {
            return new MIPSEditorDirectiveGlobalMarker(this.index, scope, this, start, parameter);
        }
        return new MIPSEditorDirectiveParameter(this.index, scope, this, start, parameter);
    }

    protected Pair<Integer, String> sanityMacroParameter(int index, int start, int size, String parameter) {
        if (index == 0) {
            if (parameter.equals("(") || parameter.equals("()")) {
                return null;
            }
            if (parameter.startsWith("(")) {
                parameter = parameter.substring(1);
                start++;
            }
        }
        if (index == size - 1) {
            if (parameter.equals(")")) return null;
            if (parameter.endsWith(")")) {
                parameter = parameter.substring(0, parameter.length() - 1);
            }
        }
        return new Pair<>(start, parameter);
    }
}
