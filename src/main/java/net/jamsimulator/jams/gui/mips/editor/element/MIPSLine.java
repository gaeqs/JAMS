/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.gui.mips.editor.element;

import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.utils.LabelUtils;
import net.jamsimulator.jams.utils.StringUtils;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.*;

/**
 * Represents a MIPS program's line. A line can contain a label, a directive or a instruction and a comment.
 * This class is not intended to be modified after it's creation, as it may cause several align problems in the editor.
 */
public class MIPSLine {

    private int start;
    private final String text;

    private MIPSLabel label;
    private MIPSInstruction instruction;
    private MIPSDirective directive;
    private MIPSComment comment;
    private Map<String, Boolean> registeredLabels;
    private Collection<String> usedLabels;
    private Collection<MIPSReplacement> usedReplacements;

    private MIPSReplacement replacement;

    //Cached elements
    private SortedSet<MIPSCodeElement> elements;

    public MIPSLine(MIPSFileElements elements, int start, String text) {
        this.start = start;
        this.text = text;
        parseLine(elements);
    }

    /**
     * Returns the text of the line.
     *
     * @return the text of the line.
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the start index of the line. This is the index of the first character of this line inside the file.
     *
     * @return the start index.
     */
    public int getStart() {
        return start;
    }

    /**
     * Moves the start index of the line. This also edits the start index of all it's children.
     * <p>
     * This method is used to move the line when one or more previous lines are edited.
     *
     * @param offset the offset.
     */
    public void move(int offset) {
        this.start += offset;
        if (comment != null) comment.move(offset);
        if (label != null) label.move(offset);
        if (instruction != null) instruction.move(offset);
        if (directive != null) directive.move(offset);
    }

    /**
     * Sets the start index of the line. This also edits the start index of all it's children.
     * <p>
     * This method is used to move the line when one or more previous lines are edited.
     *
     * @param start the new start index.
     */
    public void setStart(int start) {
        move(start - this.start);
    }

    /**
     * Returns the {@link MIPSLabel} of the line, if present.
     *
     * @return the {@link MIPSLabel} of the line, if present.
     */
    public Optional<MIPSLabel> getLabel() {
        return Optional.ofNullable(label);
    }

    /**
     * Returns the {@link MIPSDirective} of the line, if present.
     *
     * @return the {@link MIPSDirective} of the line, if present.
     */
    public Optional<MIPSDirective> getDirective() {
        return Optional.ofNullable(directive);
    }

    /**
     * Returns the {@link MIPSInstruction} of the line, if present.
     *
     * @return the {@link MIPSInstruction} of the line, if present.
     */
    public Optional<MIPSInstruction> getInstruction() {
        return Optional.ofNullable(instruction);
    }

    /**
     * Returns the {@link MIPSComment} of the line, if present.
     *
     * @return the {@link MIPSComment} of the line, if present.
     */
    public Optional<MIPSComment> getComment() {
        return Optional.ofNullable(comment);
    }

    public Optional<MIPSReplacement> getReplacement() {
        return Optional.ofNullable(replacement);
    }

    /**
     * Returns an unmodifiable {@link Map} containing all registered labels in this line.
     * <p>
     * All labels are linked to a {@code boolean} representing whether the label is global.
     * This only represents if the label is a global label by default (registered by an .extern directive, for example).
     * These labels may be transformed to global by a .globl directive. These changes won't be registered by this map.
     *
     * @return all registered labels.
     */
    public Map<String, Boolean> getRegisteredLabels() {
        return registeredLabels == null ? Collections.emptyMap() : Collections.unmodifiableMap(registeredLabels);
    }

    /**
     * Returns an unmodifiable {@link Collection} containing all registered used in this line.
     * Label registration doesn't count as a use.
     *
     * @return all used labels.
     */
    public Collection<String> getUsedLabels() {
        return usedLabels == null ? Collections.emptyList() : Collections.unmodifiableCollection(usedLabels);
    }

    public Collection<MIPSReplacement> getUsedReplacements() {
        return usedReplacements == null ? Collections.emptyList() : Collections.unmodifiableCollection(usedReplacements);
    }

    public boolean isUsingReplacements() {
        return usedReplacements != null && !usedReplacements.isEmpty();
    }

    /**
     * Returns whether this MIPSLine is empty.
     *
     * @return whether it's empty.
     */
    public boolean isEmpty() {
        return comment == null && label == null && directive == null && instruction == null;
    }

    /**
     * Returns the amount of tabs and spaces this line has between the label and the instruction / directive.
     *
     * @return the amount of tabs and spaces.
     */
    public int getTabsAmountAfterLabel() {
        if (instruction == null && directive == null && comment == null) return 0;

        String text = label == null ? this.text : this.text.trim().substring(label.text.length());
        return calculateTabs(text);
    }

    /**
     * Returns the amount of tabs and spaces this line before the label.
     *
     * @return the amount of tabs and spaces.
     */
    public int getTabsAmountBeforeLabel() {
        if (label == null) return 0;
        return calculateTabs(label.text);
    }

    /**
     * Returns the element located at the given index, if present.
     * <p>
     * The given index must be an absolute file index, not a relative one.
     * If you have a relative index you can calculate an absolute index using {@code index + line.getStart()}.
     *
     * @param index the absolute index.
     * @return the element, if present.
     */
    public Optional<MIPSCodeElement> getElementAt(int index) {
        for (MIPSCodeElement element : getSortedElements()) {
            if (element.startIndex <= index && element.endIndex > index) return Optional.of(element);
        }
        return Optional.empty();
    }

