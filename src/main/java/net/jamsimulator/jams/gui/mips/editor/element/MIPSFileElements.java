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

package net.jamsimulator.jams.gui.mips.editor.element;

import net.jamsimulator.jams.collection.Bag;
import net.jamsimulator.jams.gui.editor.EditorHintBar;
import net.jamsimulator.jams.gui.mips.editor.MIPSFileEditor;
import net.jamsimulator.jams.project.mips.MIPSFilesToAssemble;
import net.jamsimulator.jams.project.mips.MIPSProject;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a collection of assembly elements within an {@link net.jamsimulator.jams.mips.assembler.MIPS32AssemblingFile}.
 */
public class MIPSFileElements {

    private final MIPSProject project;
    private final List<MIPSLine> lines;
    private final Bag<String> labels;
    private final Bag<String> setAsGlobalLabel;
    private final TreeSet<MIPSReplacement> replacements;
    private final TreeSet<Integer> requiresUpdate;
    private final TreeSet<MIPSMacro> macros;
    private MIPSFilesToAssemble filesToAssemble;

    public MIPSFileElements(MIPSProject project) {
        this.project = project;

        this.lines = new ArrayList<>();
        this.labels = new Bag<>();
        this.setAsGlobalLabel = new Bag<>();
        this.replacements = new TreeSet<>();

        this.requiresUpdate = new TreeSet<>();
        this.filesToAssemble = null;

        this.macros = new TreeSet<>();
    }

    /**
     * Returns the project of this file, if present.
     *
     * @return the project of this file, if present.
     */
    public Optional<MIPSProject> getProject() {
        return Optional.ofNullable(project);
    }

    /**
     * Returns the {@link MIPSFilesToAssemble} this file is inside of, if present.
     *
     * @return the {@link MIPSFilesToAssemble}, if present.
     */
    public Optional<MIPSFilesToAssemble> getFilesToAssemble() {
        return Optional.ofNullable(filesToAssemble);
    }

    /**
     * Sets the {@link MIPSFilesToAssemble} this file is inside of.
     * <p>
     * This method should be used only by a {@link MIPSFilesToAssemble}.
     *
     * @param filesToAssemble the {@link MIPSFilesToAssemble}.
     */
    public void setFilesToAssemble(MIPSFilesToAssemble filesToAssemble) {
        this.filesToAssemble = filesToAssemble;
    }

    /**
     * Returns all lines of the represented file.
     *
     * @return the lines.
     */
    public List<MIPSLine> getLines() {
        return lines;
    }

    /**
     * Returns all labels registered on this file.
     *
     * @return the labels.
     */
    public Bag<String> getLabels() {
        return labels;
    }

    public boolean isLabelDeclared(String label) {
        return labels.contains(label) || filesToAssemble != null && filesToAssemble.getGlobalLabels().contains(label);
    }

    /**
     * Returns the labels that should be set as global labels.
     *
     * @return the labels.
     */
    public Bag<String> getSetAsGlobalLabel() {
        return setAsGlobalLabel;
    }

    /**
     * Returns all global labels that are defined by this file.
     *
     * @return the global labels.
     */
    public Set<String> getExistingGlobalLabels() {
        Set<String> existingLabels = new HashSet<>();
        for (String label : setAsGlobalLabel) {
            if (labels.contains(label)) existingLabels.add(label);
        }
        return existingLabels;
    }

    /**
     * Returns the element placed at the given index.
     *
     * @param index the index.
     * @return the element, if found.
     */
    public Optional<MIPSCodeElement> getElementAt(int index) {
        try {
            MIPSLine line = lines.get(lineOf(index));
            return line.getElementAt(index);
        } catch (IndexOutOfBoundsException ex) {
            return Optional.empty();
        }
    }

    /**
     * Returns the index of the file where the given absolute position is located at.
     * <p>
     * The absolute position is the character position.
     *
     * @param position the absolute position.
     * @return the line index or -1 if not found.
     */
    public int lineOf(int position) {
        if (position < 0) return -1;
        int i = 0;
        for (MIPSLine line : lines) {
            if (line.getStart() <= position && line.getStart() + line.getText().length() >= position) return i;
            i++;
        }
        return -1;
    }

    /**
     * Returns the line that contains the given index position.
     *
     * @param position the position.
     * @return the line.
     */
    public MIPSLine getLineWithPosition(int position) {
        return lines.get(lineOf(position));
    }

    public Optional<MIPSMacro> getMacro(String name) {
        return macros.stream().filter(m -> m.getName().equals(name)).findAny();
    }

