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

package net.jamsimulator.jams.gui.editor.code.indexing.element.line;

import net.jamsimulator.jams.gui.editor.code.indexing.EditorIndex;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexStyleableElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedParentElementImpl;
import net.jamsimulator.jams.gui.editor.code.indexing.element.ElementScope;
import net.jamsimulator.jams.gui.editor.code.indexing.inspection.Inspection;
import net.jamsimulator.jams.gui.editor.code.indexing.inspection.InspectionLevel;
import net.jamsimulator.jams.gui.util.EasyStyleSpansBuilder;
import net.jamsimulator.jams.utils.Validate;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;

/**
 * A line inside a {@link  net.jamsimulator.jams.gui.editor.code.indexing.line.EditorLineIndex EditorLineIndex}.
 */
public abstract class EditorIndexedLine extends EditorIndexedParentElementImpl {

    protected int number;
    protected InspectionLevel inspectionLevel = InspectionLevel.NONE;


    public EditorIndexedLine(EditorIndex index, ElementScope scope, int start, int number, String text) {
        super(index, scope, null, start, text, "");
        Validate.isTrue(number >= 0, "Index cannot be negative!");
        this.number = number;
    }

    /**
     * Returns the number of this line. This value is mutable.
     *
     * @return the number of this line.
     */
    public int getNumber() {
        return number;
    }

    /**
     * Returns the inspection level of this line. This value is mutable.
     *
     * @return the number of this line.
     */
    public InspectionLevel getInspectionLevel() {
        return inspectionLevel;
    }

    /**
     * Moves this line the given number of lines.
     *
     * @param offset the number of lines.
     */
    public void moveNumber(int offset) {
        Validate.isTrue(number + offset >= 0, "Resulted index cannot be negative!");
        number += offset;
    }

    /**
     * Moves this line in global positions and lines.
     *
     * @param numberOffset   the number of global positions to move.
     * @param positionOffset the number of lines to move.
     */
    public void movePositionAndNumber(int numberOffset, int positionOffset) {
        moveNumber(numberOffset);
        move(positionOffset);
    }

    /**
     * Recalculates the inspection level of this line.
     */
    public void recalculateInspectionLevel() {
        inspectionLevel = elementStream().flatMap(it -> it.getMetadata().inspections().stream())
                .max(Comparator.comparingInt(o -> o.level().ordinal()))
                .map(Inspection::level)
                .orElse(InspectionLevel.NONE);
        index.getHintBar().ifPresent(bar -> bar.addHint(number, inspectionLevel));
    }

    /**
     * Inserts the styles of the {@link  EditorIndexStyleableElement}s inside this line
     * into the given builder.
     *
     * @param builder the builder to edit.
     * @param offset  the start position of the builder.
     */
    public void addStyles(EasyStyleSpansBuilder builder, int offset) {
        elementStream()
                .filter(it -> it instanceof EditorIndexStyleableElement)
                .sorted(Comparator.comparingInt(EditorIndexedElement::getStart))
                .forEach(it -> {
                    try {
                        var inspectionStyle = it.getMetadata().getHigherLevelInspection()
                                .flatMap(ins -> ins.level().getElementStyle());
                        if (inspectionStyle.isEmpty()) {
                            builder.add(it.getStart() - offset, it.getLength(),
                                    ((EditorIndexStyleableElement) it).getStyles());
                        } else {
                            var set = new HashSet<>(((EditorIndexStyleableElement) it).getStyles());
                            set.add(inspectionStyle.get());
                            builder.add(it.getStart() - offset, it.getLength(), set);
                        }
                    } catch (IllegalStateException ex) {
                        System.err.println("Error styling element " + it.getIdentifier());
                        throw ex;
                    }
                });
    }

    /**
     * Returns whether this line is the start of a macro.
     *
     * @return whether this line is the start of a macro.
     */
    public abstract boolean isMacroStart();

    /**
     * Returns whether this line is the end of a macro.
     *
     * @return whether this line is the end of a macro.
     */
    public abstract boolean isMacroEnd();

    /**
     * Returns whether this line should be referenced by a label.
     *
     * @return whether this line should be reference by a label.
     */
    public abstract boolean canBeReferencedByALabel();

    /**
     * @return returns the macro scope defined by this line.
     */
    public abstract Optional<ElementScope> getDefinedMacroScope();
}