    /**
     * Returns all {@link MIPSCodeElement} inside this line, sorted by their start index.
     * <p>
     * This is a lazy method. The set will be created if it's null.
     * Any setter of this class will set the elements set to null.
     *
     * @return the {@link SortedSet}.
     */
    public SortedSet<MIPSCodeElement> getSortedElements() {
        if (elements != null) return elements;
        elements = new TreeSet<>(Comparator.comparingInt(o -> o.startIndex));
        if (comment != null) elements.add(comment);
        if (label != null) elements.add(label);

        if (instruction != null) {
            elements.add(instruction);
            instruction.getParameters().forEach(parameter -> elements.addAll(parameter.getParts()));
        }
        if (directive != null) {
            elements.add(directive);
            elements.addAll(directive.getParameters());
        }

        return elements;
    }

    /**
     * Refreshes the metadata of all elements inside this line.
     * <p>
     * Metadata includes errors and global status for labels.
     *
     * @param elements the {@link MIPSFileElements}.
     */
    public void refreshMetadata(MIPSFileElements elements) {
        usedReplacements = elements.getReplacements(start, text);

        getSortedElements().forEach(target -> target.refreshMetadata(elements));

        var errorManager = JamsApplication.getMIPSEditorErrorBuilderManager();
        getSortedElements().forEach(target -> {
            target.inspections.clear();
            errorManager.getInspections(target, elements, target.inspections);
        });
    }

    /**
     * Styles the selected line of the given {@link CodeArea}.
     * <p>
     * {@link MIPSLine}s don't store their line index, so it must be given to the method.
     *
     * @param lastEnd      the last end given by the previous line.
     * @param spansBuilder the {@link StyleSpansBuilder}.
     */
    public int styleLine(int lastEnd, StyleSpansBuilder<Collection<String>> spansBuilder) {
        if (getSortedElements().isEmpty()) return lastEnd;

        try {
            spansBuilder.add(Collections.emptyList(), start - lastEnd);
        } catch (Exception exception) {
            System.err.println("Last: " + lastEnd);
            System.err.println("Start: " + start);
            System.err.println("Diff: " + (start - lastEnd));
            throw exception;
        }
        lastEnd = start;

        for (MIPSCodeElement element : getSortedElements()) {
            try {
                lastEnd = styleElement(spansBuilder, element, lastEnd);
            } catch (Exception exception) {
                System.err.println("Last: " + lastEnd);
                System.err.println("Element: " + element);
                throw exception;
            }
        }

        return lastEnd;
    }

    /**
     * Registers the given label in the line.
     *
     * @param label  the label.
     * @param global whether the registered label is global.
     * @see #getRegisteredLabels()
     */
    protected void registerLabel(String label, boolean global) {
        if (registeredLabels == null) registeredLabels = new HashMap<>();
        registeredLabels.put(label, global);
    }

    /**
     * Adds this label to the used labels collection.
     *
     * @param label the label.
     * @see #getUsedLabels()
     */
    protected void markUsedLabel(String label) {
        if (usedLabels == null) usedLabels = new ArrayList<>(1);
        usedLabels.add(label);
    }

    private void parseLine(MIPSFileElements elements) {
        String parsing = text;
        int pStart = start;
        int pEnd = start + parsing.length();

        //COMMENT
        int commentIndex = StringUtils.getCommentIndex(parsing);
        if (commentIndex != -1) {
            comment = new MIPSComment(this, pStart + commentIndex, pEnd, parsing.substring(commentIndex));
            pEnd = start + commentIndex;
            parsing = parsing.substring(0, commentIndex);
        }

        //LABEL
        int labelIndex = LabelUtils.getLabelFinishIndex(parsing);
        if (labelIndex != -1) {

            label = new MIPSLabel(this, pStart, pStart + labelIndex, parsing.substring(0, labelIndex + 1));
            pStart = pStart + labelIndex + 1;
            parsing = parsing.substring(labelIndex + 1);
        }

        //INSTRUCTION / DIRECTIVE
        String trim = parsing.trim();
        if (trim.isEmpty()) return;
        if (trim.charAt(0) == '.') {
            directive = new MIPSDirective(this, elements, pStart, pEnd, parsing);
            if (directive.isEqv() && !directive.getEqvKey().isEmpty()) {
                replacement = new MIPSReplacement(this, directive.getEqvKey(), directive.getEqvValue());
            }
        } else {
            instruction = new MIPSInstruction(this, elements, pStart, pEnd, parsing);
        }
    }

    private int styleElement(StyleSpansBuilder<Collection<String>> spansBuilder, MIPSCodeElement element,
                             int lastEnd) {
        if (element.getStartIndex() != lastEnd) {
            spansBuilder.add(Collections.emptyList(), element.getStartIndex() - lastEnd);
        }
        spansBuilder.add(element.getStyles(), element.getSimpleText().length());

        return element.getStartIndex() + element.getSimpleText().length();
    }

    /**
     * Calculates the amount of spaces and tabs that contains the start of this line.
     * Tabs are counted as 4 spaces.
     *
     * @param text the text.
     * @return the amount of spaces and tabs.
     */
    private static int calculateTabs(String text) {
        char c;
        int amount = 0;
        int index = 0;
        while (index < text.length() && ((c = text.charAt(index++)) == '\t' || c == ' ')) amount += c == '\t' ? 4 : 1;
        return amount;
    }
}
