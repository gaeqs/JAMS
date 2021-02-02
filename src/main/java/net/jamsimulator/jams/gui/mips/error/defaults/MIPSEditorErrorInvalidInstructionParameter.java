package net.jamsimulator.jams.gui.mips.error.defaults;

import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSInstructionParameterPart;
import net.jamsimulator.jams.gui.mips.error.MIPSEditorError;
import net.jamsimulator.jams.gui.mips.error.MIPSEditorErrorBuilder;

import java.util.Map;
import java.util.Optional;

public class MIPSEditorErrorInvalidInstructionParameter extends MIPSEditorError {

    public static String NAME = "INVALID_INSTRUCTION_PARAMETER";

    public MIPSEditorErrorInvalidInstructionParameter(MIPSEditorErrorBuilder<?> builder, String parameter) {
        super(builder, Map.of("{NAME}", parameter));
    }

    public static class Builder extends MIPSEditorErrorBuilder<MIPSEditorErrorInvalidInstructionParameter> {

        public Builder() {
            super(NAME);
        }

        @Override
        public Optional<MIPSEditorErrorInvalidInstructionParameter> tryToBuild(MIPSCodeElement element, MIPSFileElements elements) {
            if (element instanceof MIPSInstructionParameterPart
                    && !((MIPSInstructionParameterPart) element).getParameter().isValid()) {
                return Optional.of(new MIPSEditorErrorInvalidInstructionParameter(this,
                        ((MIPSInstructionParameterPart) element).getParameter().getText()));
            }

            return Optional.empty();
        }
    }
}
