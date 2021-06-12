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

package net.jamsimulator.jams.gui.mips.inspection;

import net.jamsimulator.jams.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an inspection inside a {@link net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement code element}.
 * <p>
 * Inspections inform the developer about errors and warnings.
 * <p>
 * Inspections are created using {@link MIPSEditorInspectionBuilder builder}s. You must implement a inspection and
 * a builder class to create a new error or warning.
 */
public class MIPSEditorInspection {

    private final MIPSEditorInspectionBuilder<?> builder;
    private final Map<String, String> replacements;

    /**
     * Creates an instance of the inspection.
     *
     * @param builder      the builder representing this inspection.
     * @param replacements the replacement texts for the description.
     */
    public MIPSEditorInspection(MIPSEditorInspectionBuilder<?> builder, Map<String, String> replacements) {
        this.builder = builder;
        this.replacements = replacements == null ? new HashMap<>() : replacements;
    }

    /**
     * Returns the {@link MIPSEditorInspectionBuilder builder} representing this inspection.
     *
     * @return the builder.
     */
    public MIPSEditorInspectionBuilder<?> getBuilder() {
        return builder;
    }

    /**
     * Returns the description of the inspection in the selected language.
     * This description has also its placeholders replaced.
     *
     * @return the description.
     */
    public String getParsedDescription() {
        String base = builder.getDescription();

        for (Map.Entry<String, String> replacement : replacements.entrySet()) {
            base = base.replace(replacement.getKey(), replacement.getValue());
        }

        return StringUtils.parseEscapeCharacters(base);
    }
}
