package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.gui.mips.error.MIPSEditorError;
import net.jamsimulator.jams.gui.mips.error.MIPSEditorErrorBuilder;
import net.jamsimulator.jams.gui.mips.error.defaults.*;
import net.jamsimulator.jams.gui.mips.error.event.MIPSEditorBuilderUnregisterEvent;
import net.jamsimulator.jams.gui.mips.error.event.MIPSEditorErrorBuilderRegisterEvent;

import java.util.Collection;

public class MIPSEditorErrorBuilderManager extends Manager<MIPSEditorErrorBuilder<?>> {

    public static final MIPSEditorErrorBuilderManager INSTANCE = new MIPSEditorErrorBuilderManager();

    /**
     * Creates the manager.
     */
    public MIPSEditorErrorBuilderManager() {
        super(MIPSEditorErrorBuilderRegisterEvent.Before::new, MIPSEditorErrorBuilderRegisterEvent.After::new,
                MIPSEditorBuilderUnregisterEvent.Before::new, MIPSEditorBuilderUnregisterEvent.After::new);
    }

    @Override
    protected void loadDefaultElements() {
        add(new MIPSEditorErrorDirectiveNotFound.Builder());
        add(new MIPSEditorErrorDuplicateGlobalLabel.Builder());
        add(new MIPSEditorErrorDuplicateLabel.Builder());
        add(new MIPSEditorErrorIllegalLabel.Builder());
        add(new MIPSEditorErrorInstructionNotFound.Builder());
        add(new MIPSEditorErrorInvalidDirectiveParameter.Builder());
        add(new MIPSEditorErrorInvalidInstructionParameter.Builder());
        add(new MIPSEditorErrorLabelNotFound.Builder());
    }

    public void getErrors(MIPSCodeElement element, MIPSFileElements elements, Collection<? super MIPSEditorError> collection) {
        for (MIPSEditorErrorBuilder<?> builder : this) {
            builder.tryToBuild(element, elements).ifPresent(collection::add);
        }
    }
}
