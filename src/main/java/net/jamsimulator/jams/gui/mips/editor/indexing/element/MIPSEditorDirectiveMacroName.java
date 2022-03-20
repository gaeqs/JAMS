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

import net.jamsimulator.jams.gui.editor.code.indexing.EditorIndex;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedParentElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.ElementScope;
import net.jamsimulator.jams.gui.editor.code.indexing.element.basic.EditorElementMacro;
import net.jamsimulator.jams.gui.editor.code.indexing.element.basic.EditorElementMacroParameter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MIPSEditorDirectiveMacroName extends MIPSEditorDirectiveParameter implements EditorElementMacro {

    private final String identifier;
    private final int parameterAmount;
    private final List<String> rawParameters;

    private List<EditorElementMacroParameter> parameters = null;
    private ElementScope macroScope;

    public MIPSEditorDirectiveMacroName(EditorIndex index, ElementScope scope, EditorIndexedParentElement parent,
                                        int start, String text, int parameters, List<String> rawParameters) {
        super(index, scope, parent, start, text);
        macroScope = new ElementScope(text, scope);
        identifier = text + "-" + parameters;
        parameterAmount = parameters;
        this.rawParameters = rawParameters;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public ElementScope getReferencedScope() {
        if (ElementScope.GLOBAL.equals(scope.parent())) {
            return index.isIdentifierGlobal(text) ? ElementScope.GLOBAL : scope;
        }
        return scope;
    }

    @Override
    public Collection<String> getStyles() {
        return getReferencedScope().equals(ElementScope.GLOBAL)
                ? EditorElementMacro.NAME_GLOBAL_STYLE
                : EditorElementMacro.NAME_STYLE;
    }

    @Override
    public void changeScope(ElementScope scope) {
        super.changeScope(scope);
        macroScope = new ElementScope(text, scope);
    }

    @Override
    public int getParameterAmount() {
        return parameterAmount;
    }

    @Override
    public ElementScope getMacroScope() {
        return macroScope;
    }

    @Override
    public List<EditorElementMacroParameter> getParameters() {
        if (parameters == null) {
            if (parent == null) {
                parameters = Collections.emptyList();
            } else {
                parameters = parent.elementStream()
                        .filter(it -> it instanceof EditorElementMacroParameter)
                        .map(it -> (EditorElementMacroParameter) it)
                        .toList();
            }
        }
        return parameters;
    }

    @Override
    public List<String> getParameterNames() {
        return getParameters().stream().map(EditorIndexedElement::getIdentifier).toList();
    }

    @Override
    public List<String> getRawParameters() {
        return rawParameters;
    }
}
