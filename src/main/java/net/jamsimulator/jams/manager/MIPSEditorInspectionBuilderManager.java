package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspection;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspectionBuilder;
import net.jamsimulator.jams.gui.mips.inspection.error.*;
import net.jamsimulator.jams.gui.mips.inspection.event.MIPSEditorBuilderUnregisterEvent;
import net.jamsimulator.jams.gui.mips.inspection.event.MIPSEditorErrorBuilderRegisterEvent;
import net.jamsimulator.jams.gui.mips.inspection.warning.MIPSEditorInspectionDirectiveNotFound;
import net.jamsimulator.jams.gui.mips.inspection.warning.MIPSEditorInspectionRegisterUsingAt;
import net.jamsimulator.jams.gui.mips.inspection.warning.MIPSEditorInspectionUsingEquivalent;

import java.util.Collection;

/**
 * This singleton stores all {@link MIPSEditorInspectionBuilder}s that projects may use.
 * <p>
 * To register an {@link MIPSEditorInspectionBuilder} use {@link #add(Object)}.
 * To unregister an {@link MIPSEditorInspectionBuilder} use {@link #remove(Object)}.
 * An {@link MIPSEditorInspectionBuilder}'s removal from the manager doesn't make editors to stop using
 * it inmediatelly.
 */
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
        add(new MIPSEditorInspectionDuplicateGlobalLabel.Builder());
        add(new MIPSEditorInspectionDuplicateLabel.Builder());
        add(new MIPSEditorInspectionIllegalLabel.Builder());
        add(new MIPSEditorInspectionInstructionNotFound.Builder());
        add(new MIPSEditorInspectionInvalidDirectiveParameter.Builder());
        add(new MIPSEditorInspectionInvalidInstructionParameter.Builder());
        add(new MIPSEditorInspectionLabelNotFound.Builder());

        add(new MIPSEditorInspectionDirectiveNotFound.Builder());
        add(new MIPSEditorInspectionRegisterUsingAt.Builder());
        add(new MIPSEditorInspectionUsingEquivalent.Builder());
    }

    /**
     * Adds to the given collection a new instance of all inspections the given element matches.
     * <p>
     * This method checks all registered inspections in this manager.
     *
     * @param element    the element to inspect.
     * @param elements   the {@link MIPSFileElements} the given element is inside of.
     * @param collection the collection where the inspections will be added.
     */
    public void getInspections(MIPSCodeElement element, MIPSFileElements elements, Collection<? super MIPSEditorInspection> collection) {
        for (MIPSEditorInspectionBuilder<?> builder : this) {
            builder.tryToBuild(element, elements).ifPresent(collection::add);
        }
    }
}
