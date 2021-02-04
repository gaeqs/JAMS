package net.jamsimulator.jams.gui.mips.inspection.error;

import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSInstructionParameterPart;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspection;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspectionBuilder;

import java.util.Map;
import java.util.Optional;

/**
 * This error appears when an instruction parameter part is invalid.
 */
public class MIPSEditorInspectionInvalidInstructionParameter extends MIPSEditorInspection {

    public static String NAME = "INVALID_INSTRUCTION_PARAMETER";

    public MIPSEditorInspectionInvalidInstructionParameter(MIPSEditorInspectionBuilder<?> builder, String parameter) {
        super(builder, Map.of("{NAME}", parameter));
    }

    public static class Builder extends MIPSEditorInspectionBuilder<MIPSEditorInspectionInvalidInstructionParameter> {

        public Builder() {
            super(NAME, true);
        }

        @Override
        public Optional<MIPSEditorInspectionInvalidInstructionParameter> tryToBuild(MIPSCodeElement element, MIPSFileElements elements) {
            if(element.getLine().isUsingReplacements()) return Optional.empty();
            if (element instanceof MIPSInstructionParameterPart
                    && !((MIPSInstructionParameterPart) element).getParameter().isValid()) {
                return Optional.of(new MIPSEditorInspectionInvalidInstructionParameter(this,
                        ((MIPSInstructionParameterPart) element).getParameter().getText()));
            }

            return Optional.empty();
        }
    }
}
