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
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedParentElementImpl;
import net.jamsimulator.jams.gui.editor.code.indexing.element.ElementScope;
import net.jamsimulator.jams.gui.editor.code.indexing.element.basic.EditorElementLabel;
import net.jamsimulator.jams.gui.editor.code.indexing.element.line.EditorIndexedLine;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.*;
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

/**
 * Base {@link EditorIndex} representation for editors that are line-based.
 * <p>
 * In a line-based editor, a line is a separated, individual entity.
 * A line contains elements that defines its behaviour.
 * <p>
 * Example of line-based editors is the MIPS editor: each line is independent of each other.
 * <p>
 * To implement your logic in this editor, start creating a child class of {@link EditorIndexedElement}.
 *
 * @param <Line> the type of the line.
 */
public abstract class EditorLineIndex<Line extends EditorIndexedLine> extends SimpleEventBroadcast implements EditorIndex {

    protected final Project project;
    protected final String name;
    protected final Manager<? extends Inspector<?>> inspectors;

    protected volatile ProjectGlobalIndex globalIndex;
    protected volatile EditorHintBar hintBar;

    protected final ElementScope fileScope = new ElementScope(ElementScope.GLOBAL);
    protected final List<Line> lines = new ArrayList<>();
    protected final Map<EditorElementReference<?>, Set<EditorReferencingElement<?>>> referencingElements = new HashMap<>();
    protected final Map<EditorElementReference<?>, Set<EditorReferencingElement<?>>> relativeReferencingElements = new HashMap<>();
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
        Validate.notNull(change, "Change cannot be null!");
        checkThread(true);
        switch (change.type()) {
            case EDIT -> editLine(change.line(), change.text());
            case REMOVE -> removeLine(change.line());
            case ADD -> addLine(change.line(), change.text());
        }
    }

    @Override
    public void indexAll(String text) {
        if (text == null) text = "";
        checkThread(true);

        var oldGlobalReferences = globalIdentifiers == null
                ? Stream.<EditorElementReference<?>>empty() :
                referencedElements.keySet().stream().filter(it -> globalIdentifiers.contains(it.getIdentifier()));

        try {
            getHintBar().ifPresent(EditorHintBar::clear);
            lines.clear();
            referencedElements.clear();
            referencingElements.clear();
            relativeReferencingElements.clear();
            globalIdentifiers.clear();
            if (text.isEmpty()) {
                lines.add(generateNewLine(0, 0, "", fileScope));
                return;
            }

            int start = 0;
            int end = 0;
            var builder = new StringBuilder();
            var scope = fileScope;

            char c;
            while (text.length() > end) {
                c = text.charAt(end);
                if (c == '\n' || c == '\r') {
                    var line = generateNewLine(start, lines.size(), builder.toString(), scope);
                    if (line.isMacroStart() && line.getDefinedMacroScope().isPresent()) {
                        scope = line.getDefinedMacroScope().get();
                    } else if (line.isMacroEnd()) {
                        scope = scope.parent() == ElementScope.GLOBAL ? fileScope : scope.parent();
                        line.changeScope(scope);
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
                var line = generateNewLine(start, lines.size(), builder.toString(), scope);
                if (line.isMacroEnd()) {
                    line.changeScope(scope.parent() == ElementScope.GLOBAL ? fileScope : scope);
                }
                lines.add(line);
            }
            lines.forEach(this::addCachedElements);
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
                    globalIdentifiers.contains(it.getIdentifier()));
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
                .filter(it -> globalIdentifiers.contains(it.getIdentifier()))
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
        Validate.notNull(reference, "Reference cannot be null!");
        checkThread(false);

        if (reference instanceof EditorElementRelativeReference<T> relative) {
            return referencedElements.entrySet().stream()
                    .filter(it -> reference.getReferencedType().isAssignableFrom(it.getKey().getReferencedType()))
                    .flatMap(it -> it.getValue().stream())
                    .filter(it -> it.getReferencedScope().equals(scope))
                    .filter(relative.getFilterPredicate())
                    .min(relative.getType().getComparator())
                    .map(it -> (T) it);
        }

        return referencedElements.entrySet().stream()
                .filter(it -> reference.isChild(it.getKey()))
                .flatMap(it -> it.getValue().stream())
                .filter(it -> it.getReferencedScope().canBeReachedFrom(scope))
                .min(Comparator.comparing(it -> it.getReferencedScope().getScopeLayersDifference(scope)))
                .map(it -> (T) it);
    }

    @Override
    public <T extends EditorReferencedElement>
    Set<T> getReferencedElements(EditorElementReference<T> reference, ElementScope scope) {
        Validate.notNull(reference, "Reference cannot be null!");
        Validate.notNull(scope, "Scope cannot be null!");
        checkThread(false);

        if (reference instanceof EditorElementRelativeReference<T> relative) {
            return referencedElements.entrySet().stream()
                    .filter(it -> reference.getReferencedType().isAssignableFrom(it.getKey().getReferencedType()))
                    .flatMap(it -> it.getValue().stream())
                    .filter(it -> it.getReferencedScope().equals(scope))
                    .filter(relative.getFilterPredicate())
                    .min(relative.getType().getComparator())
                    .map(it -> (T) it)
                    .stream()
                    .collect(Collectors.toSet());
        }

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
        Validate.notNull(type, "Type cannot be null!");
        Validate.notNull(scope, "Scope cannot be null!");
        checkThread(false);
        return referencedElements.entrySet().stream()
                .filter(it -> type.isAssignableFrom(it.getKey().getReferencedType()))
                .flatMap(it -> it.getValue().stream())
                .filter(it -> it.getReferencedScope().canBeReachedFrom(scope))
                .map(it -> (T) it)
                .collect(Collectors.toSet());
    }

    @Override
    public <T extends EditorReferencedElement>
    Set<EditorReferencingElement<?>> getReferecingElements(EditorElementReference<T> reference) {
        Validate.notNull(reference, "Reference cannot be null!");
        checkThread(false);

        return referencingElements.entrySet().stream()
                .filter(it -> it.getKey().isChild(reference))
                .flatMap(it -> it.getValue().stream())
                .collect(Collectors.toSet());
    }

    @Override
    public Set<EditorReferencingElement<?>> getRelativeReferencingElements(
            Class<? extends EditorReferencedElement> type, ElementScope scope) {
        return referencingElements.entrySet().stream()
                .filter(it -> it.getKey().getReferencedType().isAssignableFrom(type))
                .flatMap(it -> it.getValue().stream())
                .filter(it -> it.getReferencedScope().equals(scope))
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
        Validate.notNull(references, "References cannot be null!");
        Validate.hasNoNulls(references, "References cannot have any null value!");
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
    public void inspectElementsWithRelativeReferences(Collection<EditorReferencedElement> elements) {
        var updatedLines = new HashSet<EditorIndexedLine>();
        for (var element : elements) {
            var scope = element.getReferencedScope();
            if (scope == ElementScope.GLOBAL) continue;
            var relative = getRelativeReferencingElements(element.getClass(), scope);
            if (!relative.isEmpty()) {
                relative.forEach(it -> it.inspect(inspectors));
                updatedLines.addAll(relative.stream()
                        .map(it -> it.getParentOfType(EditorIndexedLine.class).orElse(null))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet()));
            }
        }
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

    /**
     * This method is called by {@link #change(EditorLineChange)} when a line being edited.
     *
     * @param number the line number.
     * @param text   the new text of the line.
     */
    protected void editLine(int number, String text) {
        var old = lines.get(number);
        var previous = number == 0 ? null : lines.get(number - 1);
        var scope = previous == null ? fileScope :
                previous.getDefinedMacroScope().orElse(previous.getReferencingScope());
        var line = generateNewLine(old.getStart(), number, text, scope);
        lines.set(number, line);
        old.invalidate();

        int difference = line.getLength() - old.getLength();
        lines.listIterator(number + 1).forEachRemaining(it -> it.move(difference));
        removeCachedElements(old);

        var refresh = getLinesToRefreshOnScopeChange(number, old, line);

        addCachedElements(line);
        line.elementStream().forEach(element -> element.inspect(inspectors));
        line.recalculateInspectionLevel();

        // First let's refresh the references of all changed lines.
        refresh.forEach(l -> {
            l.elementStream().forEach(element -> element.inspect(inspectors));
            l.recalculateInspectionLevel();
        });

        refresh.add(old);
        refresh.add(line);
        checkInspectionsInReferences(refresh);
        checkLabelReferencesBackwards(number, line.getReferencingScope());
    }

    /**
     * This method is called by {@link #change(EditorLineChange)} when a line being removed.
     *
     * @param number the line number.
     */
    protected void removeLine(int number) {
        var line = lines.remove(number);
        line.invalidate();
        lines.listIterator(number).forEachRemaining(it ->
                it.movePositionAndNumber(-1, -line.getLength() - 1));
        getHintBar().ifPresent(it -> it.removeLine(number));
        removeCachedElements(line);

        var refresh = getLinesToRefreshOnScopeChange(number - 1, line, null);

        // First let's refresh the references of all changed lines.
        refresh.forEach(l -> {
            l.elementStream().forEach(element -> element.inspect(inspectors));
            l.recalculateInspectionLevel();
        });

        refresh.add(line);
        checkInspectionsInReferences(refresh);
        checkLabelReferencesBackwards(number, line.getReferencingScope());
    }

    /**
     * This method is called by {@link #change(EditorLineChange)} when a line being added.
     *
     * @param number the line number.
     * @param text   the text of the line.
     */
    protected void addLine(int number, String text) {
        var previous = number == 0 ? null : lines.get(number - 1);
        int start = previous == null ? 0 : previous.getEnd() + 1;
        var scope = previous == null ? fileScope :
                previous.getDefinedMacroScope().orElse(previous.getReferencingScope());
        var line = generateNewLine(start, number, text, scope);
        lines.add(number, line);
        lines.listIterator(number + 1).forEachRemaining(it ->
                it.movePositionAndNumber(1, line.getLength() + 1));
        getHintBar().ifPresent(it -> it.addLine(number));

        var refresh = getLinesToRefreshOnScopeChange(number, null, line);

        addCachedElements(line);
        line.elementStream().forEach(element -> element.inspect(inspectors));
        line.recalculateInspectionLevel();

        // First let's refresh the references of all changed lines.
        refresh.forEach(l -> {
            l.elementStream().forEach(element -> element.inspect(inspectors));
            l.recalculateInspectionLevel();
        });

        refresh.add(line);
        checkInspectionsInReferences(refresh);
        checkLabelReferencesBackwards(number, line.getReferencingScope());
    }

    /**
     * This method returns a {@link Set} with all lines that require reinspection when a scope changes.
     *
     * @param number  the line number.
     * @param removed the line that has been removed. It may be null.
     * @param added   the line that has beed added. It may be null.
     * @return the set with the lines.
     */
    protected Set<Line> getLinesToRefreshOnScopeChange(int number, Line removed, Line added) {
        if (number >= lines.size() - 1) return new HashSet<>();
        ElementScope scope = null;

        if (added != null && (added.isMacroStart() || added.isMacroEnd())) {
            if (added.isMacroStart()) {
                scope = added.getDefinedMacroScope().orElse(added.getReferencingScope());
            } else {
                scope = added.getReferencedScope().parent();
                if (scope == ElementScope.GLOBAL) scope = fileScope;
                added.changeScope(scope);
            }
        } else if (removed != null && (removed.isMacroStart() || removed.isMacroEnd())) {
            if (number == 0) {
                scope = fileScope;
            } else {
                var previous = lines.get(number - 1);
                scope = previous.getDefinedMacroScope().orElse(previous.getReferencingScope());
            }
        }

        if (scope == null) return new HashSet<>();

        var list = new HashSet<Line>();
        for (var line : lines.subList(number + 1, lines.size())) {
            if (line.isMacroStart()) {
                line.changeScope(scope);
                scope = line.getDefinedMacroScope().get();
            } else if (line.isMacroEnd()) {
                scope = scope.parent() == ElementScope.GLOBAL ? fileScope : scope.parent();
                line.changeScope(scope);
            } else {
                if (scope.equals(line.getReferencingScope())) break;
                line.changeScope(scope);
            }
            list.add(line);
        }
        return list;
    }

    /**
     * This method removes all cached elements that are inside the given line.
     *
     * @param line the line.
     */
    protected void removeCachedElements(Line line) {
        line.elementStream().forEach(this::processElementRemoval);
    }

    /**
     * This method is called when an element is removed and its linked cached data should be freed.
     * You can override this method to implement your custom cache logic.
     *
     * @param element the element being removed.
     */
    protected void processElementRemoval(EditorIndexedElement element) {
        if (element instanceof EditorReferencingElement<?> referencing) {
            referencing.getReferences().forEach(reference -> {
                var set = referencingElements.get(reference);
                if (set != null) {
                    set.remove(referencing);
                    if (set.isEmpty()) {
                        referencingElements.remove(reference);
                    }
                }
                var relativeSet = relativeReferencingElements.get(reference);
                if (relativeSet != null) {
                    relativeSet.remove(referencing);
                    if (relativeSet.isEmpty()) {
                        relativeReferencingElements.remove(reference);
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
    }

    /**
     * This method adds all cached elements that are inside the given line.
     *
     * @param line the line.
     */
    protected void addCachedElements(Line line) {
        line.elementStream().forEach(this::processElementAddition);
    }

    /**
     * This method is called when an element is added and its linked cached data should be generated.
     * You can override this method to implement your custom cache logic.
     *
     * @param element the element being added.
     */
    protected void processElementAddition(EditorIndexedElement element) {
        if (element instanceof EditorReferencingElement<?> referencing) {
            var references = referencing.getReferences();
            if (references == null) {
                System.err.println("Element " + referencing + " (" + referencing.getClass()
                        + ") has a null reference set!");
                return;
            }
            references.forEach(reference -> {
                referencingElements.computeIfAbsent(reference, it -> new HashSet<>())
                        .add(referencing);
                if (reference instanceof EditorElementRelativeReference<?>) {
                    relativeReferencingElements.computeIfAbsent(reference, it -> new HashSet<>())
                            .add(referencing);
                }
            });
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
    }

    /**
     * Method called when the index has to generate a new line.
     * <p>
     * This method must be implemented.
     *
     * @param start  the start of the line.
     * @param number the line number.
     * @param text   the text of the line.
     * @param scope  the scope of the line.
     * @return the new line.
     */
    protected abstract Line generateNewLine(int start, int number, String text, ElementScope scope);

    /**
     * This method is used to reinspect the elements that contains any of the references declared
     * in the given lines. Elements from other files in the {@link ProjectGlobalIndex} are also
     * called for reinspection.
     * <p>
     * This method should be called when a line is added, removed or edited.
     *
     * @param lines the lines containing the references.
     */
    protected void checkInspectionsInReferences(Collection<Line> lines) {
        // Let's start getting the references of the referenced elements.
        var elements = lines.stream()
                .flatMap(EditorIndexedParentElementImpl::elementStream)
                .filter(it -> it instanceof EditorReferencedElement)
                .map(it -> (EditorReferencedElement) it)
                .collect(Collectors.toSet());

        Collection<EditorElementReference<?>> referenced = elements.stream()
                .map(it -> (EditorElementReference<?>) ((EditorReferencedElement) it).getReference())
                .collect(Collectors.toSet());

        // Now we have all references that have been updated and needs refreshing.
        // If the reference is a referenced element, we must update the referenced and referencing elements.
        // If it's global we also have to update the other files!

        // We don't have to do nothing with the referencing changes. They mustn't interfeere.

        // We also have to update all references in the file that matches the identifiers of the updated markers.
        // The markers must update only the inspectors of other files.

        // Let's start updating our file:
        inspectElementsWithReferences(referenced);
        // Don't forget the relative references!
        inspectElementsWithRelativeReferences(elements);

        // Let's update now the elements marked as global.
        var marks = lines.stream().flatMap(this::getMarkersInLine).collect(Collectors.toSet());
        var localReferences = referencedElements.keySet()
                .stream()
                .filter(it -> marks.contains(it.getIdentifier()))
                .collect(Collectors.toSet());
        inspectElementsWithReferences(localReferences);

        // Now let's update the global elements and the marked references:
        getGlobalIndex().ifPresent(global -> {
            var toUpdate = Stream.concat(
                            referenced.stream().filter(it -> isIdentifierGlobal(it.getIdentifier())),
                            localReferences.stream())
                    .collect(Collectors.toSet());
            global.inspectElementsWithReferences(toUpdate, Set.of(this));
        });
    }

    /**
     * Inspects the labels whose address may depend on the start line.
     *
     * @param from  the start line.
     * @param scope the element scope of the start line.
     */
    protected void checkLabelReferencesBackwards(int from, ElementScope scope) {
        for (int i = from - 1; i >= 0; i--) {
            var line = lines.get(i);
            if (line.canBeReferencedByALabel() || !line.getReferencingScope().equals(scope)) break;
            line.elementStream()
                    .filter(it -> it instanceof EditorElementLabel)
                    .forEach(it -> it.inspect(inspectors));
            line.recalculateInspectionLevel();
        }
    }

    /**
     * Returns all identifiers marked as global by the given line.
     *
     * @param line the line.
     * @return the identifiers.
     */
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
        withLock(true, i -> elementStream().forEach(element -> element.inspect(inspectors)));
    }

    @Listener
    private void onInspectorRemove(ManagerElementUnregisterEvent<Inspector<?>> inspector) {
        withLock(true, i -> elementStream().forEach(element -> element.inspect(inspectors)));
    }

}
