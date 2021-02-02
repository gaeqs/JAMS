package net.jamsimulator.jams.gui.mips.error.defaults;

import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSDirective;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSInstruction;
import net.jamsimulator.jams.gui.mips.error.MIPSEditorError;
import net.jamsimulator.jams.gui.mips.error.MIPSEditorErrorBuilder;

import java.util.Map;
import java.util.Optional;

public class MIPSEditorErrorInstructionNotFound extends MIPSEditorError {

    public static String NAME = "INSTRUCTION_NOT_FOUND";

    public MIPSEditorErrorInstructionNotFound(MIPSEditorErrorBuilder<?> builder, String instruction) {
        super(builder, Map.of("{NAME}", instruction));
    }

    public static class Builder extends MIPSEditorErrorBuilder<MIPSEditorErrorInstructionNotFound> {

        public Builder() {
            super(NAME);
        }

        @Override
        public Optional<MIPSEditorErrorInstructionNotFound> tryToBuild(MIPSCodeElement element, MIPSFileElements elements) {
            if (element instanceof MIPSInstruction && ((MIPSInstruction) element).getMostCompatibleInstruction().isEmpty()) {
                return Optional.of(new MIPSEditorErrorInstructionNotFound(this, element.getSimpleText()));
            }

            return Optional.empty();
        }
    }
}
