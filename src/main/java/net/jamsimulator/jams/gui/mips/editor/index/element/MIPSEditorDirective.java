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
import net.jamsimulator.jams.project.mips.MIPSProject;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.Comparator;
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
        elements.add(new MIPSEditorDirectiveMnemonic(index, this,
                start + first.getKey(), first.getValue()));

        stringParameters.remove(0);


        if (index.getProject() instanceof MIPSProject project) {
            var set = project.getData().getDirectiveSet();
            directive = set.getDirective(getIdentifier()).orElse(null);
        } else {
            directive = null;
        }

        //Adds all parameters.
        for (var entry : stringParameters) {
            elements.add(new MIPSEditorDirectiveParameter(
                    index,
                    this,
                    start + entry.getKey(),
                    entry.getValue()
            ));
        }
    }
}
