package net.jamsimulator.jams.gui.mips.simulator.memory;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.mips.memory.MIPS32Memory;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.MemorySection;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.utils.AnchorUtils;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemoryPane extends AnchorPane {

	private final ComboBox<String> offsetSelection;
	private final ComboBox<String> representationSelection;
	private final MemoryTable table;
	private final HBox buttonsHBox;

	public MemoryPane(Simulation<?> simulation) {
		offsetSelection = new ComboBox<>();
		initOffsetComboBox(simulation.getMemory());
		representationSelection = new ComboBox<>();
		initRepresentationComboBox();

		table = new MemoryTable(simulation, 0, MemoryRepresentation.HEX);

		buttonsHBox = new HBox();
		initButtons();

		AnchorUtils.setAnchor(offsetSelection, 0, -1, 0, 0);
		AnchorUtils.setAnchor(representationSelection, 25, -1, 0, 0);
		AnchorUtils.setAnchor(table, 50, 30, 0, 0);
		AnchorUtils.setAnchor(buttonsHBox, -1, 0, 0, 0);

		getChildren().addAll(offsetSelection, representationSelection, table, buttonsHBox);
	}

	private void initOffsetComboBox(Memory memory) {
		List<String> list = new ArrayList<>();
		for (MemorySection section : memory.getMemorySections()) {
			list.add("0x" + StringUtils.addZeros(Integer.toHexString(section.getFirstAddress()), 8) + " - " + section.getName());
		}
		list.add("0x" + StringUtils.addZeros(Integer.toHexString(MIPS32Memory.HEAP), 8) + " - Heap");

		list.sort(String::compareTo);
		offsetSelection.getItems().addAll(list);
		offsetSelection.getSelectionModel().select(0);

		offsetSelection.setOnAction(event -> {
			String name = offsetSelection.getSelectionModel().getSelectedItem().substring(13);
			if (name.equals("Heap")) {
				table.setOffset(MIPS32Memory.HEAP);
			} else {
				Optional<MemorySection> section = memory.getMemorySection(name);
				section.ifPresent(memorySection -> table.setOffset(memorySection.getFirstAddress()));
			}
		});
	}

	private void initRepresentationComboBox() {
		representationSelection.getItems().addAll("Hex", "Integer", "Float", "Char");
		representationSelection.getSelectionModel().select(0);

		representationSelection.setOnAction(event -> {
			String name = representationSelection.getSelectionModel().getSelectedItem();
			switch (name) {
				case "Hex":
					table.setRepresentation(MemoryRepresentation.HEX);
					break;
				case "Integer":
					table.setRepresentation(MemoryRepresentation.INTEGER);
					break;
				case "Float":
					table.setRepresentation(MemoryRepresentation.FLOAT);
					break;
				case "Char":
					table.setRepresentation(MemoryRepresentation.CHAR);
					break;
			}
		});
	}

	private void initButtons() {
		buttonsHBox.setAlignment(Pos.CENTER);
		buttonsHBox.setFillHeight(true);
		Button previous = new Button("←");
		Button next = new Button("→");

		previous.getStyleClass().add("bold-button");
		next.getStyleClass().add("bold-button");
		previous.setPrefWidth(300);
		next.setPrefWidth(300);

		previous.setOnAction(event -> {
			int offset = table.getOffset() - MemoryTable.ENTRIES;
			table.setOffset(offset);
		});

		next.setOnAction(event -> {
			int offset = table.getOffset() + MemoryTable.ENTRIES;
			table.setOffset(offset);
		});

		buttonsHBox.getChildren().addAll(previous, next);
	}

}
