package net.jamsimulator.jams.gui.mips.simulator.register;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.ActionRegion;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageTableColumn;
import net.jamsimulator.jams.mips.register.COP0Register;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.event.RegisterChangeValueEvent;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.event.SimulationStartEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationStopEvent;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

public class COP0RegistersTable extends TableView<COP0RegisterPropertyWrapper> implements ActionRegion {

	private final HashMap<Register, COP0RegisterPropertyWrapper> registers;

	public COP0RegistersTable(MIPSSimulation<?> simulation, Set<Register> registers, boolean useDecimals) {
		this.registers = new HashMap<>();
		getStyleClass().add("table-view-horizontal-fit");
		setEditable(true);
		setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);

		TableColumn<COP0RegisterPropertyWrapper, Number> identifierColumn = new LanguageTableColumn<>(Messages.REGISTERS_ID);
		TableColumn<COP0RegisterPropertyWrapper, Number> selectionColumn = new LanguageTableColumn<>(Messages.REGISTERS_SELECTION);
		TableColumn<COP0RegisterPropertyWrapper, String> nameColumn = new LanguageTableColumn<>(Messages.REGISTERS_NAME);
		TableColumn<COP0RegisterPropertyWrapper, String> valueColumn = new LanguageTableColumn<>(Messages.REGISTERS_VALUE);
		TableColumn<COP0RegisterPropertyWrapper, String> hexColumn = new LanguageTableColumn<>(Messages.REGISTERS_HEX);
		getColumns().setAll(identifierColumn, selectionColumn, nameColumn, valueColumn, hexColumn);

		identifierColumn.setCellValueFactory(p -> p.getValue().identifierProperty());
		selectionColumn.setCellValueFactory(p -> p.getValue().selectionProperty());
		nameColumn.setCellValueFactory(p -> p.getValue().nameProperty());

		valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		valueColumn.setCellValueFactory(p -> p.getValue().valueProperty());

		hexColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		hexColumn.setCellValueFactory(p -> p.getValue().hexProperty());

		identifierColumn.setEditable(false);
		selectionColumn.setEditable(false);
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
				.forEach(target -> getItems().add(new COP0RegisterPropertyWrapper((COP0Register) target, useDecimals)));


		for (COP0RegisterPropertyWrapper item : getItems()) {
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
		event.getSimulation().getRegisters().unregisterListeners(this);
	}

	@Listener
	private void onSimulationStop(SimulationStopEvent event) {
		event.getSimulation().getRegisters().registerListeners(this, true);
		registers.values().forEach(COP0RegisterPropertyWrapper::updateRegister);
	}

	@Listener
	private void onRegisterValueChange(RegisterChangeValueEvent.After event) {
		COP0RegisterPropertyWrapper wrapper = registers.get(event.getRegister());
		if (wrapper == null) return;
		int value = event.getNewValue();
		wrapper.updateRegister(value);
	}
}
