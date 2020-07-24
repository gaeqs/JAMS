package net.jamsimulator.jams.gui.mips.simulator.memory;

import javafx.scene.control.TableCell;

public class MemoryTableCell extends TableCell<MemoryEntry, String> {

	@Override
	protected void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		if (empty || getTableRow() == null) {
			setText(null);
			setGraphic(null);
		} else {
			MemoryEntry entry = getTableRow().getItem();
			if (entry == null) return;

			int offset = 0;
			switch (getTableColumn().getId()) {
				case "0":
					break;
				case "4":
					offset = 4;
					break;
				case "8":
					offset = 8;
					break;
				case "C":
					offset = 12;
					break;
			}

			if (!entry.getRepresentation().isColor()) {
				setText(entry.getRepresentation().represent(entry.getSimulation().getMemory(), entry.getAddress() + offset));
				setStyle("-fx-background-color: transparent");
				return;
			}

			setText("");
			setStyle("-fx-background-color: " + entry.getRepresentation().represent(entry.getSimulation().getMemory(), entry.getAddress() + offset));
		}
	}
}
