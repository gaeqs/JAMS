package net.jamsimulator.jams.gui.mips.error.defaults;

import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSDirective;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.gui.mips.error.MIPSEditorError;
import net.jamsimulator.jams.gui.mips.error.MIPSEditorErrorBuilder;

import java.util.Map;
import java.util.Optional;

public class MIPSEditorErrorDirectiveNotFound extends MIPSEditorError {

    public static String NAME = "DIRECTIVE_NOT_FOUND";

    public MIPSEditorErrorDirectiveNotFound(MIPSEditorErrorBuilder<?> builder, String directive) {
        super(builder, Map.of("{NAME}", directive));
    }

    public static class Builder extends MIPSEditorErrorBuilder<MIPSEditorErrorDirectiveNotFound> {

        public Builder() {
            super(NAME);
        }

        @Override
        public Optional<MIPSEditorErrorDirectiveNotFound> tryToBuild(MIPSCodeElement element, MIPSFileElements elements) {
            if (element instanceof MIPSDirective && ((MIPSDirective) element).getDirective() == null) {
                return Optional.of(new MIPSEditorErrorDirectiveNotFound(this, element.getSimpleText()));
            }

            return Optional.empty();
        }
    }
}
