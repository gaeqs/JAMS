package net.jamsimulator.jams.gui.mips.inspection.warning;

import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSDirective;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspection;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspectionBuilder;

import java.util.Map;
import java.util.Optional;

/**
 * This inspection warns the user when a directive is not found.
 * <p>
 * This inspection is NOT an error. This is done to support code with unsupported directives.
 */
public class MIPSEditorInspectionDirectiveNotFound extends MIPSEditorInspection {

    public static String NAME = "DIRECTIVE_NOT_FOUND";

    public MIPSEditorInspectionDirectiveNotFound(MIPSEditorInspectionBuilder<?> builder, String directive) {
        super(builder, Map.of("{NAME}", directive));
    }

    public static class Builder extends MIPSEditorInspectionBuilder<MIPSEditorInspectionDirectiveNotFound> {

        public Builder() {
            super(NAME, false);
        }

        @Override
        public Optional<MIPSEditorInspectionDirectiveNotFound> tryToBuild(MIPSCodeElement element, MIPSFileElements elements) {
            if (element instanceof MIPSDirective && ((MIPSDirective) element).getDirective().isEmpty()) {
                return Optional.of(new MIPSEditorInspectionDirectiveNotFound(this, element.getSimpleText()));
            }

            return Optional.empty();
        }
    }
}
