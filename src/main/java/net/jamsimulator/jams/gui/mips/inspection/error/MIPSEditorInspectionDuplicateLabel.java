package net.jamsimulator.jams.gui.mips.inspection.error;

import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSDirectiveParameter;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSLabel;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspection;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspectionBuilder;

import java.util.Map;
import java.util.Optional;

/**
 * This error appears when the given label is duplicated.
 */
public class MIPSEditorInspectionDuplicateLabel extends MIPSEditorInspection {

    public static String NAME = "DUPLICATE_LABEL";

    public MIPSEditorInspectionDuplicateLabel(MIPSEditorInspectionBuilder<?> builder, String label) {
        super(builder, Map.of("{NAME}", label));
    }

    public static class Builder extends MIPSEditorInspectionBuilder<MIPSEditorInspectionDuplicateLabel> {

        public Builder() {
            super(NAME, true);
        }

        @Override
        public Optional<MIPSEditorInspectionDuplicateLabel> tryToBuild(MIPSCodeElement element, MIPSFileElements elements) {
            if (!element.getLine().areAllReplacementsValid() || element.usesMacroParameter()) return Optional.empty();

            String label;

            if (element instanceof MIPSDirectiveParameter) {
                if (!((MIPSDirectiveParameter) element).isRegisteredLabel()) return Optional.empty();
                label = element.getSimpleText();
            } else if (element instanceof MIPSLabel) {
                label = ((MIPSLabel) element).getLabel();
            } else {
                return Optional.empty();
            }

            if (elements.getLabels().amount(label) > 1) {
                return Optional.of(new MIPSEditorInspectionDuplicateLabel(this, label));
            }

            return Optional.empty();
        }
    }
}
