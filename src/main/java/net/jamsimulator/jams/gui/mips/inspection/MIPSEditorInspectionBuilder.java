package net.jamsimulator.jams.gui.mips.inspection;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.manager.Labeled;

import java.util.Optional;

/**
 * Represents a builder for {@link MIPSEditorInspection}.
 * <p>
 * Instances of this class must check whether a {@link MIPSCodeElement}
 * should have the representing inspection marked. This procedure must be
 * implemented in the method {@link #tryToBuild(MIPSCodeElement, MIPSFileElements)}.
 *
 * These builders also gives general information about the representing inspection,
 * such as the name, the description or whether it is an error or a warning.
 *
 * @param <Error> the {@link MIPSEditorInspection} this builder is representing.
 */
public abstract class MIPSEditorInspectionBuilder<Error extends MIPSEditorInspection> implements Labeled {

    public static final String ERROR_LANGUAGE_NODE = "EDITOR_MIPS_ERROR_";
    public static final String WARNING_LANGUAGE_NODE = "EDITOR_MIPS_WARNING_";

    private final String name;
    private final boolean error;

    public MIPSEditorInspectionBuilder(String name, boolean error) {
        this.name = name;
        this.error = error;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getDescription() {
        return Jams.getLanguageManager().getSelected().getOrDefault(
                (error ? ERROR_LANGUAGE_NODE : WARNING_LANGUAGE_NODE) + name);
    }

    public boolean isError() {
        return error;
    }

    public abstract Optional<Error> tryToBuild(MIPSCodeElement element, MIPSFileElements elements);

}
