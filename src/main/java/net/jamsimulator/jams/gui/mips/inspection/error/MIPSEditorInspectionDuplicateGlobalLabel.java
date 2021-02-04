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
 * This error appears when the given label is duplicated in another file.
 */
public class MIPSEditorInspectionDuplicateGlobalLabel extends MIPSEditorInspection {

    public static String NAME = "DUPLICATE_GLOBAL_LABEL";

    public MIPSEditorInspectionDuplicateGlobalLabel(MIPSEditorInspectionBuilder<?> builder, String label, String file) {
        super(builder, Map.of("{NAME}", label));
    }

    public static class Builder extends MIPSEditorInspectionBuilder<MIPSEditorInspectionDuplicateGlobalLabel> {

        public Builder() {
            super(NAME, true);
        }

        @Override
        public Optional<MIPSEditorInspectionDuplicateGlobalLabel> tryToBuild(MIPSCodeElement element, MIPSFileElements elements) {
            String label;
            boolean global;

            if (element instanceof MIPSDirectiveParameter) {
                if (!((MIPSDirectiveParameter) element).isRegisteredLabel()) return Optional.empty();
                global = ((MIPSDirectiveParameter) element).isGlobalLabel();
                label = element.getSimpleText();
            } else if (element instanceof MIPSLabel) {
                global = ((MIPSLabel) element).isGlobal();
                label = ((MIPSLabel) element).getLabel();
            } else {
                return Optional.empty();
            }

            var optional = elements.getFilesToAssemble();
            if (optional.isEmpty()) return Optional.empty();
            int amount = optional.get().getGlobalLabels().amount(label);
            if (global) amount--;
            if (amount > 0) {
                return Optional.of(new MIPSEditorInspectionDuplicateGlobalLabel(this, label, "NOT IMPLEMENTED"));
            }

            return Optional.empty();
        }
    }
}
