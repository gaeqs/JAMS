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
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexStyleableElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedElementImpl;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedParentElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.ElementScope;

import java.util.Collection;
import java.util.Set;

public class MIPSEditorInstructionMnemonic extends EditorIndexedElementImpl implements EditorIndexStyleableElement {

    public static final Set<String> STYLE = Set.of("instruction");
    public static final Set<String> PSEUDO_STYLE = Set.of("instruction", "pseudo-instruction");

    protected final boolean pseudo;

    public MIPSEditorInstructionMnemonic(EditorIndex index, ElementScope scope, EditorIndexedParentElement parent,
                                         int start, String text, boolean pseudo) {
        super(index, scope, parent, start, text);
        this.pseudo = pseudo;
    }

    public boolean isPseudo() {
        return pseudo;
    }

    @Override
    public Collection<String> getStyles() {
        return pseudo ? PSEUDO_STYLE : STYLE;
    }
}