    public Optional<MIPSMacro> macroAt(int lineStart) {
        MIPSMacro dummy = new MIPSMacro(lineStart);
        var macro = macros.lower(dummy);
        if (macro == null || (macro.getEnd() != null && macro.getEnd().getStart() < lineStart)) return Optional.empty();
        return Optional.of(macro);
    }

    public boolean removeLine(int index, EditorHintBar bar) {
        if (index < 0 || index >= lines.size()) throw new IndexOutOfBoundsException("Index out of bounds");
        MIPSLine line = lines.remove(index);

        int length = line.getText().length() + 1;
        for (int i = index; i < lines.size(); i++)
            lines.get(i).move(-length);

        requiresUpdate.remove(lines.size());

        line.getReplacement().ifPresent(replacement -> removeReplacement(index, replacement));
        line.getMacro().ifPresent(macro -> removeMacro(index, macro));
        line.getDirective().filter(MIPSDirective::isEndMacro).ifPresent(d -> removeEndMacro(index, line));

        if (bar != null) {
            bar.applyLineRemoval(index);
        }

        return checkLabels(line, false);
    }

    public boolean addLine(int index, String text, EditorHintBar bar) {
        if (index < 0 || index > lines.size()) throw new IndexOutOfBoundsException("Index out of bounds");
        if (text.contains("\n") || text.contains("\r")) throw new IllegalArgumentException("Invalid line!");

        int start = 0;
        if (index != 0) {
            MIPSLine previous = lines.get(index - 1);
            start = previous.getStart() + previous.getText().length() + 1;
        }

        MIPSLine line = new MIPSLine(this, start, text);
        lines.add(index, line);

        int length = text.length() + 1;
        for (int i = index + 1; i < lines.size(); i++)
            lines.get(i).move(length);

        line.getReplacement().ifPresent(replacement -> addReplacemenet(index, replacement));
        line.getMacro().ifPresent(macro -> addMacro(index, macro));
        line.getDirective().filter(MIPSDirective::isEndMacro).ifPresent(d -> addEndMacro(index, line));

        requiresUpdate.add(index);

        if (bar != null) {
            bar.applyLineAddition(index);
        }

        return checkLabels(line, true);
    }

    public boolean editLine(int index, String text) {
        if (index < 0 || index >= lines.size()) throw new IndexOutOfBoundsException("Index out of bounds");
        if (text.contains("\n") || text.contains("\r")) throw new IllegalArgumentException("Invalid line!");

        MIPSLine old = lines.get(index);

        int difference = text.length() - old.getText().length();

        MIPSLine line = new MIPSLine(this, old.getStart(), text);
        lines.set(index, line);

        for (int i = index + 1; i < lines.size(); i++)
            lines.get(i).move(difference);

        old.getReplacement().ifPresent(replacement -> removeReplacement(index, replacement));
        old.getMacro().ifPresent(macro -> removeMacro(index, macro));
        old.getDirective().filter(MIPSDirective::isEndMacro).ifPresent(d -> removeEndMacro(index, old));

        line.getReplacement().ifPresent(replacement -> addReplacemenet(index, replacement));
        line.getMacro().ifPresent(macro -> addMacro(index, macro));
        line.getDirective().filter(MIPSDirective::isEndMacro).ifPresent(d -> addEndMacro(index, line));

        requiresUpdate.add(index);
        boolean a = checkLabels(old, false);
        boolean b = checkLabels(line, true);
        return a || b;
    }

    /**
     * Refreshes all lines.
     *
     * @param raw the raw file string.
     */
    public void refreshAll(String raw) {
        lines.clear();
        labels.clear();
        setAsGlobalLabel.clear();
        replacements.clear();
        macros.clear();
        if (raw.isEmpty()) return;

        int start = 0;
        int end = 0;
        StringBuilder builder = new StringBuilder();

        //Checks all lines
        char c;
        while (raw.length() > end) {
            c = raw.charAt(end);
            if (c == '\n' || c == '\r') {
                refreshAllManageLine(new MIPSLine(this, start, builder.toString()));

                //Restarts the builder.
                builder = new StringBuilder();
                start = end + 1;
            } else builder.append(c);
            end++;
        }

        //Final line
        if (end >= start) {
            refreshAllManageLine(new MIPSLine(this, start, builder.toString()));
        }

        for (MIPSLine mipsLine : lines) {
            mipsLine.refreshMetadata(this);
        }
    }

