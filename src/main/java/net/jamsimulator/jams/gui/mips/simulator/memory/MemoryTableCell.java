package net.jamsimulator.jams.gui.mips.simulator.memory;

import javafx.application.Platform;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import net.jamsimulator.jams.utils.NumericUtils;
import net.jamsimulator.jams.utils.StringUtils;

public class MemoryTableCell extends TextFieldTableCell<MemoryEntry, String> {

	private final int offset;

	public MemoryTableCell(int offset) {
		super();

		this.offset = offset;

		setConverter(new StringConverter<String>() {
			@Override
			public String toString(String object) {
				if(getTableRow() == null) return object;
				MemoryEntry entry = getTableRow().getItem();
				if (entry == null) return object;
				int value = entry.getSimulation().getMemory()
						.getWord(entry.getAddress() + offset, false, true);
				return "0x" + StringUtils.addZeros(Integer.toHexString(value), 8);
			}

			@Override
			public String fromString(String string) {
				MemoryEntry entry = getTableRow().getItem();
				if (entry == null) return null;

				try {
					int value = NumericUtils.decodeInteger(string);
					entry.getSimulation().getMemory().setWord(entry.getAddress() + offset, value);
					return string;
				} catch (NumberFormatException ex) {

					return getTableRow().getItem().getRepresentation()
							.represent(entry.getSimulation().getMemory(), entry.getAddress() + offset);
				}
			}
		});
	}

	@Override
	public void cancelEdit() {
		super.cancelEdit();
		MemoryEntry entry = getTableRow().getItem();
		if (entry == null) return;
		updateItem(entry.getRepresentation()
				.represent(entry.getSimulation().getMemory(), entry.getAddress() + offset), false);
	}

	@Override
	public void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		if (empty || getTableRow() == null) {
			setText(null);
			setGraphic(null);
		} else {
			MemoryEntry entry = getTableRow().getItem();
			if (entry == null) return;

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
