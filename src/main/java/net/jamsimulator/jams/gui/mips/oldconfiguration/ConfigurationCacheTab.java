package net.jamsimulator.jams.gui.mips.oldconfiguration;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.util.CacheBuilderComboBox;
import net.jamsimulator.jams.gui.util.propertyeditor.PropertyEditor;
import net.jamsimulator.jams.gui.util.propertyeditor.PropertyEditors;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.mips.memory.cache.CacheBuilder;
import net.jamsimulator.jams.mips.memory.cache.builder.DirectCacheBuilder;
import net.jamsimulator.jams.project.mips.MIPSSimulationConfiguration;
import net.jamsimulator.jams.utils.Spacer;

import java.util.LinkedList;

public class ConfigurationCacheTab extends VBox {

	private final MIPSSimulationConfiguration configuration;
	private Button addButton;

	public ConfigurationCacheTab(MIPSSimulationConfiguration configuration) {
		this.configuration = configuration;
		init();
	}

	public MIPSSimulationConfiguration getConfiguration() {
		return configuration;
	}

	private void init() {
		if (configuration == null) return;

		setAlignment(Pos.TOP_CENTER);
		setSpacing(3);

		addButton = new Button("+");
		addButton.getStyleClass().add("bold-button");
		addButton.setOnAction(event -> {
			getChildren().add(getChildren().indexOf(addButton), createEmptyCacheBox());
		});

		getChildren().add(new Region());
		addSlowly(new LinkedList<>(configuration.getCacheBuilders()));
	}

	private void addSlowly(LinkedList<CacheBuilder<?>> list) {
		if (list.isEmpty()) {
			getChildren().add(addButton);
			return;
		}
		Platform.runLater(() -> {
			CacheBuilder<?> entry = list.removeFirst();
			getChildren().add(new CacheBox(entry, this));
			addSlowly(list);
		});
	}

	private CacheBox createEmptyCacheBox() {
		CacheBuilder<?> builder = Jams.getCacheBuilderManager().get(DirectCacheBuilder.NAME).orElse(null);
		configuration.getCacheBuilders().add(builder);
		return new CacheBox(builder, this);
	}

	private static class CacheBox extends VBox {

		private CacheBuilderComboBox builderComboBox;
		private final ConfigurationCacheTab config;
		private final VBox propertiesBox;

		private CacheBuilder<?> current;

		public CacheBox(CacheBuilder<?> builder, ConfigurationCacheTab config) {
			this.config = config;
			this.current = builder;
			loadGeneral();

			propertiesBox = new VBox();
			propertiesBox.setSpacing(5);
			getChildren().add(propertiesBox);
			loadProperties();
		}

		private void loadGeneral() {
			HBox general = new HBox();
			general.setAlignment(Pos.CENTER_LEFT);
			general.setSpacing(5);

			builderComboBox = new CacheBuilderComboBox(current);
			Button removeButton = new Button("-");


			builderComboBox.prefWidthProperty().bind(general.widthProperty()
					.subtract(removeButton.widthProperty())
					.subtract(30));
			removeButton.getStyleClass().add("bold-button");

			general.getChildren().addAll(new Region(), builderComboBox, removeButton);
			getChildren().add(general);

			builderComboBox.setOnAction(event -> {
				if (builderComboBox.getSelectionModel().getSelectedItem() == null) return;
				CacheBuilder<?> target = builderComboBox.getSelectionModel().getSelectedItem().makeNewInstance();

				int index = config.getConfiguration().getCacheBuilders().indexOf(current);
				config.getConfiguration().getCacheBuilders().set(index, target);
				current = target;
				loadProperties();
			});

			removeButton.setOnAction(event -> {
				config.getConfiguration().getCacheBuilders().remove(current);
				config.getChildren().remove(this);
			});
		}

		private void loadProperties() {
			propertiesBox.getChildren().clear();
			for (Property<?> property : current.getProperties()) {
				PropertyEditor<?> editor = PropertyEditors.getEditor(property).orElse(null);
				if (editor == null) continue;

				HBox box = new HBox();
				Label name = new LanguageLabel(current.getLanguageNode() + "_PROPERTY_" + property.getName());

				box.getChildren().addAll(new Spacer(55, 0), editor.thisInstanceAsNode(), name);
				box.setSpacing(5);
				propertiesBox.getChildren().add(new Group(box));
			}

		}
	}
}
