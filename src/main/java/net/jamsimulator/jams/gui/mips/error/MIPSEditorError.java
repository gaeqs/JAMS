package net.jamsimulator.jams.gui.mips.error;

import net.jamsimulator.jams.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class MIPSEditorError {

    private final MIPSEditorErrorBuilder<?> builder;
    private final Map<String, String> replacements;

    public MIPSEditorError(MIPSEditorErrorBuilder<?> builder, Map<String, String> replacements) {
        this.builder = builder;
        this.replacements = replacements == null ? new HashMap<>() : replacements;
    }

    public MIPSEditorErrorBuilder<?> getBuilder() {
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
