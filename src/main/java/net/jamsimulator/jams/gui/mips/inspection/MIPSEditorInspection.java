package net.jamsimulator.jams.gui.mips.inspection;

import net.jamsimulator.jams.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an inspection inside a {@link net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement code element}.
 * <p>
 * Inspections inform the developer about errors and warnings.
 * <p>
 * Inspections are created using {@link MIPSEditorInspectionBuilder builder}s. You must implement a inspection and
 * a builder class to create a new error or warning.
 */
public class MIPSEditorInspection {

    private final MIPSEditorInspectionBuilder<?> builder;
    private final Map<String, String> replacements;

    /**
     * Creates an instance of the inspection.
     *
     * @param builder      the builder representing this inspection.
     * @param replacements the replacement texts for the description.
     */
    public MIPSEditorInspection(MIPSEditorInspectionBuilder<?> builder, Map<String, String> replacements) {
        this.builder = builder;
        this.replacements = replacements == null ? new HashMap<>() : replacements;
    }

    /**
     * Returns the {@link MIPSEditorInspectionBuilder builder} representing this inspection.
     *
     * @return the builder.
     */
    public MIPSEditorInspectionBuilder<?> getBuilder() {
        return builder;
    }

    /**
     * Returns the description of the inspection in the selected language.
     * This description has also its placeholders replaced.
     *
     * @return the description.
     */
    public String getParsedDescription() {
        String base = builder.getDescription();

        for (Map.Entry<String, String> replacement : replacements.entrySet()) {
            base = base.replace(replacement.getKey(), replacement.getValue());
        }

        return StringUtils.parseEscapeCharacters(base);
    }
}
