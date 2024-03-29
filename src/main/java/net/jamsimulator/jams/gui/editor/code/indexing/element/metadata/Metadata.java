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

package net.jamsimulator.jams.gui.editor.code.indexing.element.metadata;

import net.jamsimulator.jams.gui.editor.code.indexing.inspection.Inspection;

import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;

/**
 * Represents the mutable data of a {@link
 * net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedElement indexed element}.
 *
 * @param inspections the inspections of the element.
 */
public record Metadata(Set<Inspection> inspections) {
    public static final Metadata EMPTY = new Metadata(Collections.emptySet());

    /**
     * Returns the higher level inspection inside this metadata.
     * Returns empty if there's no inspections.
     *
     * @return the inspection if found.
     */
    public Optional<Inspection> getHigherLevelInspection() {
        return inspections.stream().max(Comparator.comparingInt(it -> it.level().ordinal()));
    }

}
