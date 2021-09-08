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

package net.jamsimulator.jams.gui.editor.code.indexing.line;

import net.jamsimulator.jams.collection.Bag;
import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.gui.editor.code.indexing.EditorIndex;
import net.jamsimulator.jams.gui.editor.code.indexing.EditorLineChange;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.line.EditorIndexedLine;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorElementReference;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorGlobalMarkerElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorReferencedElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorReferencingElement;
import net.jamsimulator.jams.gui.editor.code.indexing.event.IndexFinishEditEvent;
import net.jamsimulator.jams.gui.editor.code.indexing.global.ProjectGlobalIndex;
import net.jamsimulator.jams.gui.editor.code.indexing.inspection.Inspector;
import net.jamsimulator.jams.gui.util.EasyStyleSpansBuilder;
import net.jamsimulator.jams.project.Project;
import org.fxmisc.richtext.model.StyleSpans;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

public abstract class EditorLineIndex<Line extends EditorIndexedLine> extends SimpleEventBroadcast implements EditorIndex {

    protected final Project project;

    protected ProjectGlobalIndex globalIndex;

    protected final List<Line> lines = new ArrayList<>();
    protected final Map<EditorElementReference<?>, Set<EditorReferencingElement>> referencingElements = new HashMap<>();
    protected final Map<EditorElementReference<?>, Set<EditorReferencedElement>> referencedElements = new HashMap<>();
    protected final Bag<String> globalIdentifiers = new Bag<>();

    protected final Lock lock = new ReentrantLock();
    protected volatile Thread lockOwner = null;
    protected volatile boolean editMode = false;

    protected final Lock initializationLock = new ReentrantLock();
    protected final Condition initializationCondition = initializationLock.newCondition();
    protected volatile boolean initialized = false;

    public EditorLineIndex(Project project) {
        this.project = project;
    }

