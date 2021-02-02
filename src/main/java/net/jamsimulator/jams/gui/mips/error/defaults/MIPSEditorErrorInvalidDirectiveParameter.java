package net.jamsimulator.jams.gui.mips.error.defaults;

import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSDirectiveParameter;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.gui.mips.error.MIPSEditorError;
import net.jamsimulator.jams.gui.mips.error.MIPSEditorErrorBuilder;

import java.util.Map;
import java.util.Optional;

public class MIPSEditorErrorInvalidDirectiveParameter extends MIPSEditorError {

    public static String NAME = "INVALID_DIRECTIVE_PARAMETER";

    public MIPSEditorErrorInvalidDirectiveParameter(MIPSEditorErrorBuilder<?> builder, String parameter) {
        super(builder, Map.of("{NAME}", parameter));
    }

    public static class Builder extends MIPSEditorErrorBuilder<MIPSEditorErrorInvalidDirectiveParameter> {

        public Builder() {
            super(NAME);
        }

        @Override
        public Optional<MIPSEditorErrorInvalidDirectiveParameter> tryToBuild(MIPSCodeElement element, MIPSFileElements elements) {
            if (element instanceof MIPSDirectiveParameter) {
                var param = (MIPSDirectiveParameter) element;
                var directive = param.getDirective().getDirective();
                if (directive != null && !directive.isParameterValidInContext(param.getIndex(), param.getSimpleText(), elements)) {
                    return Optional.of(new MIPSEditorErrorInvalidDirectiveParameter(this, element.getSimpleText()));
                }
            }

            return Optional.empty();
        }
    }
}
