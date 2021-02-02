package net.jamsimulator.jams.gui.mips.error.defaults;

import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSDirectiveParameter;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSLabel;
import net.jamsimulator.jams.gui.mips.error.MIPSEditorError;
import net.jamsimulator.jams.gui.mips.error.MIPSEditorErrorBuilder;

import java.util.Map;
import java.util.Optional;

public class MIPSEditorErrorDuplicateLabel extends MIPSEditorError {

    public static String NAME = "DUPLICATE_LABEL";

    public MIPSEditorErrorDuplicateLabel(MIPSEditorErrorBuilder<?> builder, String label) {
        super(builder, Map.of("{NAME}", label));
    }

    public static class Builder extends MIPSEditorErrorBuilder<MIPSEditorErrorDuplicateLabel> {

        public Builder() {
            super(NAME);
        }

        @Override
        public Optional<MIPSEditorErrorDuplicateLabel> tryToBuild(MIPSCodeElement element, MIPSFileElements elements) {
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
                return Optional.of(new MIPSEditorErrorDuplicateLabel(this, label));
            }

            return Optional.empty();
        }
    }
}
