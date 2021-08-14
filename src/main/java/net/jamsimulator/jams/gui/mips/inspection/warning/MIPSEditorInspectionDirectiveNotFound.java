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

package net.jamsimulator.jams.gui.mips.inspection.warning;

import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSDirective;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspection;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspectionBuilder;

import java.util.Map;
import java.util.Optional;

/**
 * This inspection warns the user when a directive is not found.
 * <p>
 * This inspection is NOT an error. This is done to support code with unsupported directives.
 */
public class MIPSEditorInspectionDirectiveNotFound extends MIPSEditorInspection {

    public static String NAME = "DIRECTIVE_NOT_FOUND";

    public MIPSEditorInspectionDirectiveNotFound(MIPSEditorInspectionBuilder<?> builder, String directive) {
        super(builder, Map.of("{NAME}", directive));
    }

    public static class Builder extends MIPSEditorInspectionBuilder<MIPSEditorInspectionDirectiveNotFound> {

        public Builder() {
            super(NAME, false);
        }

        @Override
        public Optional<MIPSEditorInspectionDirectiveNotFound> tryToBuild(MIPSCodeElement element, MIPSFileElements elements) {
            if (element.usesMacroParameter()) return Optional.empty();
            if (element instanceof MIPSDirective && ((MIPSDirective) element).getDirective().isEmpty()) {
                return Optional.of(new MIPSEditorInspectionDirectiveNotFound(this, element.getSimpleText()));
            }

            return Optional.empty();
        }
    }
}
