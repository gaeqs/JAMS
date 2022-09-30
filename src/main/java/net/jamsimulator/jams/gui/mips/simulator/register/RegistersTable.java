/*
 *  MIT License
 *
 *  Copyright (c) 2022 Gael Rial Costas
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

package net.jamsimulator.jams.gui.mips.simulator.register;

import javafx.scene.control.TableView;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.ActionRegion;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageTableColumn;
import net.jamsimulator.jams.mips.register.COP0Register;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.event.SimulationStopEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationUndoStepEvent;
import net.jamsimulator.jams.utils.NumberRepresentation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RegistersTable extends TableView<RegisterPropertyWrapper> implements ActionRegion {

    private final List<RegisterPropertyWrapper> registers = new ArrayList<>();

    private NumberRepresentation representation;

    public RegistersTable(Simulation<?> simulation, Set<Register> registers,
                          NumberRepresentation representation, boolean showSelColumn) {
        this.representation = representation;

        getStyleClass().add("table-view-horizontal-fit");
        setEditable(true);
        setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);

        var identifier = new LanguageTableColumn<RegisterPropertyWrapper, String>(Messages.REGISTERS_ID);
        var name = new LanguageTableColumn<RegisterPropertyWrapper, String>(Messages.REGISTERS_NAME);
        var value = new LanguageTableColumn<RegisterPropertyWrapper, String>(Messages.REGISTERS_VALUE);

        identifier.setCellValueFactory(p -> p.getValue().identifierProperty());
        name.setCellValueFactory(p -> p.getValue().nameProperty());
        value.setCellValueFactory(p -> p.getValue().valueProperty());

        value.setCellFactory(t -> new RegisterValueCell());

        identifier.setEditable(false);
        name.setEditable(false);
        value.setEditable(true);

        if (showSelColumn) {
            var sel = new LanguageTableColumn<RegisterPropertyWrapper, String>(Messages.REGISTERS_SELECTION);
            sel.setCellValueFactory(p -> p.getValue().selectionProperty());
            sel.setEditable(false);
            getColumns().setAll(identifier, sel, name, value);
        } else {
            getColumns().setAll(identifier, name, value);
        }

        var registersWithPositiveIds = registers.stream()
                .filter(it -> it.getIdentifier() >= 0)
                .collect(Collectors.toSet());

        var comparator = registers.stream().allMatch(it -> it instanceof COP0Register)
                ? Comparator.comparingInt(Register::getIdentifier)
                .thenComparingInt(it -> ((COP0Register) it).getSelection())
                : Comparator.comparingInt(Register::getIdentifier);

        registers.stream()
                .sorted(comparator)
                .forEach(target -> {
                    Register nextRegister;
                    if (target.getIdentifier() < 0) {
                        nextRegister = null;
                    } else {
                        int nextIdentifier = (target.getIdentifier() + 1) % registersWithPositiveIds.size();
                        nextRegister = registersWithPositiveIds.stream()
                                .filter(it -> it.getIdentifier() == nextIdentifier)
                                .findAny()
                                .orElse(null);
                    }
                    this.registers.add(new RegisterPropertyWrapper(this, target, nextRegister));
                });

        this.registers.forEach(getItems()::add);

        simulation.registerListeners(this, true);
    }

    public NumberRepresentation getRepresentation() {
        return representation;
    }

    public void setRepresentation(NumberRepresentation representation) {
        this.representation = representation;
        refreshRepresentation();
    }

    public void refreshRepresentation() {
        registers.forEach(RegisterPropertyWrapper::refresh);

    }

    @Override
    public boolean supportsActionRegion(String region) {
        return RegionTags.MIPS_SIMULATION.equals(region);
    }

    @Listener(priority = Integer.MIN_VALUE)
    private void onSimulationStop(SimulationStopEvent event) {
        if (event.getSimulation() instanceof MIPSSimulation<?> simulation) {
            simulation.getRegisters().registerListeners(this, true);
            refreshRepresentation();
        }
    }

    @Listener(priority = Integer.MIN_VALUE)
    private void onSimulationUndo(SimulationUndoStepEvent event) {
        refreshRepresentation();
    }
}
