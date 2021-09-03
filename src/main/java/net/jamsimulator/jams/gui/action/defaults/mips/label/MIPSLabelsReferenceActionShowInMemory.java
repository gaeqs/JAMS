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

package net.jamsimulator.jams.gui.action.defaults.mips.label;

import javafx.scene.input.KeyCombination;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.action.context.ContextAction;
import net.jamsimulator.jams.gui.bar.BarButton;
import net.jamsimulator.jams.gui.editor.code.CodeFileEditor;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.main.MainMenuBar;
import net.jamsimulator.jams.gui.mips.simulator.label.LabelTable;
import net.jamsimulator.jams.gui.mips.simulator.label.LabelTableReference;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.manager.ResourceProvider;

public class MIPSLabelsReferenceActionShowInMemory extends ContextAction {

    public static final String NAME = "MIPS_LABELS_REFERENCE_SHOW_IN_MEMORY";
    public static final KeyCombination DEFAULT_COMBINATION = null;

    public MIPSLabelsReferenceActionShowInMemory(ResourceProvider provider) {
        super(provider,
                NAME,
                RegionTags.MIPS_SIMULATION_LABELS_REFERENCE,
                Messages.ACTION_MIPS_SIMULATION_LABELS_REFERENCE_SHOW_IN_MEMORY,
                DEFAULT_COMBINATION,
                LabelsTagRegions.SHOW,
                null,
                null
        );
    }

    @Override
    public void run(Object node) {
        if (!(node instanceof LabelTableReference reference)) return;
        var explorer = (LabelTable) reference.getExplorer();

        explorer.getSimulationPane().getBarMap().searchButton("memory").ifPresent(BarButton::show);
        var memoryPane = explorer.getSimulationPane().getMemoryPane();
        memoryPane.selectAddress(reference.getReference().address());
    }

    @Override
    public void runFromMenu() {
    }

    @Override
    public boolean supportsExplorerState(Explorer explorer) {
        return explorer instanceof LabelTable && explorer.getSelectedElements().size() == 1 &&
                explorer.getSelectedElements().get(0) instanceof LabelTableReference;
    }

    @Override
    public boolean supportsTextEditorState(CodeFileEditor editor) {
        return false;
    }

    @Override
    public boolean supportsMainMenuState(MainMenuBar bar) {
        return false;
    }
}
