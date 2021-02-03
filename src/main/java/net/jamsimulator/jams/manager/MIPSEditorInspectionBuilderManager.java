package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspection;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspectionBuilder;
import net.jamsimulator.jams.gui.mips.inspection.error.*;
import net.jamsimulator.jams.gui.mips.inspection.event.MIPSEditorBuilderUnregisterEvent;
import net.jamsimulator.jams.gui.mips.inspection.event.MIPSEditorErrorBuilderRegisterEvent;
import net.jamsimulator.jams.gui.mips.inspection.warning.MIPSEditorInspectionRegisterAtUse;

import java.util.Collection;

public class MIPSEditorInspectionBuilderManager extends Manager<MIPSEditorInspectionBuilder<?>> {

    public static final MIPSEditorInspectionBuilderManager INSTANCE = new MIPSEditorInspectionBuilderManager();

    /**
     * Creates the manager.
     */
    public MIPSEditorInspectionBuilderManager() {
        super(MIPSEditorErrorBuilderRegisterEvent.Before::new, MIPSEditorErrorBuilderRegisterEvent.After::new,
                MIPSEditorBuilderUnregisterEvent.Before::new, MIPSEditorBuilderUnregisterEvent.After::new);
    }

    @Override
    protected void loadDefaultElements() {
        add(new MIPSEditorInspectionDirectiveNotFound.Builder());
        add(new MIPSEditorInspectionDuplicateGlobalLabel.Builder());
        add(new MIPSEditorInspectionDuplicateLabel.Builder());
        add(new MIPSEditorInspectionIllegalLabel.Builder());
        add(new MIPSEditorInspectionInstructionNotFound.Builder());
        add(new MIPSEditorInspectionInvalidDirectiveParameter.Builder());
        add(new MIPSEditorInspectionInvalidInstructionParameter.Builder());
        add(new MIPSEditorInspectionLabelNotFound.Builder());

        add(new MIPSEditorInspectionRegisterAtUse.Builder());
    }

    public void getInspections(MIPSCodeElement element, MIPSFileElements elements, Collection<? super MIPSEditorInspection> collection) {
        for (MIPSEditorInspectionBuilder<?> builder : this) {
            builder.tryToBuild(element, elements).ifPresent(collection::add);
        }
    }
}
