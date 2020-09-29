package net.jamsimulator.jams.gui.mips.simulator.label;

import javafx.scene.control.TableView;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageTableColumn;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.utils.StringUtils;

public class LabelTable extends TableView<LabelEntry> {

	public LabelTable(Simulation<?> simulation) {
		setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
		getStyleClass().add("table-view-horizontal-fit");

		var nameColumn = new LanguageTableColumn<LabelEntry, String>(Messages.REGISTERS_NAME);
		var addressColumn = new LanguageTableColumn<LabelEntry, String>(Messages.INSTRUCTIONS_ADDRESS);

		nameColumn.setEditable(false);
		addressColumn.setEditable(false);

		nameColumn.setCellValueFactory(p -> p.getValue().nameProperty());
		addressColumn.setCellValueFactory(p -> p.getValue().addressProperty());


		getColumns().addAll(nameColumn, addressColumn);

		simulation.getData().getLabels().forEach((label, address) ->
				getItems().add(new LabelEntry(label, "0x" +
						StringUtils.addZeros(Integer.toHexString(address), 8))));
	}

}
