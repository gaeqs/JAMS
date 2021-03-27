package net.jamsimulator.jams.gui.mips.simulator.lab;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.mips.interrupt.InterruptCause;
import net.jamsimulator.jams.mips.interrupt.MIPSInterruptException;
import net.jamsimulator.jams.mips.simulation.Simulation;

public class LabInterruptGenerator extends VBox {

	public LabInterruptGenerator(Simulation<?> simulation) {
		setAlignment(Pos.CENTER);

		var comboBox = new ComboBox<InterruptCause>();
		comboBox.getItems().addAll(InterruptCause.values());
		comboBox.getSelectionModel().selectFirst();
		getChildren().add(comboBox);

		var button = new Button("Interrupt");
		button.setOnAction(event -> simulation.addInterruptToQueue(
				new MIPSInterruptException(comboBox.getSelectionModel().getSelectedItem())));
		getChildren().add(button);
	}

}
