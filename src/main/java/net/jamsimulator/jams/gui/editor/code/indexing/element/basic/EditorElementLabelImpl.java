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

package net.jamsimulator.jams.gui.editor.code.indexing.element.basic;

import net.jamsimulator.jams.gui.editor.code.indexing.EditorIndex;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedElementImpl;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedParentElement;

import java.util.Collection;
import java.util.Set;

public class EditorElementLabelImpl extends EditorIndexedElementImpl implements EditorElementLabel {

    public static final Set<String> STYLE = Set.of("label");
    public static final Set<String> GLOBAL_STYLE = Set.of("global-label");

    private final String identifier;

    public EditorElementLabelImpl(EditorIndex index, EditorIndexedParentElement parent,
                                  int start, String text) {
        super(index, parent, start, text);
        identifier = text.substring(0, text.length() - 1).trim();
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Collection<String> getStyles() {
        return index.isIdentifierGlobal(identifier) ? GLOBAL_STYLE : STYLE;
    }
}
