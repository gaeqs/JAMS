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
import net.jamsimulator.jams.mips.directive.Directive;
import net.jamsimulator.jams.mips.directive.defaults.DirectiveEqv;
import net.jamsimulator.jams.mips.directive.defaults.DirectiveGlobl;
import net.jamsimulator.jams.mips.directive.defaults.DirectiveMacro;
import net.jamsimulator.jams.project.mips.MIPSProject;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MIPSEditorDirective extends EditorIndexedParentElementImpl {

    private Directive directive;

    public MIPSEditorDirective(EditorIndex index, EditorIndexedParentElement parent,
                               int start, String text) {
        super(index, parent, start, text);
        parseText();
    }

    @Override
    public String getIdentifier() {
        return text.substring(1);
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
        int i = 0;
        for (var entry : parameters) {
            elements.add(parseParameter(i++, start + entry.getKey(), entry.getValue()));
        }
    }

    protected MIPSEditorDirectiveMnemonic parseMnemonic(int start, String mnemonic) {
        return new MIPSEditorDirectiveMnemonic(index, this, start, mnemonic);
    }

    protected MIPSEditorDirectiveParameter parseParameter(int index, int start, String parameter) {
        if (directive instanceof DirectiveMacro) {
            if (index == 0) {
                return new MIPSEditorDirectiveMacroName(this.index, this, start, parameter);
            } else {
                return new MIPSEditorDirectiveParameter(this.index, this, start, parameter);
            }
        }
        if(directive instanceof DirectiveGlobl) {
            return new MIPSEditorDirectiveGlobalMarker(this.index, this, start, parameter);
        }
        return new MIPSEditorDirectiveParameter(this.index, this, start, parameter);
    }
}
