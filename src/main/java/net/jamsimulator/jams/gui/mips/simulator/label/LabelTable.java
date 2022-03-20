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

package net.jamsimulator.jams.gui.mips.simulator.label;

import javafx.scene.control.ScrollPane;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;
import net.jamsimulator.jams.gui.mips.project.MIPSSimulationPane;
import net.jamsimulator.jams.mips.assembler.AssemblerScope;

import java.util.Comparator;

public class LabelTable extends Explorer {

    public static final String STYLE_CLASS = "label-table";

    private final MIPSSimulationPane simulationPane;

    public LabelTable(ScrollPane scrollPane, MIPSSimulationPane simulationPane) {
        super(scrollPane, false, true);
        getStyleClass().add(STYLE_CLASS);
        this.simulationPane = simulationPane;

        var globalScope = simulationPane.getSimulation().getSource().globalScope();

        globalScope.getChildren().stream()
                .filter(it -> !it.getScopeLabels().isEmpty())
                .forEach(this::addFile);
    }

    public MIPSSimulationPane getSimulationPane() {
        return simulationPane;
    }

    @Override
    protected void generateMainSection() {
        mainSection = new ExplorerSection(this, null,
                "main", 0, Comparator.comparing(ExplorerElement::getVisibleName));
        hideMainSectionRepresentation();
        getChildren().add(mainSection);
    }


    private void addFile(AssemblerScope scope) {
        var section = new LabelTableFile(this, mainSection, scope.getName(), 1);
        mainSection.addElement(section);

        scope.getChildren().forEach(it -> addMacro(it, section, 1));

        scope.getScopeLabels().forEach((name, label) ->
                section.addElement(new LabelTableLabel(this, section, label, 1)));
    }

    private void addMacro(AssemblerScope scope, ExplorerSection parent, int level) {
        var section = new LabelTableMacro(this, parent, scope.getName(), level);
        parent.addElement(section);

        scope.getChildren().forEach(it -> addMacro(it, section, level + 1));

        scope.getScopeLabels().forEach((name, label) ->
                section.addElement(new LabelTableLabel(this, section, label, level + 1)));
    }
}