    private void refreshAllManageLine(MIPSLine line) {
        line.getRegisteredLabels().forEach((label, global) -> {
            labels.add(label);
            if (global) setAsGlobalLabel.add(label);
        });

        if (line.getDirective().isPresent()) {
            var directive = line.getDirective().get();
            if (directive.isGlobal()) {
                directive.getParameters().forEach(target -> setAsGlobalLabel.add(target.text));
            } else if (directive.isEndMacro()) {
                macros.forEach(macro -> macro.setEndIfPrevious(line));
            }
        }

        this.lines.add(line);
        line.getReplacement().ifPresent(replacements::add);
        line.getMacro().ifPresent(macros::add);
    }

    /**
     * Styles all lines.
     *
     * @param area the area to style.
     */
    public void styleAll(CodeArea area, EditorHintBar hintBar) {
        if (lines.isEmpty()) return;
        int lastEnd = 0;
        var spansBuilder = new StyleSpansBuilder<Collection<String>>();

        int i = 0;
        for (MIPSLine line : lines) {
            lastEnd = line.styleLine(lastEnd, spansBuilder);
            line.refreshHints(hintBar, i++);
        }
        requiresUpdate.clear();

        StyleSpans<Collection<String>> spans;
        try {
            spans = spansBuilder.create();
        } catch (IllegalStateException ex) {
            // No spans have been added.
            return;
        }
        area.setStyleSpans(0, spans);
    }

    /**
     * Styles the selected lines.
     *
     * @param area   the area to style.
     * @param from   the first line index.
     * @param amount the amount of lines to style.
     */
    public void styleLines(CodeArea area, EditorHintBar hintBar, int from, int amount) {
        if (from < 0 || from + amount > lines.size())
            throw new IndexOutOfBoundsException("Index out of bounds. [" + from + ", " + (from + amount) + ")");

        int lastEnd = lines.get(from).getStart();
        var spansBuilder = new StyleSpansBuilder<Collection<String>>();

        for (int i = 0; i < amount; i++) {
            var line = lines.get(from + i);
            lastEnd = line.styleLine(lastEnd, spansBuilder);
            line.refreshHints(hintBar, from + i);

        }

        StyleSpans<Collection<String>> spans;
        try {
            spans = spansBuilder.create();
        } catch (IllegalStateException ex) {
            // No spans have been added.
            return;
        }
        area.setStyleSpans(0, spans);
    }

    /**
     * Applies all pending updates to the given area.
     *
     * @param area the {@link CodeArea}.
     */
    public void update(MIPSFileEditor area) {
        if (requiresUpdate.isEmpty()) return;

        MIPSLine line;
        var hintBar = area.getHintBar();

        for (int i : requiresUpdate) {
            if (i < 0 || i >= lines.size()) continue;

            var spansBuilder = new StyleSpansBuilder<Collection<String>>();
            line = lines.get(i);

            line.refreshMetadata(this);
            line.styleLine(line.getStart(), spansBuilder);
            line.refreshHints(hintBar, i);


            StyleSpans<Collection<String>> spans;
            try {
                spans = spansBuilder.create();
            } catch (IllegalStateException ex) {
                // No spans have been added.
                continue;
            }
            area.setStyleSpans(i, 0, spans);

        }
        requiresUpdate.clear();
    }

    /**
     * Adds to the update queue all lines containing any of the given labels.
     *
     * @param labelsToCheck the labels.
     */
    public void seachForLabelsUpdates(Collection<String> labelsToCheck) {
        int i = 0;
        Collection<String> used;
        for (MIPSLine mipsLine : lines) {
            used = mipsLine.getUsedLabels();
            for (String label : labelsToCheck) {
                if (used.contains(label)) {
                    requiresUpdate.add(i);
                    break;
                }
            }
            if (mipsLine.getLabel().isPresent()) {
                if (labelsToCheck.contains(mipsLine.getLabel().get().getLabel())) {
                    requiresUpdate.add(i);
                }
            }
            i++;
        }
    }

    public Set<MIPSReplacement> getReplacements(int startIndex, String text) {
        Set<MIPSReplacement> set = new HashSet<>();
        Iterator<MIPSReplacement> iterator = replacements.iterator();

        MIPSReplacement current;
        while (iterator.hasNext() && (current = iterator.next()).getLine().getStart() < startIndex) {
            if (text.contains(current.getKey())) {
                set.add(current);
            }
        }
        return set;
    }

    // region replacement managment

