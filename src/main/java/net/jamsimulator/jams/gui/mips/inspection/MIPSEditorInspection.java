package net.jamsimulator.jams.gui.mips.inspection;

import net.jamsimulator.jams.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class MIPSEditorInspection {

    private final MIPSEditorInspectionBuilder<?> builder;
    private final Map<String, String> replacements;

    public MIPSEditorInspection(MIPSEditorInspectionBuilder<?> builder, Map<String, String> replacements) {
        this.builder = builder;
        this.replacements = replacements == null ? new HashMap<>() : replacements;
    }

    public MIPSEditorInspectionBuilder<?> getBuilder() {
        return builder;
    }

    public String getParsedDescription() {
        String base = builder.getDescription();

        for (Map.Entry<String, String> replacement : replacements.entrySet()) {
            base = base.replace(replacement.getKey(), replacement.getValue());
        }

        return StringUtils.parseEscapeCharacters(base);
    }
}
