package net.jamsimulator.jams.gui.mips.simulator.register;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import net.jamsimulator.jams.mips.register.Register;

import java.util.Comparator;
import java.util.Set;

public class RegistersTable extends TableView<RegisterPropertyWrapper> {

	public RegistersTable(Set<Register> registers, boolean useDecimals) {
		getStyleClass().add("table-view-horizontal-fit");
		setEditable(true);
		setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);

		TableColumn<RegisterPropertyWrapper, Number> identifierColumn = new TableColumn<>("Id");
		TableColumn<RegisterPropertyWrapper, String> nameColumn = new TableColumn<>("Name");
		TableColumn<RegisterPropertyWrapper, String> valueColumn = new TableColumn<>("Value");
		TableColumn<RegisterPropertyWrapper, String> hexColumn = new TableColumn<>("Hex");
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

		valueColumn.setOnEditCommit(t -> t.getRowValue().valueProperty().setValue(t.getNewValue()));
		hexColumn.setOnEditCommit(t -> t.getRowValue().hexProperty().setValue(t.getNewValue()));

		registers.stream()
				.sorted((Comparator.comparingInt(Register::getIdentifier)))
				.forEach(target -> getItems().add(new RegisterPropertyWrapper(target, useDecimals)));
	}
}