    private void addReplacemenet(int lineIndex, MIPSReplacement replacement) {
        replacements.add(replacement);

        //CHECK
        var sublist = lines.subList(lineIndex + 1, lines.size());

        int i = lineIndex + 1;
        for (var line : sublist) {
            if (line.getText().contains(replacement.getKey())) {
                requiresUpdate.add(i);
            }
            i++;
        }
    }

    private void removeReplacement(int lineIndex, MIPSReplacement replacement) {
        replacements.remove(replacement);

        //CHECK
        var sublist = lines.subList(lineIndex + 1, lines.size());

        int i = lineIndex;
        for (var line : sublist) {
            if (line.getUsedReplacements().contains(replacement)) {
                requiresUpdate.add(i);
            }
            i++;
        }
    }

    // endregion

    //region macro managment

    private void addMacro(int lineIndex, MIPSMacro macro) {
        macros.add(macro);

        for (MIPSMacro other : macros) {
            if (other == macro) break;
            other.setEndIfPrevious(macro.getStart());
        }

        macro.searchNewEnd(lineIndex + 1, lines);

        int end = macro.getEnd() == null ? lines.size() : lines.indexOf(macro.getEnd());
        addMacroLinesToUpdate(lineIndex + 1, end);

        // Search for macro calls!
        addMacroCallsToUpdate(lineIndex, macro);
    }

    public void removeMacro(int lineIndex, MIPSMacro macro) {
        macros.remove(macro);

        for (MIPSMacro other : macros) {
            if (other.getEnd() != macro.getStart()) continue;
            other.searchNewEnd(lineIndex, lines);

            int end = other.getEnd() == null ? lines.size() : lines.indexOf(other.getEnd());
            addMacroLinesToUpdate(lineIndex, end);
        }

        int end = macro.getEnd() == null ? lines.size() : lines.indexOf(macro.getEnd());
        addMacroLinesToUpdate(lineIndex, end);

        // Search for macro calls!
        addMacroCallsToUpdate(lineIndex, macro);
    }

    public void addEndMacro(int lineIndex, MIPSLine line) {
        for (MIPSMacro macro : macros) {
            if (macro.getStart().getStart() >= line.getStart()) continue;
            macro.setEndIfPrevious(line);
            if (macro.getEnd() == line) {
                int start = lines.indexOf(macro.getStart());
                addMacroLinesToUpdate(start, lineIndex);
            }
        }
    }

    private void removeEndMacro(int lineIndex, MIPSLine line) {
        for (MIPSMacro macro : macros) {
            if (macro.getEnd() != line) continue;

            macro.searchNewEnd(lineIndex, lines);

            int end = macro.getEnd() == null ? lines.size() : lines.indexOf(macro.getEnd());
            addMacroLinesToUpdate(lineIndex, end);
        }
    }


    private void addMacroLinesToUpdate(int from, int to) {
        var sublist = lines.subList(from, to);

        int i = from;
        for (MIPSLine line : sublist) {
            if (line.canHaveMacroParameters()) {
                requiresUpdate.add(i);
            }
            i++;
        }
    }

    private void addMacroCallsToUpdate(int lineIndex, MIPSMacro macro) {
        for (MIPSLine other : lines.subList(lineIndex, lines.size())) {
            if (other.getMacroCall().map(e -> e.getSimpleText().equals(macro.getName())).orElse(false)) {
                requiresUpdate.add(lineIndex);
            }
            lineIndex++;
        }
    }

    //endregion

    private boolean checkLabels(MIPSLine line, boolean add) {
        List<String> labelsToCheck = new ArrayList<>();
        AtomicBoolean globalLabelUpdated = new AtomicBoolean(false);

        //LABELS
        line.getRegisteredLabels().forEach((label, global) -> {
            if (add) labels.add(label);
            else labels.remove(label);
            labelsToCheck.add(label);

            if (global) {
                if (add) setAsGlobalLabel.add(label);
                else setAsGlobalLabel.remove(label);
            }
            globalLabelUpdated.set(globalLabelUpdated.get() || global || setAsGlobalLabel.contains(label));
        });

        //DIRECTIVE
        if (line.getDirective().isPresent() && line.getDirective().get().isGlobal()) {
            line.getDirective().get().getParameters().forEach(target -> {
                if (add) setAsGlobalLabel.add(target.text);
                else setAsGlobalLabel.remove(target.text);
                labelsToCheck.add(target.text);
            });
            globalLabelUpdated.set(globalLabelUpdated.get() || !line.getDirective().get().getParameters().isEmpty());
        }

        seachForLabelsUpdates(labelsToCheck);
        return globalLabelUpdated.get();
    }

}
