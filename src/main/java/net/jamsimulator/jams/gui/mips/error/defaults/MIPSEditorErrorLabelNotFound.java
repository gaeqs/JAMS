package net.jamsimulator.jams.gui.mips.error.defaults;

import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSInstructionParameterPart;
import net.jamsimulator.jams.gui.mips.error.MIPSEditorError;
import net.jamsimulator.jams.gui.mips.error.MIPSEditorErrorBuilder;

import java.util.Map;
import java.util.Optional;

public class MIPSEditorErrorLabelNotFound extends MIPSEditorError {

    public static String NAME = "LABEL_NOT_FOUND";

    public MIPSEditorErrorLabelNotFound(MIPSEditorErrorBuilder<?> builder, String instruction) {
        super(builder, Map.of("{NAME}", instruction));
    }

    public static class Builder extends MIPSEditorErrorBuilder<MIPSEditorErrorLabelNotFound> {

        public Builder() {
            super(NAME);
        }

        @Override
        public Optional<MIPSEditorErrorLabelNotFound> tryToBuild(MIPSCodeElement element, MIPSFileElements elements) {
            if (element instanceof MIPSInstructionParameterPart) {
                var type = ((MIPSInstructionParameterPart) element).getType();
                if (type != MIPSInstructionParameterPart.InstructionParameterPartType.LABEL)
                    return Optional.empty();

                if (!elements.getLabels().contains(element.getSimpleText())) {
                    return Optional.of(new MIPSEditorErrorLabelNotFound(this, element.getSimpleText()));
                }
            }

            return Optional.empty();
        }
    }
}
