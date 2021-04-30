package net.jamsimulator.jams.gui.mips.inspection.error;

import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSMacroCall;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspection;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspectionBuilder;

import java.util.Map;
import java.util.Optional;

/**
 * This error appears when the written label is not found.
 */
public class MIPSEditorInspectionInvalidMacroParametersAmount extends MIPSEditorInspection {

    public static String NAME = "INVALID_MACRO_PARAMETERS_AMOUNT";

    public MIPSEditorInspectionInvalidMacroParametersAmount(MIPSEditorInspectionBuilder<?> builder, String macro, int expected, int found) {
        super(builder, Map.of("{NAME}", macro, "{EXPECTED}", Integer.toString(expected), "{FOUND}", Integer.toString(found)));
    }

    public static class Builder extends MIPSEditorInspectionBuilder<MIPSEditorInspectionInvalidMacroParametersAmount> {

        public Builder() {
            super(NAME, true);
        }

        @Override
        public Optional<MIPSEditorInspectionInvalidMacroParametersAmount> tryToBuild(MIPSCodeElement element, MIPSFileElements elements) {
            if (!element.getLine().areAllReplacementsValid() || element.usesMacroParameter()) return Optional.empty();

            if (element instanceof MIPSMacroCall) {
                var name = element.getSimpleText();
                var macro = elements.getMacro(name).orElse(null);

                if (macro == null || macro.getStart().getStart() > element.getStartIndex()) {
                    return Optional.empty();
                }

                if (macro.getParameters().length != ((MIPSMacroCall) element).getParameters().size()) {
                    return Optional.of(new MIPSEditorInspectionInvalidMacroParametersAmount(this, name,
                            macro.getParameters().length, ((MIPSMacroCall) element).getParameters().size()));
                }
            }

            return Optional.empty();
        }
    }
}
