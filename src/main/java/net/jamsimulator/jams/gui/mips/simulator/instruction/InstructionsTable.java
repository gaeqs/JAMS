package net.jamsimulator.jams.gui.mips.simulator.instruction;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import net.jamsimulator.jams.gui.mips.simulator.instruction.singlecycle.SingleCycleInstructionsTable;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageTableColumn;
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

	protected Simulation<?> simulation;

	public InstructionsTable(Simulation<?> simulation, Map<Integer, String> originals) {
		this.simulation = simulation;
		getStyleClass().add("table-view-horizontal-fit");
		setEditable(true);
		setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);

		TableColumn<InstructionEntry, Boolean> breakPointColumn = new LanguageTableColumn<>(Messages.INSTRUCTIONS_BREAKPOINT);
		TableColumn<InstructionEntry, String> addressColumn = new LanguageTableColumn<>(Messages.INSTRUCTIONS_ADDRESS);
		TableColumn<InstructionEntry, String> codeColumn = new LanguageTableColumn<>(Messages.INSTRUCTIONS_CODE);
		TableColumn<InstructionEntry, String> instructionColumn = new LanguageTableColumn<>(Messages.INSTRUCTIONS_INSTRUCTION);
		TableColumn<InstructionEntry, String> originalColumn = new LanguageTableColumn<>(Messages.INSTRUCTIONS_ORIGINAL);

		breakPointColumn.setSortable(false);
		addressColumn.setSortable(false);
		codeColumn.setSortable(false);
		instructionColumn.setSortable(false);
		originalColumn.setSortable(false);


		breakPointColumn.setCellValueFactory(p -> p.getValue().breakpointProperty());
		breakPointColumn.setCellFactory(param -> new CheckBoxTableCell<>());

		addressColumn.setCellValueFactory(p -> p.getValue().addressProperty());
		codeColumn.setCellValueFactory(p -> p.getValue().codeProperty());
		codeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		instructionColumn.setCellValueFactory(p -> p.getValue().instructionProperty());
		originalColumn.setCellValueFactory(p -> p.getValue().originalProperty());

		breakPointColumn.setEditable(true);
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


		getColumns().setAll(breakPointColumn, addressColumn, codeColumn, instructionColumn, originalColumn);

		getVisibleLeafColumn(0).setMinWidth(40);
		getVisibleLeafColumn(0).setMaxWidth(40);


		int current = MIPS32Memory.TEXT;
		int end = simulation.getInstructionStackBottom();
		while (current <= end) {
			getItems().add(new InstructionEntry(simulation, current, originals.getOrDefault(current, "")));
			current += 4;
		}

		getSelectionModel().select(2, codeColumn);

	}

	public Simulation<?> getSimulation() {
		return simulation;
	}

	public void onRowUpdate(TableRow<InstructionEntry> row) {

	}


}
