package net.jamsimulator.jams.gui.mips.error.defaults;

import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSDirectiveParameter;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSLabel;
import net.jamsimulator.jams.gui.mips.error.MIPSEditorError;
import net.jamsimulator.jams.gui.mips.error.MIPSEditorErrorBuilder;
import net.jamsimulator.jams.mips.directive.parameter.DirectiveParameterType;
import net.jamsimulator.jams.utils.LabelUtils;

import java.util.Map;
import java.util.Optional;

public class MIPSEditorErrorIllegalLabel extends MIPSEditorError {

    public static String NAME = "ILLEGAL_LABEL";

    public MIPSEditorErrorIllegalLabel(MIPSEditorErrorBuilder<?> builder, String label) {
        super(builder, Map.of("{NAME}", label));
    }

    public static class Builder extends MIPSEditorErrorBuilder<MIPSEditorErrorIllegalLabel> {

        public Builder() {
            super(NAME);
        }

        @Override
        public Optional<MIPSEditorErrorIllegalLabel> tryToBuild(MIPSCodeElement element, MIPSFileElements elements) {
            String label;

            if (element instanceof MIPSDirectiveParameter) {
                var type =
                        ((MIPSDirectiveParameter) element).getDirective().getDirective()
                                .getParameterTypeFor(((MIPSDirectiveParameter) element).getIndex());

                if (type != DirectiveParameterType.LABEL) return Optional.empty();
                label = element.getSimpleText();
            } else if (element instanceof MIPSLabel) {
                label = ((MIPSLabel) element).getLabel();
            } else {
                return Optional.empty();
            }

            if (!LabelUtils.isLabelLegal(label)) {
                return Optional.of(new MIPSEditorErrorIllegalLabel(this, label));
            }

            return Optional.empty();
        }
    }
}
