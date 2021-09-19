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

import net.jamsimulator.jams.event.EventBroadcast;
import net.jamsimulator.jams.gui.editor.code.hint.EditorHintBar;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorElementReference;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorReferencedElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorReferencingElement;
import net.jamsimulator.jams.gui.editor.code.indexing.event.IndexRequestRefreshEvent;
import net.jamsimulator.jams.gui.editor.code.indexing.global.ProjectGlobalIndex;
import net.jamsimulator.jams.gui.editor.code.indexing.inspection.Inspector;
import net.jamsimulator.jams.project.Project;
import net.jamsimulator.jams.utils.Labeled;
import org.fxmisc.richtext.model.StyleSpans;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public interface EditorIndex extends EventBroadcast, Labeled {

    Project getProject();

    Set<Inspector<?>> getInspectors();

    Optional<ProjectGlobalIndex> getGlobalIndex();

    void setGlobalIndex(ProjectGlobalIndex index);

    Optional<EditorHintBar> getHintBar();

    void setHintBar(EditorHintBar hintBar);

    boolean isInitialized();

    void waitForInitialization() throws InterruptedException;

    boolean isInEditMode();

    void change(EditorLineChange change);

    void indexAll(String text);

    Optional<EditorIndexedElement> getElementAt(int position);

    Set<EditorElementReference<?>> getAllReferencedReferences();

    Set<EditorElementReference<?>> getAllReferencingReferences();

    @Override
    void transferListenersTo(EventBroadcast broadcast);

    <T extends EditorReferencedElement>
    Optional<T> getReferencedElement(EditorElementReference<T> reference, boolean globalContext);

    <T extends EditorReferencedElement>
    Set<T> getReferencedElements(EditorElementReference<T> reference, boolean globalContext);

    <T extends EditorReferencedElement>
    Set<T> getReferencedElementsOfType(Class<T> type, boolean globalContext);

    <T extends EditorReferencedElement>
    Set<EditorReferencingElement<?>> getReferecingElements(EditorElementReference<T> reference);

    Stream<? extends EditorIndexedElement> elementStream();

    boolean isIdentifierGlobal(String global);

    Optional<StyleSpans<Collection<String>>> getStyleForLine(int line);

    Optional<StyleSpans<Collection<String>>> getStyleRange(int from, int to);

    void inspectElementsWithReferences(Set<EditorElementReference<?>> references);

    void lock(boolean editMode);

    void unlock(boolean finishEditMode);

    boolean isLocked();

    default void withLock(boolean editMode, Consumer<EditorIndex> consumer) {
        lock(editMode);
        try {
            consumer.accept(this);
        } finally {
            unlock(editMode);
        }
    }

    default <T> T withLockF(boolean editMode, Function<EditorIndex, T> consumer) {
        lock(editMode);
        try {
            return consumer.apply(this);
        } finally {
            unlock(editMode);
        }
    }

    default void requestRefresh() {
        callEvent(new IndexRequestRefreshEvent(this));
    }
}
