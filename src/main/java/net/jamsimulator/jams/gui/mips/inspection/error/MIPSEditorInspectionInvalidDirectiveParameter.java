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

package net.jamsimulator.jams.gui.mips.inspection.error;

import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSDirectiveParameter;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspection;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspectionBuilder;

import java.util.Map;
import java.util.Optional;

/**
 * This error appears when a directive parameter is invalid.
 */
public class MIPSEditorInspectionInvalidDirectiveParameter extends MIPSEditorInspection {

    public static String NAME = "INVALID_DIRECTIVE_PARAMETER";

    public MIPSEditorInspectionInvalidDirectiveParameter(MIPSEditorInspectionBuilder<?> builder, String parameter) {
        super(builder, Map.of("{NAME}", parameter));
    }

    public static class Builder extends MIPSEditorInspectionBuilder<MIPSEditorInspectionInvalidDirectiveParameter> {

        public Builder() {
            super(NAME, true);
        }

        @Override
        public Optional<MIPSEditorInspectionInvalidDirectiveParameter> tryToBuild(MIPSCodeElement element, MIPSFileElements elements) {
            if (!element.getLine().areAllReplacementsValid() || element.usesMacroParameter()) return Optional.empty();
            if (element instanceof MIPSDirectiveParameter param) {
                int amount = param.getDirective().getParameters().size();
                var directive = param.getDirective().getDirective();
                if (directive.isPresent() && !directive.get().isParameterValidInContext(param.getIndex(), param.getSimpleText(), amount, elements)) {
                    return Optional.of(new MIPSEditorInspectionInvalidDirectiveParameter(this, element.getSimpleText()));
                }
            }

            return Optional.empty();
        }
    }
}
