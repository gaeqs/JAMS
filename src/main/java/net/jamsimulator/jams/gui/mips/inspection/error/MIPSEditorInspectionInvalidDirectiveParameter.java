package net.jamsimulator.jams.gui.mips.inspection.error;

import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSDirectiveParameter;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspection;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspectionBuilder;

import java.util.Map;
import java.util.Optional;

/**
 * This error appears when a directive parameter is invalid.
 */
public class MIPSEditorInspectionInvalidDirectiveParameter extends MIPSEditorInspection {

    public static String NAME = "INVALID_DIRECTIVE_PARAMETER";

    public MIPSEditorInspectionInvalidDirectiveParameter(MIPSEditorInspectionBuilder<?> builder, String parameter) {
        super(builder, Map.of("{NAME}", parameter));
    }

    public static class Builder extends MIPSEditorInspectionBuilder<MIPSEditorInspectionInvalidDirectiveParameter> {

        public Builder() {
            super(NAME, true);
        }

        @Override
        public Optional<MIPSEditorInspectionInvalidDirectiveParameter> tryToBuild(MIPSCodeElement element, MIPSFileElements elements) {
            if(!element.getLine().areAllReplacementsValid()) return Optional.empty();
            if (element instanceof MIPSDirectiveParameter) {
                var param = (MIPSDirectiveParameter) element;
                var directive = param.getDirective().getDirective();
                if (directive.isPresent() && !directive.get().isParameterValidInContext(param.getIndex(), param.getSimpleText(), elements)) {
                    return Optional.of(new MIPSEditorInspectionInvalidDirectiveParameter(this, element.getSimpleText()));
                }
            }

            return Optional.empty();
        }
    }
}
