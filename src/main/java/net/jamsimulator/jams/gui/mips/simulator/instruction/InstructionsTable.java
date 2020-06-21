package net.jamsimulator.jams.gui.mips.simulator.instruction;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import net.jamsimulator.jams.mips.memory.MIPS32Memory;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.Map;

public class InstructionsTable extends TableView<InstructionEntry> {

	public InstructionsTable(Simulation<?> simulation, Map<Integer, String> originals) {
		setEditable(true);
		TableColumn<InstructionEntry, String> addressColumn = new TableColumn<>("Address");
		TableColumn<InstructionEntry, String> codeColumn = new TableColumn<>("Code");
		TableColumn<InstructionEntry, String> instructionColumn = new TableColumn<>("Instruction");
		TableColumn<InstructionEntry, String> originalColumn = new TableColumn<>("Original");
		getColumns().setAll(addressColumn, codeColumn, instructionColumn, originalColumn);

		addressColumn.setCellValueFactory(p -> p.getValue().addressProperty());
		codeColumn.setCellValueFactory(p -> p.getValue().codeProperty());
		codeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		instructionColumn.setCellValueFactory(p -> p.getValue().instructionProperty());
		originalColumn.setCellValueFactory(p -> p.getValue().originalProperty());

		addressColumn.setEditable(false);
		codeColumn.setEditable(true);
		instructionColumn.setEditable(false);
		originalColumn.setEditable(false);

		codeColumn.setOnEditCommit(t -> t.getRowValue().codeProperty().setValue(t.getNewValue()));


		int current = MIPS32Memory.TEXT;
		int end = simulation.getInstructionStackBottom();
		while (current <= end) {
			getItems().add(new InstructionEntry(simulation, current, originals.getOrDefault(current, "")));
			current += 4;
		}
	}
}
