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
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSInstructionParameterPart;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspection;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspectionBuilder;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.mips.parameter.ParameterType;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * This inspection warns the user to avoid using the $at register, as it is used by pseudoinstructions
 * to do their executions.
 */
public class MIPSEditorInspectionRegisterUsingAt extends MIPSEditorInspection {

    private static final Set<String> AT_NAMES = Set.of("$at", "$1");
    private static final Set<ParameterType> AT_PARAMETERS = Set.of(
            ParameterType.REGISTER,
            ParameterType.SIGNED_16_BIT_REGISTER_SHIFT,
            ParameterType.UNSIGNED_16_BIT_REGISTER_SHIFT,
            ParameterType.SIGNED_32_BIT_REGISTER_SHIFT,
            ParameterType.LABEL_SIGNED_32_BIT_SHIFT_REGISTER_SHIFT,
            ParameterType.LABEL_REGISTER_SHIFT
    );
    public static String NAME = "REGISTER_USING_AT";

    public MIPSEditorInspectionRegisterUsingAt(MIPSEditorInspectionBuilder<?> builder) {
        super(builder, Map.of());
    }

    public static class Builder extends MIPSEditorInspectionBuilder<MIPSEditorInspectionRegisterUsingAt> {

        public Builder(ResourceProvider provider) {
            super(provider, NAME, false);
        }

        @Override
        public Optional<MIPSEditorInspectionRegisterUsingAt> tryToBuild(MIPSCodeElement element, MIPSFileElements elements) {

            if (element instanceof MIPSInstructionParameterPart
                    && ((MIPSInstructionParameterPart) element).getType()
                    == MIPSInstructionParameterPart.InstructionParameterPartType.REGISTER) {

                var mipsInstruction = ((MIPSInstructionParameterPart) element).getParameter().getInstruction();
                var optional = mipsInstruction.getMostCompatibleInstruction();
                if (optional.isEmpty()) return Optional.empty();
                var instruction = optional.get();
                var parameters = instruction.getParameters();

                int index = ((MIPSInstructionParameterPart) element).getIndex();
                if (index >= parameters.length) return Optional.empty();

                if (AT_PARAMETERS.contains(parameters[index]) && AT_NAMES.contains(element.getSimpleText())) {
                    return Optional.of(new MIPSEditorInspectionRegisterUsingAt(this));
                }
            }

            return Optional.empty();
        }
    }
}
