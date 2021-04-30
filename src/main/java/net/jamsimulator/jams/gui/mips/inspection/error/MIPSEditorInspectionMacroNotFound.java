package net.jamsimulator.jams.gui.mips.inspection.error;

import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSInstructionParameterPart;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSMacroCall;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspection;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspectionBuilder;

import java.util.Map;
import java.util.Optional;

/**
 * This error appears when the written label is not found.
 */
public class MIPSEditorInspectionMacroNotFound extends MIPSEditorInspection {

    public static String NAME = "MACRO_NOT_FOUND";

    public MIPSEditorInspectionMacroNotFound(MIPSEditorInspectionBuilder<?> builder, String macro) {
        super(builder, Map.of("{NAME}", macro));
    }

    public static class Builder extends MIPSEditorInspectionBuilder<MIPSEditorInspectionMacroNotFound> {

        public Builder() {
            super(NAME, true);
        }

        @Override
        public Optional<MIPSEditorInspectionMacroNotFound> tryToBuild(MIPSCodeElement element, MIPSFileElements elements) {
            if (!element.getLine().areAllReplacementsValid() || element.usesMacroParameter()) return Optional.empty();

            if (element instanceof MIPSMacroCall) {
                var name = element.getSimpleText();

                var macro = elements.getMacro(name).orElse(null);

                if(macro == null || macro.getStart().getStart() > element.getStartIndex()) {
                    return Optional.of(new MIPSEditorInspectionMacroNotFound(this, element.getSimpleText()));
                }
            }

            return Optional.empty();
        }
    }
}
