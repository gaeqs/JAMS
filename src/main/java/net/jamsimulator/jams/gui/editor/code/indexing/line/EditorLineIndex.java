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
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.gui.editor.code.hint.EditorHintBar;
import net.jamsimulator.jams.gui.editor.code.indexing.EditorIndex;
import net.jamsimulator.jams.gui.editor.code.indexing.EditorLineChange;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.ElementScope;
import net.jamsimulator.jams.gui.editor.code.indexing.element.line.EditorIndexedLine;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorElementReference;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorGlobalMarkerElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorReferencedElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorReferencingElement;
import net.jamsimulator.jams.gui.editor.code.indexing.event.IndexFinishEditEvent;
import net.jamsimulator.jams.gui.editor.code.indexing.global.ProjectGlobalIndex;
import net.jamsimulator.jams.gui.editor.code.indexing.inspection.Inspector;
import net.jamsimulator.jams.gui.util.EasyStyleSpansBuilder;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.manager.event.ManagerElementRegisterEvent;
import net.jamsimulator.jams.manager.event.ManagerElementUnregisterEvent;
import net.jamsimulator.jams.project.Project;
import net.jamsimulator.jams.utils.Validate;
import org.fxmisc.richtext.model.StyleSpans;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class EditorLineIndex<Line extends EditorIndexedLine> extends SimpleEventBroadcast implements EditorIndex {

    protected final Project project;
    protected final String name;
    protected final Manager<? extends Inspector<?>> inspectors;

    protected volatile ProjectGlobalIndex globalIndex;
    protected volatile EditorHintBar hintBar;

    protected final List<Line> lines = new ArrayList<>();
    protected final Map<EditorElementReference<?>, Set<EditorReferencingElement<?>>> referencingElements = new HashMap<>();
    protected final Map<EditorElementReference<?>, Set<EditorReferencedElement>> referencedElements = new HashMap<>();
    protected final Bag<String> globalIdentifiers = new Bag<>();

    protected final ReentrantLock lock = new ReentrantLock();
    protected AtomicInteger editCount = new AtomicInteger(0);

    protected final Lock initializationLock = new ReentrantLock();
    protected final Condition initializationCondition = initializationLock.newCondition();
    protected volatile boolean initialized = false;

    public EditorLineIndex(Project project, Manager<? extends Inspector> inspectors, String name) {
        this(project, name, (Manager<? extends Inspector<?>>) inspectors);
    }

    public EditorLineIndex(Project project, String name, Manager<? extends Inspector<?>> inspectors) {
        Validate.notNull(project, "Project cannot be null!");
        Validate.notNull(name, "Name cannot be null!");
        Validate.notNull(inspectors, "Inspectors cannot be null!");
        this.project = project;
        this.name = name;
        this.inspectors = inspectors;

        inspectors.registerListeners(this, true);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Manager<? extends Inspector<?>> getInspectorManager() {
        return inspectors;
    }

    public List<Line> getLines() {
        return List.copyOf(lines);
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
    public Optional<EditorHintBar> getHintBar() {
        return Optional.ofNullable(hintBar);
    }

    @Override
    public void setHintBar(EditorHintBar hintBar) {
        checkThread(false);
        this.hintBar = hintBar;
        if (hintBar != null) {
            hintBar.clear();
            int i = 0;
            for (Line line : lines) {
                hintBar.addHint(i++, line.getInspectionLevel());
            }
        }
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
        return editCount.get() > 0;
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

        var oldGlobalReferences = globalIdentifiers == null
                ? Stream.<EditorElementReference<?>>empty() :
                referencedElements.keySet().stream().filter(it -> globalIdentifiers.contains(it.identifier()));

        try {
            getHintBar().ifPresent(EditorHintBar::clear);
            lines.clear();
            referencedElements.clear();
            referencingElements.clear();
            globalIdentifiers.clear();
            if (text.isEmpty()) {
                lines.add(generateNewLine(0, 0, "", ElementScope.FILE));
                return;
            }

            int start = 0;
            int end = 0;
            var builder = new StringBuilder();
            var scope = ElementScope.FILE;

            char c;
            while (text.length() > end) {
                c = text.charAt(end);
                if (c == '\n' || c == '\r') {
                    var line = generateNewLine(start, lines.size(), builder.toString(), scope);
                    if (line.isMacroEnd() || line.isMacroStart()) {
                        line.changeScope(ElementScope.FILE);
                        scope = line.isMacroStart()
                                ? new ElementScope(ElementScope.Type.MACRO, line.getDefinedMacroIdentifier().get())
                                : ElementScope.FILE;
                    }
                    lines.add(line);
                    builder = new StringBuilder();
                    start = end + 1;
                } else {
                    builder.append(c);
                }
                end++;
            }

            if (end >= start) {
                var line = generateNewLine(start, lines.size(), builder.toString(), ElementScope.FILE);
                if (line.isMacroEnd() || line.isMacroStart()) line.changeScope(ElementScope.FILE);
                lines.add(line);
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

        elementStream().forEach(it -> it.inspect(inspectors));
        lines.forEach(EditorIndexedLine::recalculateInspectionLevel);

        if (globalIndex != null) {
            var newGlobalReferences = referencedElements.keySet().stream().filter(it ->
                    globalIdentifiers.contains(it.identifier()));
            var globalUpdates =
                    Stream.concat(oldGlobalReferences, newGlobalReferences).collect(Collectors.toSet());
            globalIndex.inspectElementsWithReferences(globalUpdates, Set.of(this));
        }
    }

    @Override
    public Optional<EditorIndexedElement> getElementAt(int position) {
        checkThread(false);
        return lines.stream()
                .filter(it -> it.getStart() <= position && it.getEnd() > position)
                .findAny().flatMap(it -> it.getElementAt(position));
    }

    @Override
    public Set<EditorElementReference<?>> getAllGlobalReferencedReferences() {
        checkThread(false);
        return referencedElements.keySet().stream()
                .filter(it -> globalIdentifiers.contains(it.identifier()))
                .collect(Collectors.toSet());
    }

    @Override
    public Stream<? extends EditorIndexedElement> elementStream() {
        checkThread(false);
        return lines.stream().flatMap(EditorIndexedElement::elementStream);
    }

    @Override
    public <T extends EditorReferencedElement>
    Optional<T> getReferencedElement(EditorElementReference<T> reference, ElementScope scope) {
        checkThread(false);
        return referencedElements.entrySet().stream()
                .filter(it -> reference.isChild(it.getKey()))
                .flatMap(it -> it.getValue().stream())
                .filter(it -> it.getReferencedScope().canBeReachedFrom(scope))
                .findAny()
                .map(it -> (T) it);
    }

    @Override
    public <T extends EditorReferencedElement>
    Set<T> getReferencedElements(EditorElementReference<T> reference, ElementScope scope) {
        checkThread(false);
        return referencedElements.entrySet().stream()
                .filter(it -> reference.isChild(it.getKey()))
                .flatMap(it -> it.getValue().stream())
                .filter(it -> it.getReferencedScope().canBeReachedFrom(scope))
                .map(it -> (T) it)
                .collect(Collectors.toSet());
    }

    @Override
    public <T extends EditorReferencedElement>
    Set<T> getReferencedElementsOfType(Class<T> type, ElementScope scope) {
        checkThread(false);
        return referencedElements.entrySet().stream()
                .filter(it -> type.isAssignableFrom(it.getKey().referencedType()))
                .flatMap(it -> it.getValue().stream())
                .filter(it -> it.getReferencedScope().canBeReachedFrom(scope))
                .map(it -> (T) it)
                .collect(Collectors.toSet());
    }

    @Override
    public <T extends EditorReferencedElement>
    Set<EditorReferencingElement<?>> getReferecingElements(EditorElementReference<T> reference) {
        checkThread(false);

        return referencingElements.entrySet().stream()
                .filter(it -> it.getKey().isChild(reference))
                .flatMap(it -> it.getValue().stream())
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isIdentifierGlobal(String identifier) {
        checkThread(false);
        return globalIdentifiers.contains(identifier);
    }

    @Override
    public Optional<StyleSpans<Collection<String>>> getStyleForLine(int line) {
        checkThread(false);
        if (line < 0 || line >= lines.size()) return Optional.empty();
        var builder = new EasyStyleSpansBuilder();
        var l = lines.get(line);
        l.addStyles(builder, l.getStart());
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
    public void inspectElementsWithReferences(Collection<EditorElementReference<?>> references) {
        checkThread(true);
        var updatedLines = new HashSet<EditorIndexedLine>();
        references.forEach(reference -> {
            var referencing = getReferecingElements(reference);
            var referenced = getReferencedElements(reference, ElementScope.INTERNAL);
            if (!referencing.isEmpty()) {
                referencing.forEach(element -> element.inspect(inspectors));
                updatedLines.addAll(referencing.stream()
                        .map(it -> it.getParentOfType(EditorIndexedLine.class).orElse(null))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet()));
            }
            if (!referenced.isEmpty()) {
                referenced.forEach(element -> element.inspect(inspectors));

                updatedLines.addAll(referenced.stream()
                        .map(it -> it.getParentOfType(EditorIndexedLine.class).orElse(null))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet()));
            }
        });

        updatedLines.forEach(EditorIndexedLine::recalculateInspectionLevel);
    }

    @Override
    public void lock(boolean editMode) {
        // We want to add the edit mode count before locking.
        // This prevents multiple refreshes when several threads edit this index.
        if (editMode) editCount.incrementAndGet();
        lock.lock();
    }

    @Override
    public void unlock(boolean finishEditMode) {
        if (!lock.isHeldByCurrentThread()) {
            throw new IllegalStateException("This thread is not the owner of the lock! (" +
                    Thread.currentThread() + ") " + lock);
        }

        boolean editModeFinished = finishEditMode && editCount.decrementAndGet() == 0;
        lock.unlock();

        if (editModeFinished) {
            callEvent(new IndexFinishEditEvent(this));
            requestRefresh();
        }
    }

    @Override
    public boolean isLocked() {
        return lock.isLocked();
    }

    protected void editLine(int number, String text) {
        var old = lines.get(number);
        var line = generateNewLine(old.getStart(), number, text, old.getReferencingScope());
        lines.set(number, line);
        old.invalidate();

        int difference = line.getLength() - old.getLength();
        lines.listIterator(number + 1).forEachRemaining(it -> it.move(difference));
        removeReferences(old);
        addReferences(line);
        line.elementStream().forEach(element -> element.inspect(inspectors));
        line.recalculateInspectionLevel();

        var refresh = refreshLinesScope(number, old, line);

        // First let's refresh the references of all changed lines.
        refresh.forEach(l -> {
            l.elementStream().forEach(element -> element.inspect(inspectors));
            l.recalculateInspectionLevel();
        });

        refresh.add(old);
        refresh.add(line);
        checkInspectionsInReferences(refresh);
    }

    protected void removeLine(int number) {
        var line = lines.remove(number);
        line.invalidate();
        lines.listIterator(number).forEachRemaining(it ->
                it.movePositionAndNumber(-1, -line.getLength() - 1));
        getHintBar().ifPresent(it -> it.removeLine(number));
        removeReferences(line);

        var refresh = refreshLinesScope(number, line, null);

        // First let's refresh the references of all changed lines.
        refresh.forEach(l -> {
            l.elementStream().forEach(element -> element.inspect(inspectors));
            l.recalculateInspectionLevel();
        });

        refresh.add(line);
        checkInspectionsInReferences(refresh);
    }

    protected void addLine(int number, String text) {
        var previous = number == 0 ? null : lines.get(number - 1);
        int start = previous == null ? 0 : previous.getEnd() + 1;
        var scope = previous == null ? ElementScope.FILE : previous.getReferencingScope();
        var line = generateNewLine(start, number, text, scope);
        lines.add(number, line);
        lines.listIterator(number + 1).forEachRemaining(it ->
                it.movePositionAndNumber(1, line.getLength() + 1));
        getHintBar().ifPresent(it -> it.addLine(number));
        addReferences(line);
        line.elementStream().forEach(element -> element.inspect(inspectors));
        line.recalculateInspectionLevel();

        var refresh = refreshLinesScope(number, null, line);

        // First let's refresh the references of all changed lines.
        refresh.forEach(l -> {
            l.elementStream().forEach(element -> element.inspect(inspectors));
            l.recalculateInspectionLevel();
        });

        refresh.add(line);
        checkInspectionsInReferences(refresh);
    }

    protected Set<Line> refreshLinesScope(int index, Line removed, Line added) {
        if (index >= lines.size() - 1) return new HashSet<>();

        ElementScope scope = null;
        if (added != null && (added.isMacroStart() || added.isMacroEnd())) {
            scope = added.isMacroStart()
                    ? new ElementScope(ElementScope.Type.MACRO, added.getDefinedMacroIdentifier().orElse(""))
                    : ElementScope.FILE;

        } else if (removed != null && (removed.isMacroStart() || removed.isMacroEnd())) {
            scope = index == 0 ? ElementScope.FILE : lines.get(index - 1).getReferencingScope();
        }

        if (scope == null) return new HashSet<>();

        var list = new HashSet<Line>();
        for (var line : lines.subList(index + 1, lines.size())) {
            if (line.isMacroStart() || line.isMacroEnd()) break;
            line.changeScope(scope);
            list.add(line);
        }
        return list;
    }

    protected void removeReferences(Line line) {
        line.elementStream().forEach(element -> {
            if (element instanceof EditorReferencingElement<?> referencing) {
                referencing.getReferences().forEach(reference -> {
                    var set = referencingElements.get(reference);
                    if (set != null) {
                        set.remove(referencing);
                        if (set.isEmpty()) {
                            referencingElements.remove(reference);
                        }
                    }
                });
            }
            if (element instanceof EditorReferencedElement referenced) {
                var reference = referenced.getReference();
                var set = referencedElements.get(reference);
                if (set != null) {
                    set.remove(referenced);
                    if (set.isEmpty()) {
                        referencedElements.remove(reference);
                    }
                }
            }
            if (element instanceof EditorGlobalMarkerElement marker) {
                globalIdentifiers.removeAll(marker.getGlobalIdentifiers());
            }
        });
    }

    protected void addReferences(Line line) {
        line.elementStream().forEach(element -> {
            if (element instanceof EditorReferencingElement<?> referencing) {
                var references = referencing.getReferences();
                if (references == null) {
                    System.err.println("Element " + referencing + " (" + referencing.getClass()
                            + ") has a null reference set!");
                    return;
                }
                references.forEach(reference -> referencingElements.computeIfAbsent(reference, it -> new HashSet<>())
                        .add(referencing));
            }
            if (element instanceof EditorReferencedElement referenced) {
                var reference = referenced.getReference();
                if (reference == null) {
                    System.err.println("Element " + referenced + " (" + referenced.getClass() + ") has a null reference!");
                    return;
                }
                referencedElements.computeIfAbsent(referenced.getReference(), it -> new HashSet<>()).add(referenced);
            }
            if (element instanceof EditorGlobalMarkerElement marker) {
                globalIdentifiers.addAll(marker.getGlobalIdentifiers());
            }
        });
    }

    protected abstract Line generateNewLine(int start, int number, String text, ElementScope scope);

    protected void checkInspectionsInReferences(Collection<Line> lines) {
        var referenced = lines.stream()
                .flatMap(this::getReferencedReferencesInLine).collect(Collectors.toSet());

        // Now we have all references that have been updated and needs refreshing.
        // If the reference is a referenced element, we must update the referenced and referencing elements.
        // If it's global we also have to update the other files!

        // We don't have to do nothing with the referencing changes. They mustn't interfeere.

        // We also have to update all references in the file that matches the identifiers of the updated markers.
        // The markers must update only the inspectors of other files.

        // Let's start updating our file:
        inspectElementsWithReferences(referenced);

        // Now let's update the global elements and the marked references:
        getGlobalIndex().ifPresent(global -> {
            var marks = lines.stream().flatMap(this::getMarkersInLine).collect(Collectors.toSet());

            var toUpdate = Stream.concat(
                            referenced.stream().filter(it -> isIdentifierGlobal(it.identifier())),
                            referencedElements.keySet().stream().filter(it -> marks.contains(it.identifier())))
                    .collect(Collectors.toSet());

            global.inspectElementsWithReferences(toUpdate, Set.of(this));
        });
    }

    protected Stream<EditorElementReference<?>> getReferencedReferencesInLine(EditorIndexedLine line) {
        return line.elementStream()
                .filter(it -> it instanceof EditorReferencedElement)
                .map(it -> ((EditorReferencedElement) it).getReference());
    }

    protected Stream<String> getMarkersInLine(EditorIndexedLine line) {
        return line.elementStream()
                .filter(it -> it instanceof EditorGlobalMarkerElement)
                .flatMap(it -> ((EditorGlobalMarkerElement) it).getGlobalIdentifiers().stream());
    }


    private void checkThread(boolean edit) {
        if (!lock.isHeldByCurrentThread()) throw new IllegalStateException("Index is not locked! " + lock);
        if (edit && !isInEditMode()) throw new IllegalStateException("Index is not in edit mode!");
    }


    @Listener
    private void onInspectorAdd(ManagerElementRegisterEvent<Inspector<?>> inspector) {
        withLock(true, i ->  elementStream().forEach(element -> element.inspect(inspectors)));
    }

    @Listener
    private void onInspectorRemove(ManagerElementUnregisterEvent<Inspector<?>> inspector) {
        withLock(true, i ->  elementStream().forEach(element -> element.inspect(inspectors)));
    }

}
