package net.jamsimulator.jams.gui.mips.error;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.manager.Labeled;

import java.util.Optional;

public abstract class MIPSEditorErrorBuilder<Error extends MIPSEditorError> implements Labeled {

    public static final String LANGUAGE_NODE = "EDITOR_MIPS_ERROR_";

    private final String name;

    public MIPSEditorErrorBuilder(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getDescription() {
        return Jams.getLanguageManager().getSelected().getOrDefault(LANGUAGE_NODE + name);
    }

    public abstract Optional<Error> tryToBuild(MIPSCodeElement element, MIPSFileElements elements);

}
