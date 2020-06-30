package net.jamsimulator.jams.gui.mips.simulator.instruction;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import net.jamsimulator.jams.gui.mips.simulator.instruction.singlecycle.SingleCycleInstructionsTable;
import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.memory.MIPS32Memory;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class InstructionsTable extends TableView<InstructionEntry> {

	public static Map<Architecture, BiFunction<Simulation<?>,
			Map<Integer, String>, InstructionsTable>> TABLES_PER_ARCHITECTURE = new HashMap<>();

	static {
		TABLES_PER_ARCHITECTURE.put(SingleCycleArchitecture.INSTANCE,
				(s, o) -> new SingleCycleInstructionsTable((Simulation<? extends SingleCycleArchitecture>) s, o));
	}

	public static void registerTableView(Architecture architecture,
										 BiFunction<Simulation<?>, Map<Integer, String>, InstructionsTable> builder) {
		TABLES_PER_ARCHITECTURE.put(architecture, builder);
	}

	public static InstructionsTable createTable(Architecture architecture, Simulation<?> simulation, Map<Integer, String> originals) {
		BiFunction<Simulation<?>, Map<Integer, String>, InstructionsTable> builder =
				TABLES_PER_ARCHITECTURE.get(architecture);
		if (builder == null) return new InstructionsTable(simulation, originals);
		return builder.apply(simulation, originals);
	}

	public InstructionsTable(Simulation<?> simulation, Map<Integer, String> originals) {
		setEditable(true);
		setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);

		TableColumn<InstructionEntry, String> addressColumn = new TableColumn<>("Address");
		TableColumn<InstructionEntry, String> codeColumn = new TableColumn<>("Code");
		TableColumn<InstructionEntry, String> instructionColumn = new TableColumn<>("Instruction");
		TableColumn<InstructionEntry, String> originalColumn = new TableColumn<>("Original");

		addressColumn.setCellValueFactory(p -> p.getValue().addressProperty());
		codeColumn.setCellValueFactory(p -> p.getValue().codeProperty());
		codeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		instructionColumn.setCellValueFactory(p -> p.getValue().instructionProperty());
		originalColumn.setCellValueFactory(p -> p.getValue().originalProperty());

		addressColumn.setEditable(false);
		codeColumn.setEditable(true);
		instructionColumn.setEditable(false);
		originalColumn.setEditable(false);

		setRowFactory(row -> new TableRow<InstructionEntry>() {
			@Override
			protected void updateItem(InstructionEntry item, boolean empty) {
				super.updateItem(item, empty);
				onRowUpdate(this);
			}
		});

		codeColumn.setOnEditCommit(t -> t.getRowValue().codeProperty().setValue(t.getNewValue()));


		getColumns().setAll(addressColumn, codeColumn, instructionColumn, originalColumn);

		int current = MIPS32Memory.TEXT;
		int end = simulation.getInstructionStackBottom();
		while (current <= end) {
			getItems().add(new InstructionEntry(simulation, current, originals.getOrDefault(current, "")));
			current += 4;
		}

		getSelectionModel().select(2, codeColumn);

	}

	public void onRowUpdate(TableRow<InstructionEntry> row) {

	}


}
