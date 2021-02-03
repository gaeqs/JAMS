package net.jamsimulator.jams.gui.mips.inspection.error;

import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSDirective;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspection;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspectionBuilder;

import java.util.Map;
import java.util.Optional;

public class MIPSEditorInspectionDirectiveNotFound extends MIPSEditorInspection {

    public static String NAME = "DIRECTIVE_NOT_FOUND";

    public MIPSEditorInspectionDirectiveNotFound(MIPSEditorInspectionBuilder<?> builder, String directive) {
        super(builder, Map.of("{NAME}", directive));
    }

    public static class Builder extends MIPSEditorInspectionBuilder<MIPSEditorInspectionDirectiveNotFound> {

        public Builder() {
            super(NAME, true);
        }

        @Override
        public Optional<MIPSEditorInspectionDirectiveNotFound> tryToBuild(MIPSCodeElement element, MIPSFileElements elements) {
            if (element instanceof MIPSDirective && ((MIPSDirective) element).getDirective() == null) {
                return Optional.of(new MIPSEditorInspectionDirectiveNotFound(this, element.getSimpleText()));
            }

            return Optional.empty();
        }
    }
}
