package net.jamsimulator.jams.gui.mips.configuration;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.util.ArchitectureComboBox;
import net.jamsimulator.jams.gui.util.MemoryBuilderComboBox;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.project.mips.MipsProjectData;
import net.jamsimulator.jams.project.mips.MIPSSimulationConfiguration;

public class ConfigurationGeneralTab extends VBox {

	private final ConfigurationsWindow window;
	private final MipsProjectData data;
	private final MIPSSimulationConfiguration configuration;

	private HBox enableUndoHBox;

	public ConfigurationGeneralTab(ConfigurationsWindow window, MipsProjectData data, MIPSSimulationConfiguration configuration) {
		this.window = window;
		this.data = data;
		this.configuration = configuration;

		setSpacing(3);

		Region region = new Region();
		region.setPrefHeight(10);
		getChildren().add(region);

		generateNameHBox();
		generateArchitectureHBox();
		generateMemoryHBox();

		getChildren().add(new Separator());

		generateCallEventsCheckBox();
		generateEnableUndoCheckBox();
	}

	private void generateNameHBox() {
		HBox nameHBox = new HBox();
		nameHBox.setSpacing(5);
		nameHBox.setAlignment(Pos.CENTER_LEFT);
		nameHBox.getChildren().add(new Region());

		Label label = new LanguageLabel(Messages.SIMULATION_CONFIGURATION_NAME);
		nameHBox.getChildren().add(new Group(label));


		TextField nameField = new TextField(configuration.getName());

		EventHandler<ActionEvent> handler = event -> {
			if (data.getConfigurations().stream().anyMatch(target -> target.getName().equals(nameField.getText()))) {
				nameField.setText(configuration.getName());
				return;
			}
			configuration.setName(nameField.getText());
			window.getList().refreshName(configuration);
		};

		nameField.setOnAction(handler);
		nameField.focusedProperty().addListener((obs, old, val) -> {
			if (!val) {
				handler.handle(null);
			}
		});

		nameField.prefWidthProperty().bind(widthProperty().subtract(label.widthProperty()).subtract(30));

		nameHBox.getChildren().add(nameField);
		getChildren().add(nameHBox);
	}

	private void generateArchitectureHBox() {
		HBox architectureHBox = new HBox();
		architectureHBox.setSpacing(5);
		architectureHBox.setAlignment(Pos.CENTER_LEFT);
		architectureHBox.getChildren().add(new Region());
		Label label = new LanguageLabel(Messages.SIMULATION_CONFIGURATION_ARCHITECTURE);
		architectureHBox.getChildren().add(new Group(label));

		ArchitectureComboBox architectureBox = new ArchitectureComboBox(configuration.getArchitecture());
		architectureBox.prefWidthProperty().bind(widthProperty().subtract(label.widthProperty()).subtract(30));

		architectureBox.setOnAction(event ->
				configuration.setArchitecture(architectureBox.getSelectionModel().getSelectedItem()));

		architectureHBox.getChildren().add(architectureBox);
		getChildren().add(architectureHBox);
	}

	private void generateMemoryHBox() {
		HBox memoryHBox = new HBox();
		memoryHBox.setSpacing(5);
		memoryHBox.setAlignment(Pos.CENTER_LEFT);
		memoryHBox.getChildren().add(new Region());
		Label label = new LanguageLabel(Messages.SIMULATION_CONFIGURATION_MEMORY);
		memoryHBox.getChildren().add(new Group(label));

		MemoryBuilderComboBox memoryBox = new MemoryBuilderComboBox(configuration.getMemoryBuilder());
		memoryBox.prefWidthProperty().bind(widthProperty().subtract(label.widthProperty()).subtract(30));

		memoryBox.setOnAction(event -> {
			configuration.setMemoryBuilder(memoryBox.getSelectionModel().getSelectedItem());
		});


		memoryHBox.getChildren().add(memoryBox);
		getChildren().add(memoryHBox);
	}

	private void generateCallEventsCheckBox() {
		HBox box = new HBox();
		box.setSpacing(5);

		CheckBox checkBox = new CheckBox();
		checkBox.setSelected(configuration.isCallEvents());
		checkBox.selectedProperty().addListener((obs, old, val) -> {
			configuration.setCallEvents(val);
			enableUndoHBox.setDisable(!val);
		});

		Label label = new LanguageLabel(Messages.SIMULATION_CONFIGURATION_CALL_EVENTS);
		label.setOnMouseClicked(event -> checkBox.setSelected(!checkBox.isSelected()));

		box.getChildren().addAll(new Region(), checkBox, label);
		getChildren().add(box);
	}

	private void generateEnableUndoCheckBox() {
		enableUndoHBox = new HBox();
		enableUndoHBox.setSpacing(5);
		enableUndoHBox.setDisable(!configuration.isCallEvents());

		CheckBox checkBox = new CheckBox();
		checkBox.setSelected(configuration.isUndoEnabled());
		checkBox.selectedProperty().addListener((obs, old, val) -> configuration.setUndoEnabled(val));

		Label label = new LanguageLabel(Messages.SIMULATION_CONFIGURATION_ENABLE_UNDO);
		label.setOnMouseClicked(event -> checkBox.setSelected(!checkBox.isSelected()));

		enableUndoHBox.getChildren().addAll(new Region(), checkBox, label);
		getChildren().add(enableUndoHBox);
	}

}
