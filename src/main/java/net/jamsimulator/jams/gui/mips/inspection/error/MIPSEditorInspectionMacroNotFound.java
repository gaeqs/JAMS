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
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSMacroCall;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspection;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspectionBuilder;
import net.jamsimulator.jams.manager.ResourceProvider;

import java.util.Map;
import java.util.Optional;

/**
 * This error appears when the written label is not found.
 */
public class MIPSEditorInspectionMacroNotFound extends MIPSEditorInspection {

    public static String NAME = "MACRO_NOT_FOUND";

    public MIPSEditorInspectionMacroNotFound(MIPSEditorInspectionBuilder<?> builder, String macro) {
        super(builder, Map.of("{NAME}", macro));
    }

    public static class Builder extends MIPSEditorInspectionBuilder<MIPSEditorInspectionMacroNotFound> {

        public Builder(ResourceProvider provider) {
            super(provider,NAME, true);
        }

        @Override
        public Optional<MIPSEditorInspectionMacroNotFound> tryToBuild(MIPSCodeElement element, MIPSFileElements elements) {
            if (!element.getLine().areAllReplacementsValid() || element.usesMacroParameter()) return Optional.empty();

            if (element instanceof MIPSMacroCall) {
                var name = element.getSimpleText();

                var macro = elements.getMacro(name).orElse(null);

                if (macro == null || macro.getStart().getStart() > element.getStartIndex()) {
                    return Optional.of(new MIPSEditorInspectionMacroNotFound(this, element.getSimpleText()));
                }
            }

            return Optional.empty();
        }
    }
}