    public Line getLine(int line) {
        checkThread(false);
        return lines.get(line);
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public Optional<ProjectGlobalIndex> getGlobalIndex() {
        return Optional.ofNullable(globalIndex);
    }

    @Override
    public void setGlobalIndex(ProjectGlobalIndex globalIndex) {
        checkThread(true);
        this.globalIndex = globalIndex;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void waitForInitialization() throws InterruptedException {
        initializationLock.lock();
        if (!initialized) {
            initializationCondition.await();
        }
        initializationLock.unlock();
    }

    @Override
    public boolean isInEditMode() {
        return editMode;
    }

    @Override
    public void change(EditorLineChange change) {
        checkThread(true);
        switch (change.type()) {
            case EDIT -> editLine(change.line(), change.text());
            case REMOVE -> removeLine(change.line());
            case ADD -> addLine(change.line(), change.text());
        }
    }

    @Override
    public void indexAll(String text) {
        checkThread(true);
        try {
            lines.clear();
            if (text.isEmpty()) {
                lines.add(generateNewLine(0, 0, ""));
                return;
            }

            int start = 0;
            int end = 0;
            var builder = new StringBuilder();

            char c;
            while (text.length() > end) {
                c = text.charAt(end);
                if (c == '\n' || c == '\r') {
                    lines.add(generateNewLine(start, lines.size(), builder.toString()));
                    builder = new StringBuilder();
                    start = end + 1;
                } else {
                    builder.append(c);
                }
                end++;
            }

            if (end >= start) {
                lines.add(generateNewLine(start, lines.size(), builder.toString()));
            }

            lines.forEach(this::addReferences);
        } finally {
            initializationLock.lock();
            if (!initialized) {
                initialized = true;
                initializationCondition.signalAll();
            }
            initializationLock.unlock();
        }
    }

    @Override
    public Optional<EditorIndexedElement> getElementAt(int position) {
        checkThread(false);
        return lines.stream()
                .filter(it -> it.getStart() >= position && it.getEnd() < position)
                .findAny().flatMap(it -> it.getElementAt(position));
    }

    @Override
    public Stream<? extends EditorIndexedElement> elementStream() {
        checkThread(false);
        return lines.stream().flatMap(EditorIndexedElement::elementStream);
    }

    @Override
    public <T extends EditorReferencedElement>
    Optional<T> getReferencedElement(EditorElementReference<T> reference, boolean globalContext) {
        checkThread(false);
        var set = referencedElements.get(reference);
        if (set == null || set.isEmpty()) return Optional.empty();
        if (globalContext) {
            return set.stream()
                    .filter(it -> globalIdentifiers.contains(it.getIdentifier()))
                    .findAny()
                    .map(it -> (T) it);
        } else {
            return set.stream().findAny().map(it -> (T) it);
        }
    }

    @Override
    public <T extends EditorReferencedElement>
    Set<EditorReferencingElement> getReferecingElements(EditorElementReference<T> reference) {
        checkThread(false);
        var set = referencingElements.get(reference);
        if (set == null) return Collections.emptySet();
        return Set.copyOf(set);
    }

    @Override
    public boolean isIdentifierGlobal(String global) {
        checkThread(false);
        return globalIdentifiers.contains(global);
    }

    @Override
    public Optional<StyleSpans<Collection<String>>> getStyleForLine(int line) {
        checkThread(false);
        if (line < 0 || line >= lines.size()) return Optional.empty();
        var builder = new EasyStyleSpansBuilder();
        var l = lines.get(line);
        l.addStyles(builder, l.getLength());
        builder.add(0, lines.get(line).getLength(), Set.of("mips-error"));
        return builder.isEmpty() ? Optional.empty() : Optional.of(builder.create());
    }

    @Override
    public Optional<StyleSpans<Collection<String>>> getStyleRange(int from, int to) {
        checkThread(false);
        if (from < 0 || from >= lines.size() || to < from) return Optional.empty();

        var builder = new EasyStyleSpansBuilder();

        var subList = lines.subList(from, Math.min(to + 1, lines.size()));
        int start = subList.get(0).getStart();
        subList.forEach(line -> line.addStyles(builder, start));
        return builder.isEmpty() ? Optional.empty() : Optional.of(builder.create());
    }

    @Override
    public void inspect(Collection<Inspector> inspectors) {
        checkThread(false);
        elementStream().forEach(e -> e.inspect(inspectors));
    }

    @Override
    public void lock(boolean editMode) {
        lock.lock();
        lockOwner = Thread.currentThread();
        this.editMode = editMode;
    }

    @Override
    public void unlock(boolean finishEditMode) {
        lockOwner = null;

        boolean editModeFinished = editMode && finishEditMode;
        if (editModeFinished) editMode = false;
        lock.unlock();

        if (editModeFinished) {
            callEvent(new IndexFinishEditEvent(this));
            requestRefresh();
        }
    }

    @Override
    public boolean isLocked() {
        return lockOwner != null;
    }

    protected void editLine(int number, String text) {
        var old = lines.get(number);
        var line = generateNewLine(old.getStart(), number, text);
        lines.set(number, line);
        old.invalidate();

        int difference = line.getLength() - old.getLength();
        lines.listIterator(number + 1).forEachRemaining(it -> it.move(difference));
        removedReferences(old);
        addReferences(line);
    }

    protected void removeLine(int number) {
        var line = lines.remove(number);
        line.invalidate();
        lines.listIterator(number).forEachRemaining(it ->
                it.movePositionAndNumber(-1, -line.getLength()));
        removedReferences(line);
    }

    protected void addLine(int number, String text) {
        int start = number == 0 ? 0 : lines.get(number - 1).getEnd() + 1;
        var line = generateNewLine(start, number, text);
        lines.add(number, line);
        lines.listIterator(number + 1).forEachRemaining(it ->
                it.movePositionAndNumber(1, line.getLength()));
        addReferences(line);
    }

    protected void removedReferences(Line line) {
        line.elementStream().forEach(element -> {
            if (element instanceof EditorReferencingElement referencing) {
                referencing.getReferences().forEach(reference -> {
                    var set = referencingElements.get(reference);
                    if (set != null) set.remove(referencing);
                });
            }
            if (element instanceof EditorReferencedElement referenced) {
                var set = referencedElements.get(referenced.getReference());
                if (set != null) set.remove(referenced);
            }
            if (element instanceof EditorGlobalMarkerElement marker) {
                globalIdentifiers.removeAll(marker.getGlobalIdentifiers());
            }
        });
    }

    protected void addReferences(Line line) {
        line.elementStream().forEach(element -> {
            if (element instanceof EditorReferencingElement referencing) {
                referencing.getReferences().forEach(reference ->
                        referencingElements.computeIfAbsent(reference, it -> new HashSet<>()).add(referencing));
            }
            if (element instanceof EditorReferencedElement referenced) {
                referencedElements.computeIfAbsent(referenced.getReference(), it -> new HashSet<>()).add(referenced);
            }
            if (element instanceof EditorGlobalMarkerElement marker) {
                globalIdentifiers.addAll(marker.getGlobalIdentifiers());
            }
        });
    }

    protected abstract Line generateNewLine(int start, int number, String text);

    private void checkThread(boolean edit) {
        if (lockOwner != Thread.currentThread()) throw new IllegalStateException("Index is not locked!");
        if (edit && !editMode) throw new IllegalStateException("Index is not in edit mode!");
    }
}
