/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.gui.mips.inspection;

import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.gui.mips.inspection.error.*;
import net.jamsimulator.jams.gui.mips.inspection.warning.MIPSEditorInspectionDirectiveNotFound;
import net.jamsimulator.jams.gui.mips.inspection.warning.MIPSEditorInspectionRegisterUsingAt;
import net.jamsimulator.jams.gui.mips.inspection.warning.MIPSEditorInspectionUsingEquivalent;
import net.jamsimulator.jams.manager.Manager;

import java.util.Collection;

/**
 * This singleton stores all {@link MIPSEditorInspectionBuilder}s that projects may use.
 * <p>
 * To register an {@link MIPSEditorInspectionBuilder} use {@link #add(Object)}.
 * To unregister an {@link MIPSEditorInspectionBuilder} use {@link #remove(Object)}.
 * An {@link MIPSEditorInspectionBuilder}'s removal from the manager doesn't make editors to stop using
 * it inmediatelly.
 */
public final class MIPSEditorInspectionBuilderManager extends Manager<MIPSEditorInspectionBuilder> {

    public static final String NAME = "mips_editor_inspection_builder";
    public static final MIPSEditorInspectionBuilderManager INSTANCE = new MIPSEditorInspectionBuilderManager();

    /**
     * Creates the manager.
     */
    private MIPSEditorInspectionBuilderManager() {
        super(MIPSEditorInspectionBuilder.class, true);
    }

    @Override
    protected void loadDefaultElements() {
        add(new MIPSEditorInspectionDuplicateGlobalLabel.Builder());
        add(new MIPSEditorInspectionDuplicateLabel.Builder());
        add(new MIPSEditorInspectionIllegalLabel.Builder());
        add(new MIPSEditorInspectionInstructionNotFound.Builder());
        add(new MIPSEditorInspectionInvalidDirectiveParameter.Builder());
        add(new MIPSEditorInspectionInvalidMacroParametersAmount.Builder());
        add(new MIPSEditorInspectionLabelNotFound.Builder());
        add(new MIPSEditorInspectionMacroNotFound.Builder());

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
     * @param elements   the {@link MIPSFileElements} the given element is inside.
     * @param collection the collection where the inspections will be added.
     */
    public void getInspections(MIPSCodeElement element, MIPSFileElements elements, Collection<? super MIPSEditorInspection> collection) {
        for (MIPSEditorInspectionBuilder<?> builder : this) {
            builder.tryToBuild(element, elements).ifPresent(collection::add);
        }
    }
}
