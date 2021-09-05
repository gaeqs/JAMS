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

package net.jamsimulator.jams.gui.editor.code.indexing;

import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorElementReference;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorReferencedElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorReferencingElement;
import net.jamsimulator.jams.gui.editor.code.indexing.global.ProjectGlobalIndex;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface EditorIndex {

    Optional<ProjectGlobalIndex> getGlobalIndex();

    void setGlobalIndex(ProjectGlobalIndex index);

    boolean isInitialized();

    void change(EditorLineChange change);

    void indexAll(String text);

    Optional<EditorIndexedElement> getElementAt(int position);

    void lockIndex();

    void unlockIndex();

    boolean isLocked();

    <T extends EditorReferencedElement>
    Optional<T> getReferencedElement(EditorElementReference<T> reference, boolean globalContext);

    <T extends EditorReferencedElement>
    Set<EditorReferencingElement> getReferecingElements(EditorElementReference<T> reference);

    Stream<? extends EditorIndexedElement> elementStream();

    default void withLock(Consumer<EditorIndex> consumer) {
        lockIndex();
        consumer.accept(this);
        unlockIndex();
    }

}
