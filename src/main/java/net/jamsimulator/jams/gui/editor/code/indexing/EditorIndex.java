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
import net.jamsimulator.jams.gui.editor.code.indexing.element.ElementScope;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorElementReference;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorReferencedElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorReferencingElement;
import net.jamsimulator.jams.gui.editor.code.indexing.event.IndexRequestRefreshEvent;
import net.jamsimulator.jams.gui.editor.code.indexing.global.ProjectGlobalIndex;
import net.jamsimulator.jams.gui.editor.code.indexing.inspection.Inspector;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.project.Project;
import net.jamsimulator.jams.utils.Labeled;
import org.fxmisc.richtext.model.StyleSpans;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Represents the indexing system of a {@link net.jamsimulator.jams.gui.editor.code.CodeFileEditor CodeFileEditor}.
 *
 * <h2>Philosophy:</h2>
 * The index if based in several points:
 * - Element based. Elements must be almost immutables: only its inspections, scope and start can be changed.
 * - Elements are recreated when its line is edited.
 * - Elements store the minimum informaiton possible. This information must be searched using the searching methods.
 * - Searching methods must be fast as possible.
 * <p>
 * There are two classes that extends {@link EditorIndexedElement} used to fulfill these requirements:
 * {@link EditorReferencedElement} and {@link EditorReferencingElement}. Instances of these classes contains
 * {@link EditorElementReference}s. These references can be used to quickly search the referenced or referencing
 * elements in the index.
 *
 * <h2>Interacting with the index:</h2>
 * <p>
 * The index is a thread-safe element. To interact with it, you must lock the object using {@link #lock(boolean)}.
 * You must also specify if you will edit the index. If you edit the index,
 * a {@link net.jamsimulator.jams.gui.editor.code.indexing.event.IndexFinishEditEvent finish event}
 * and a {@link IndexRequestRefreshEvent request refresh event} will be sent.
 *
 * <h2>Refreshing:</h2>
 * <p>
 * A {@link IndexRequestRefreshEvent refresh event} will be sent when the index is edited, but an external
 * source can also call this event using {@link #requestRefresh()}.
 */
public interface EditorIndex extends EventBroadcast, Labeled {

    /**
     * Returns the {@link Project} linked to the file this index represents.
     * This method doesn't require locking.
     * <p>
     * The project cannot be changed once the index is created.
     *
     * @return the {@link Project}.
     */
    Project getProject();

    /**
     * Returns the {@link Manager} that provides the inspectors to this index.
     * This method doesn't require locking.
     * <p>
     * The manager cannot be changed once the index is created.
     * <p>
     * The index must listen manager's changes.
     *
     * @return the {@link Manager inspector manager}.
     */
    Manager<? extends Inspector<?>> getInspectorManager();

    /**
     * Returns the {@link ProjectGlobalIndex} this index is part of.
     * This method doesn't require locking.
     * <p>
     * You may change the {@link ProjectGlobalIndex} using {@link #setGlobalIndex(ProjectGlobalIndex)}.
     *
     * @return the {@link ProjectGlobalIndex} if present.
     */
    Optional<ProjectGlobalIndex> getGlobalIndex();

    /**
     * Sets the {@link ProjectGlobalIndex} this index is part of.
     * This method requires locking with edit mode.
     * <p>
     * Due to initialization reasons, this method won't refresh any inspection.
     *
     * @param index the {@link ProjectGlobalIndex}. It may be null.
     */
    void setGlobalIndex(ProjectGlobalIndex index);

    /**
     * Returns the {@link EditorHintBar} linked to this index.
     * This method doesn't require locking.
     *
     * @return the {@link EditorHintBar} if present.
     */
    Optional<EditorHintBar> getHintBar();

    /**
     * Sets the {@link EditorHintBar} linked to this index.
     * This method requires locking, but not edit mode.
     * <p>
     * The given {@link EditorHintBar} will be refreshed automatically.
     *
     * @param hintBar the {@link EditorHintBar}. It may be null.
     */
    void setHintBar(EditorHintBar hintBar);

    /**
     * Returns whether this index is initialized.
     * This method doesn't require locking.
     * You must call {@link #indexAll(String)} to initialize an index.
     * <p>
     * You must initialize the index before doing any changes.
     *
     * @return whether this index is initialized.
     */
    boolean isInitialized();

    /**
     * Waits for the index to be initialized.
     * If the index is already initialized, this method does nothing.
     * <p>
     * This method doesn't require locking.
     *
     * @throws InterruptedException when the waiting thread is interrupted while it's waiting.
     */
    void waitForInitialization() throws InterruptedException;

    /**
     * Returns whether this index is in edit mode.
     * This method doesn't require locking.
     *
     * @return whether this index is in edit mode.
     */
    boolean isInEditMode();

    /**
     * Performs a {@link EditorLineChange}.
     * This method requires locking with edit mode.
     * <p>
     * The index will perform the required actions to update the index.
     * If the edit involves any {@link EditorReferencedElement}, this method will also
     * update the referencing lines automatically. This involves other indices lines too!
     *
     * @param change the change to perform.
     */
    void change(EditorLineChange change);

    /**
     * Reinspects all lines using the given text.
     * This method requires locking with edit mode.
     * <p>
     * When the indexing is finished, the index will be initialized.
     *
     * @param text the text to index.
     */
    void indexAll(String text);

    /**
     * Returns the element located at the given position.
     * This method requires locking, but not edit mode.
     *
     * @param position the position of the element.
     * @return the {@link EditorIndexedElement} if present.
     */
    Optional<EditorIndexedElement> getElementAt(int position);

    /**
     * Retuns all registered {@link EditorReferencedElement}'s {@link EditorElementReference references}
     * that are marked as global in this file.
     * This method requires locking, but not edit mode.
     *
     * @return the {@link Set} wit all {@link EditorElementReference}s.
     */
    Set<EditorElementReference<?>> getAllGlobalReferencedReferences();

    /**
     * Returns the {@link EditorReferencedElement} that matches the given {@link EditorElementReference}
     * and that is visible from the given {@link ElementScope}.
     * <p>
     * This method requires locking, but not edit mode.
     * <p>
     * This method returns the first found {@link EditorReferencedElement}. If you need to
     * check if there are more than one {@link EditorReferencedElement} declared with the
     * same {@link EditorElementReference}, use {@link #getReferencedElements(EditorElementReference, ElementScope)}.
     *
     * @param reference the reference.
     * @param scope     the scope.
     * @param <T>       the type of the {@link EditorReferencedElement}.
     * @return the {@link EditorReferencedElement} if present.
     */
    <T extends EditorReferencedElement>
    Optional<T> getReferencedElement(EditorElementReference<T> reference, ElementScope scope);

    /**
     * Returns all {@link EditorReferencedElement}s that match the given {@link EditorElementReference}
     * and that are visible from the given {@link ElementScope}.
     * <p>
     * This method requires locking, but not edit mode.
     * <p>
     * This method returns all {@link EditorReferencedElement}s declared in this file that matches the given
     * {@link EditorElementReference}. This is possible, and should be marked as an error to the user.
     * If you need only one element, use {@link #getReferencedElement(EditorElementReference, ElementScope)}.
     *
     * @param reference the reference.
     * @param scope     the scope.
     * @param <T>       the type of the {@link EditorReferencedElement}.
     * @return the {@link EditorReferencedElement}s.
     */
    <T extends EditorReferencedElement>
    Set<T> getReferencedElements(EditorElementReference<T> reference, ElementScope scope);

    /**
     * Returns all {@link EditorReferencedElement}s that match the given type
     * and that are visible from the given {@link ElementScope}.
     * <p>
     * This method requires locking, but not edit mode.
     *
     * @param type  the  type of the {@link EditorReferencedElement}.
     * @param scope the scope.
     * @param <T>   the type of the {@link EditorReferencedElement}.
     * @return the {@link EditorReferencedElement}s.
     */
    <T extends EditorReferencedElement>
    Set<T> getReferencedElementsOfType(Class<T> type, ElementScope scope);

    /**
     * Returns all {@link EditorReferencingElement} that are referencing the given
     * {@link EditorElementReference}.
     * <p>
     * This method requires locking, but not edit mode.
     *
     * @param reference the reference.
     * @param <T>       the  type of the {@link EditorReferencedElement} being referenced.
     * @return the {@link EditorReferencingElement}s.
     */
    <T extends EditorReferencedElement>
    Set<EditorReferencingElement<?>> getReferecingElements(EditorElementReference<T> reference);

    /**
     * Returns a {@link Stream} with all {@link EditorIndexedElement}s inside this index.
     * <p>
     * This method requires locking, but not edit mode.
     * <p>
     * You mustn't use the stream after you unlock the index!
     *
     * @return the {@link Stream}.
     */
    Stream<? extends EditorIndexedElement> elementStream();

    /**
     * Returns whether the given identifier is marked to be a global identifier.
     * <p>
     * This method requires locking, but not edit mode.
     *
     * @param identifier the identifier.
     * @return whether the given identifier is marked to be a global identifier.
     */
    boolean isIdentifierGlobal(String identifier);

    /**
     * Returns the styles for the given line.
     * <p>
     * This method requires locking, but not edit mode.
     *
     * @param line the line.
     * @return the styles.
     */
    Optional<StyleSpans<Collection<String>>> getStyleForLine(int line);

    /**
     * Returns the styles for the given range of lines.
     * Both 'from' and 'to' are inclusive.
     * <p>
     * This method requires locking, but not edit mode.
     *
     * @param from the start line, inclusive.
     * @param to   the end line, inclusive.
     * @return the styles.
     */
    Optional<StyleSpans<Collection<String>>> getStyleRange(int from, int to);

    /**
     * Inspects the elements with a reference present in the given {@link Collection}.
     * <p>
     * This method inspect both {@link EditorReferencedElement}s and {@link EditorReferencingElement}s.
     *
     * @param references the references.
     */
    void inspectElementsWithReferences(Collection<EditorElementReference<?>> references);

    /**
     * Reformats the elements inside this index.
     * This method must returns the reformatted text that this index represents.
     *
     * @return the reformatted text.
     */
    String reformat();

    /**
     * Locks this index.
     * <p>
     * If the index is already locked by another thread, this threads waits for the lock to be released.
     * The lock is a reentrant lock. You can lock several times.
     * <p>
     * This method is not recommended to use.
     * Use {@link #withLock(boolean, Consumer)} or {@link #withLockF(boolean, Function)} instead.
     *
     * @param editMode whether the index should enter edit mode.
     */
    void lock(boolean editMode);

    /**
     * Unlocks this index.
     * <p>
     * The index must be locked by this thread for this method to work.
     * <p>
     * This method is not recommended to use.
     * Use {@link #withLock(boolean, Consumer)} or {@link #withLockF(boolean, Function)} instead.
     *
     * @param finishEditMode whether the lock made the index enter edit mode.
     */
    void unlock(boolean finishEditMode);

    /**
     * Returns whether this index is locked.
     * This method doesn't require locking (for obvious reasons).
     *
     * @return whether this index is locked.
     */
    boolean isLocked();

    /**
     * Executes the given code with the index locked.
     *
     * @param editMode whether the index should lock using edit mode.
     * @param consumer the code to execute.
     */
    default void withLock(boolean editMode, Consumer<EditorIndex> consumer) {
        lock(editMode);
        try {
            consumer.accept(this);
        } finally {
            unlock(editMode);
        }
    }

    /**
     * Executes the given code with the index locked and returns the result.
     *
     * @param editMode whether the index should lock using edit mode.
     * @param function the code to execute.
     */
    default <T> T withLockF(boolean editMode, Function<EditorIndex, T> function) {
        lock(editMode);
        try {
            return function.apply(this);
        } finally {
            unlock(editMode);
        }
    }

    /**
     * Requests a refresh.
     * This method doesn't require locking.
     */
    default void requestRefresh() {
        callEvent(new IndexRequestRefreshEvent(this));
    }
}
