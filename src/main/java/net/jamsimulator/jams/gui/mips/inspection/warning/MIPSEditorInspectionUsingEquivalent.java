package net.jamsimulator.jams.gui.mips.inspection.warning;

import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
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
public class MIPSEditorInspectionUsingEquivalent extends MIPSEditorInspection {

    public static String NAME = "USING_EQUIVALENT";

    public MIPSEditorInspectionUsingEquivalent(MIPSEditorInspectionBuilder<?> builder) {
        super(builder, Map.of());
    }

    public static class Builder extends MIPSEditorInspectionBuilder<MIPSEditorInspectionUsingEquivalent> {

        public Builder() {
            super(NAME, false);
        }

        @Override
        public Optional<MIPSEditorInspectionUsingEquivalent> tryToBuild(MIPSCodeElement element, MIPSFileElements elements) {
            if(!element.getLine().areAllReplacementsValid()) {
                return Optional.of(new MIPSEditorInspectionUsingEquivalent(this));
            }

            return Optional.empty();
        }
    }
}
