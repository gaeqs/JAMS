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

package net.jamsimulator.jams.gui.mips.simulator.register;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.ActionRegion;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageTableColumn;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.event.RegisterChangeValueEvent;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.event.SimulationStartEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationStopEvent;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

public class RegistersTable extends TableView<RegisterPropertyWrapper> implements ActionRegion {

    private final HashMap<Register, RegisterPropertyWrapper> registers;

    @SuppressWarnings("unchecked")
    public RegistersTable(MIPSSimulation<?> simulation, Set<Register> registers, boolean useDecimals) {
        this.registers = new HashMap<>();
        getStyleClass().add("table-view-horizontal-fit");
        setEditable(true);
        setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);

        TableColumn<RegisterPropertyWrapper, Number> identifierColumn = new LanguageTableColumn<>(Messages.REGISTERS_ID);
        TableColumn<RegisterPropertyWrapper, String> nameColumn = new LanguageTableColumn<>(Messages.REGISTERS_NAME);
        TableColumn<RegisterPropertyWrapper, String> valueColumn = new LanguageTableColumn<>(Messages.REGISTERS_VALUE);
        TableColumn<RegisterPropertyWrapper, String> hexColumn = new LanguageTableColumn<>(Messages.REGISTERS_HEX);
        getColumns().setAll(identifierColumn, nameColumn, valueColumn, hexColumn);

        identifierColumn.setCellValueFactory(p -> p.getValue().identifierProperty());
        nameColumn.setCellValueFactory(p -> p.getValue().nameProperty());

        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        valueColumn.setCellValueFactory(p -> p.getValue().valueProperty());

        hexColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        hexColumn.setCellValueFactory(p -> p.getValue().hexProperty());

        identifierColumn.setEditable(false);
        nameColumn.setEditable(false);
        valueColumn.setEditable(true);
        hexColumn.setEditable(true);

        valueColumn.setOnEditCommit(t -> {
            if (!t.getRowValue().getRegister().isModifiable()) {
                t.getRowValue().valueProperty().setValue(t.getOldValue());
                return;
            }
            t.getRowValue().valueProperty().setValue(t.getNewValue());
        });
        hexColumn.setOnEditCommit(t -> {
            if (!t.getRowValue().getRegister().isModifiable()) {
                t.getRowValue().hexProperty().setValue(t.getOldValue());
                return;
            }
            t.getRowValue().hexProperty().setValue(t.getNewValue());
        });

        registers.stream()
                .sorted((Comparator.comparingInt(Register::getIdentifier)))
                .forEach(target -> getItems().add(new RegisterPropertyWrapper(target, useDecimals)));


        for (RegisterPropertyWrapper item : getItems()) {
            this.registers.put(item.getRegister(), item);
        }

        simulation.registerListeners(this, true);
        if (!simulation.isRunning()) {
            simulation.getRegisters().registerListeners(this, true);
        }
    }

    @Override
    public boolean supportsActionRegion(String region) {
        return RegionTags.MIPS_SIMULATION.equals(region);
    }

    @Listener
    private void onSimulationStart(SimulationStartEvent event) {
        if (event.getSimulation() instanceof MIPSSimulation<?> simulation) {
            simulation.getRegisters().unregisterListeners(this);
        }
    }

    @Listener
    private void onSimulationStop(SimulationStopEvent event) {
        if (event.getSimulation() instanceof MIPSSimulation<?> simulation) {
            simulation.getRegisters().registerListeners(this, true);
            registers.values().forEach(RegisterPropertyWrapper::updateRegister);
        }
    }

    @Listener
    private void onRegisterValueChange(RegisterChangeValueEvent.After event) {
        RegisterPropertyWrapper wrapper = registers.get(event.getRegister());
        if (wrapper == null) return;
        int value = event.getNewValue();
        wrapper.updateRegister(value);
    }
}
