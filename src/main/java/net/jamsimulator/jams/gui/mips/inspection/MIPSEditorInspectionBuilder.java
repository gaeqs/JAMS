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
 * <p>
 * These builders also gives general information about the representing inspection,
 * such as the name, the description or whether it is an error or a warning.
 *
 * @param <Error> the {@link MIPSEditorInspection} this builder is representing.
 */
public abstract class MIPSEditorInspectionBuilder<Error extends MIPSEditorInspection> implements Labeled {

    /**
     * The error node prefix for language messages.
     */
    public static final String ERROR_LANGUAGE_NODE = "EDITOR_MIPS_ERROR_";

    /**
     * The warning node prefix for language messages.
     */
    public static final String WARNING_LANGUAGE_NODE = "EDITOR_MIPS_WARNING_";

    private final String name;
    private final boolean error;

    /**
     * Creates the builder.
     *
     * @param name  the name of the inspection.
     * @param error whether the inspection is an error. If not, it is a warning.
     */
    public MIPSEditorInspectionBuilder(String name, boolean error) {
        this.name = name;
        this.error = error;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the description of the inspection in the selected language.
     * This description won't have any placeholder replaced.
     *
     * @return the description.
     */
    public String getDescription() {
        return Jams.getLanguageManager().getSelected().getOrDefault(
                (error ? ERROR_LANGUAGE_NODE : WARNING_LANGUAGE_NODE) + name);
    }

    /**
     * Returns whether this inspection is an error. If not, it is a warning.
     *
     * @return whether it is an error.
     */
    public boolean isError() {
        return error;
    }

    /**
     * Inspects the given {@link MIPSCodeElement}. If the element has the represented inspection,
     * this method returns an {@link Optional} containing an insance of the represented {@link MIPSEditorInspection}.
     *
     * @param element  the element to inspect.
     * @param elements the {@link MIPSFileElements} instance the element is inside of.
     * @return an instance of the represented inspection, if it matches.
     */
    public abstract Optional<Error> tryToBuild(MIPSCodeElement element, MIPSFileElements elements);

}
