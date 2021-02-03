package net.jamsimulator.jams.gui.mips.inspection.error;

import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSInstruction;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspection;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspectionBuilder;

import java.util.Map;
import java.util.Optional;

public class MIPSEditorInspectionInstructionNotFound extends MIPSEditorInspection {

    public static String NAME = "INSTRUCTION_NOT_FOUND";

    public MIPSEditorInspectionInstructionNotFound(MIPSEditorInspectionBuilder<?> builder, String instruction) {
        super(builder, Map.of("{NAME}", instruction));
    }

    public static class Builder extends MIPSEditorInspectionBuilder<MIPSEditorInspectionInstructionNotFound> {

        public Builder() {
            super(NAME, true);
        }

        @Override
        public Optional<MIPSEditorInspectionInstructionNotFound> tryToBuild(MIPSCodeElement element, MIPSFileElements elements) {
            if (element instanceof MIPSInstruction && ((MIPSInstruction) element).getMostCompatibleInstruction().isEmpty()) {
                return Optional.of(new MIPSEditorInspectionInstructionNotFound(this, element.getSimpleText()));
            }

            return Optional.empty();
        }
    }
}
