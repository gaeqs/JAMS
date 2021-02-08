package net.jamsimulator.jams.gui.mips.inspection.error;

import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSDirectiveParameter;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSLabel;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspection;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspectionBuilder;
import net.jamsimulator.jams.mips.directive.parameter.DirectiveParameterType;
import net.jamsimulator.jams.utils.LabelUtils;

import java.util.Map;
import java.util.Optional;

/**
 * This error appears when label has illegal characters.
 */
public class MIPSEditorInspectionIllegalLabel extends MIPSEditorInspection {

    public static String NAME = "ILLEGAL_LABEL";

    public MIPSEditorInspectionIllegalLabel(MIPSEditorInspectionBuilder<?> builder, String label) {
        super(builder, Map.of("{NAME}", label));
    }

    public static class Builder extends MIPSEditorInspectionBuilder<MIPSEditorInspectionIllegalLabel> {

        public Builder() {
            super(NAME, true);
        }

        @Override
        public Optional<MIPSEditorInspectionIllegalLabel> tryToBuild(MIPSCodeElement element, MIPSFileElements elements) {
            if(element.getLine().isUsingReplacements()) return Optional.empty();

            String label;

            if (element instanceof MIPSDirectiveParameter
                    && ((MIPSDirectiveParameter) element).getDirective().getDirective().isPresent()) {
                var type =
                        ((MIPSDirectiveParameter) element).getDirective().getDirective().get()
                                .getParameterTypeFor(((MIPSDirectiveParameter) element).getIndex());

                if (type != DirectiveParameterType.LABEL) return Optional.empty();
                label = element.getSimpleText();
            } else if (element instanceof MIPSLabel) {
                label = ((MIPSLabel) element).getLabel();
            } else {
                return Optional.empty();
            }

            if (!LabelUtils.isLabelLegal(label)) {
                return Optional.of(new MIPSEditorInspectionIllegalLabel(this, label));
            }

            return Optional.empty();
        }
    }
}
