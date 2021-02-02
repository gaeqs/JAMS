package net.jamsimulator.jams.gui.mips.error.defaults;

import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSDirectiveParameter;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSLabel;
import net.jamsimulator.jams.gui.mips.error.MIPSEditorError;
import net.jamsimulator.jams.gui.mips.error.MIPSEditorErrorBuilder;

import java.util.Map;
import java.util.Optional;

public class MIPSEditorErrorDuplicateGlobalLabel extends MIPSEditorError {

    public static String NAME = "DUPLICATE_GLOBAL_LABEL";

    public MIPSEditorErrorDuplicateGlobalLabel(MIPSEditorErrorBuilder<?> builder, String label, String file) {
        super(builder, Map.of("{NAME}", label));
    }

    public static class Builder extends MIPSEditorErrorBuilder<MIPSEditorErrorDuplicateGlobalLabel> {

        public Builder() {
            super(NAME);
        }

        @Override
        public Optional<MIPSEditorErrorDuplicateGlobalLabel> tryToBuild(MIPSCodeElement element, MIPSFileElements elements) {
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
                return Optional.of(new MIPSEditorErrorDuplicateGlobalLabel(this, label, "NOT IMPLEMENTED"));
            }

            return Optional.empty();
        }
    }
}
