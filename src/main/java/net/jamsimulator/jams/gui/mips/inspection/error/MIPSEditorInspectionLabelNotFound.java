package net.jamsimulator.jams.gui.mips.inspection.error;

import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSInstructionParameterPart;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspection;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspectionBuilder;

import java.util.Map;
import java.util.Optional;

public class MIPSEditorInspectionLabelNotFound extends MIPSEditorInspection {

    public static String NAME = "LABEL_NOT_FOUND";

    public MIPSEditorInspectionLabelNotFound(MIPSEditorInspectionBuilder<?> builder, String instruction) {
        super(builder, Map.of("{NAME}", instruction));
    }

    public static class Builder extends MIPSEditorInspectionBuilder<MIPSEditorInspectionLabelNotFound> {

        public Builder() {
            super(NAME, true);
        }

        @Override
        public Optional<MIPSEditorInspectionLabelNotFound> tryToBuild(MIPSCodeElement element, MIPSFileElements elements) {
            if (element instanceof MIPSInstructionParameterPart) {
                var type = ((MIPSInstructionParameterPart) element).getType();
                if (type != MIPSInstructionParameterPart.InstructionParameterPartType.LABEL)
                    return Optional.empty();

                if (!elements.getLabels().contains(element.getSimpleText())) {
                    return Optional.of(new MIPSEditorInspectionLabelNotFound(this, element.getSimpleText()));
                }
            }

            return Optional.empty();
        }
    }
}
