package net.jamsimulator.jams.gui.mips.sidebar;

import javafx.beans.property.Property;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.util.SyscallBuilderComboBox;
import net.jamsimulator.jams.gui.util.propertyeditor.PropertyEditor;
import net.jamsimulator.jams.gui.util.propertyeditor.PropertyEditors;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;
import net.jamsimulator.jams.mips.syscall.defaults.SyscallExecutionRunExceptionHandler;
import net.jamsimulator.jams.project.mips.MipsSimulationConfiguration;
import net.jamsimulator.jams.utils.NumericUtils;
import net.jamsimulator.jams.utils.Spacer;

import java.util.Map;

public class SimulationSyscallsConfiguration extends VBox {

	private MipsSimulationConfiguration configuration;
	private Button addButton;

	public SimulationSyscallsConfiguration(MipsSimulationConfiguration configuration) {
		this.configuration = configuration;
		init();
	}

	public MipsSimulationConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(MipsSimulationConfiguration configuration) {
		this.configuration = configuration;
		getChildren().clear();
		init();
	}

	private void init() {
		if (configuration == null) return;
		setAlignment(Pos.TOP_CENTER);
		setSpacing(3);
		configuration.getSyscallExecutionBuilders().forEach((key, builder) ->
				getChildren().add(new SyscallBox(key, builder, this)));

		addButton = new Button("+");
		addButton.getStyleClass().add("bold-button");
		addButton.setOnAction(event -> getChildren().add(getChildren().indexOf(addButton), createEmptySyscallBox()));
		getChildren().add(addButton);
	}

	private SyscallBox createEmptySyscallBox() {
		Map<Integer, SyscallExecutionBuilder<?>> builders = configuration.getSyscallExecutionBuilders();
		int id = 1;
		while (builders.containsKey(id)) {
			id++;
		}

		SyscallExecutionBuilder<?> builder = Jams.getSyscallExecutionBuilderManager()
				.get(SyscallExecutionRunExceptionHandler.NAME).orElse(null);

		configuration.getSyscallExecutionBuilders().put(id, builder);
		return new SyscallBox(id, builder, this);
	}


	private static class SyscallBox extends VBox {

		private int key;

		private TextField keyTextField;
		private SyscallBuilderComboBox builderComboBox;
		private final SimulationSyscallsConfiguration config;
		private final VBox propertiesBox;

		private String oldText;

		public SyscallBox(int key, SyscallExecutionBuilder<?> builder, SimulationSyscallsConfiguration config) {
			this.key = key;
			this.config = config;
			loadGeneral(key, builder);

			propertiesBox = new VBox();
			propertiesBox.setSpacing(5);
			getChildren().add(propertiesBox);
			loadProperties(builder);
		}

		public int getKey() {
			return key;
		}

		public boolean setKey(int key) {
			Map<Integer, SyscallExecutionBuilder<?>> builders = config.getConfiguration().getSyscallExecutionBuilders();
			if (key == this.key || builders.containsKey(key)) {
				keyTextField.setText(oldText);
				return false;
			}

			builders.remove(this.key);
			builders.put(key, builderComboBox.getSelectionModel().getSelectedItem());

			this.key = key;

			keyTextField.setText(String.valueOf(key));
			return true;
		}

		private void loadGeneral(int key, SyscallExecutionBuilder<?> builder) {
			HBox general = new HBox();
			general.setAlignment(Pos.CENTER_LEFT);
			general.setSpacing(5);

			keyTextField = new TextField(String.valueOf(key));
			builderComboBox = new SyscallBuilderComboBox(builder);
			Button removeButton = new Button("-");


			keyTextField.setMaxWidth(50);
			builderComboBox.prefWidthProperty().bind(general.widthProperty()
					.subtract(keyTextField.widthProperty())
					.subtract(removeButton.widthProperty())
					.subtract(30));
			removeButton.getStyleClass().add("bold-button");

			general.getChildren().addAll(new Region(), keyTextField, builderComboBox, removeButton);
			getChildren().add(general);

			oldText = keyTextField.getText();

			Runnable keyTextTrigger = () -> {
				if (oldText.equals(keyTextField.getText())) return;

				try {
					int newKey = NumericUtils.decodeInteger(keyTextField.getText());
					if (setKey(newKey)) {
						oldText = keyTextField.getText();
					}
				} catch (NumberFormatException ignore) {
					keyTextField.setText(oldText);
				}
			};

			keyTextField.setOnAction(event -> keyTextTrigger.run());
			keyTextField.focusedProperty().addListener((obs, old, val) -> {
				if (!val) keyTextTrigger.run();
			});

			builderComboBox.setOnAction(event -> {
				if (builderComboBox.getSelectionModel().getSelectedItem() == null) return;
				SyscallExecutionBuilder<?> target = builderComboBox.getSelectionModel().getSelectedItem().makeNewInstance();
				config.getConfiguration().getSyscallExecutionBuilders().put(key, target);
				loadProperties(target);
			});

			removeButton.setOnAction(event -> {
				config.getConfiguration().getSyscallExecutionBuilders().remove(key);
				config.getChildren().remove(this);
			});
		}

		private void loadProperties(SyscallExecutionBuilder<?> builder) {
			propertiesBox.getChildren().clear();
			for (Property<?> property : builder.getProperties()) {
				PropertyEditor<?> editor = PropertyEditors.getEditor(property).orElse(null);
				if (editor == null) continue;

				HBox box = new HBox();
				Label name = new LanguageLabel(builder.getLanguageNode() + "_PROPERTY_" + property.getName());

				box.getChildren().addAll(new Spacer(55, 0), editor.thisInstanceAsNode(), name);
				box.setSpacing(5);
				propertiesBox.getChildren().add(new Group(box));
			}

		}
	}
}
