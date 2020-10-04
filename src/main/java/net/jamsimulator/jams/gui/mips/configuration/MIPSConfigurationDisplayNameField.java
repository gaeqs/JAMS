package net.jamsimulator.jams.gui.mips.configuration;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.project.mips.MIPSSimulationConfiguration;

public class MIPSConfigurationDisplayNameField extends HBox {

	public MIPSConfigurationDisplayNameField(MIPSConfigurationWindow window, MIPSSimulationConfiguration configuration) {
		getStyleClass().add("mips-configurations-window-name-field");
		var data = window.getProjectData();
		var label = new LanguageLabel(Messages.SIMULATION_CONFIGURATION_NAME);
		var field = new TextField(configuration.getName());

		EventHandler<ActionEvent> handler = event -> {
			if (data.getConfigurations().stream().anyMatch(target -> target.getName().equals(field.getText()))) {
				field.setText(configuration.getName());
				return;
			}
			configuration.setName(field.getText());
			window.getList().getContents().refreshName(configuration);
		};

		field.setOnAction(handler);
		field.focusedProperty().addListener((obs, old, val) -> {
			if (!val) handler.handle(null);
		});

		field.prefWidthProperty().bind(widthProperty().subtract(label.widthProperty()).subtract(30));

		getChildren().addAll(label, field);
	}

}
