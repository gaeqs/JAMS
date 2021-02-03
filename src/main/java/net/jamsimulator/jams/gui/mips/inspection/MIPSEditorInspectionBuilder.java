package net.jamsimulator.jams.gui.mips.inspection;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.manager.Labeled;

import java.util.Optional;

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
