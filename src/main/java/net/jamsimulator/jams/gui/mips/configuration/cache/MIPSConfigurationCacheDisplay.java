package net.jamsimulator.jams.gui.mips.configuration.cache;

import javafx.beans.property.Property;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.configuration.ConfigurationRegionDisplay;
import net.jamsimulator.jams.gui.util.propertyeditor.BooleanPropertyEditor;
import net.jamsimulator.jams.gui.util.propertyeditor.Pow2PropertyEditor;
import net.jamsimulator.jams.gui.util.propertyeditor.PropertyEditor;
import net.jamsimulator.jams.gui.util.propertyeditor.PropertyEditors;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.mips.memory.cache.CacheBuilder;

public class MIPSConfigurationCacheDisplay extends VBox {

	private final CacheBuilder<?> builder;
	private Label sizeLabel;

	public MIPSConfigurationCacheDisplay(CacheBuilder<?> builder) {
		this.builder = builder;
		setSpacing(7);
		setPadding(new Insets(5));
		populate();
	}

	private void populate() {
		getChildren().add(new ConfigurationRegionDisplay(Messages.SIMULATION_CONFIGURATION_CACHES_TAB_PROPERTIES));
		getChildren().add(new Region());

		for (Property<?> property : builder.getProperties()) {

			var hBox = new HBox();
			hBox.setSpacing(5);

			var languageNode = builder.getLanguageNode() + "_PROPERTY_" + property.getName();
			var label = new LanguageLabel(languageNode);

			PropertyEditor<?> editor;

			if (property.getValue() instanceof Integer) {
				editor = new Pow2PropertyEditor((Property<Integer>) property, 15);
			} else {
				editor = PropertyEditors.getEditor(property).orElse(null);
				if (editor == null) continue;
			}

			if (editor instanceof BooleanPropertyEditor) {
				var bEditor = (BooleanPropertyEditor) editor;
				hBox.getChildren().addAll(editor.thisInstanceAsNode(), label);
				label.setOnMouseClicked(event -> bEditor.setSelected(!bEditor.isSelected()));
			} else {
				hBox.getChildren().addAll(label, editor.thisInstanceAsNode());
			}

			editor.addListener(v -> refreshSize());
			getChildren().add(hBox);
		}

		getChildren().addAll(new Group(), new ConfigurationRegionDisplay(Messages.SIMULATION_CONFIGURATION_CACHES_TAB_INFO));

		var sizeBox = new HBox();
		sizeBox.setSpacing(5);
		sizeBox.getChildren().add(new LanguageLabel(Messages.SIMULATION_CONFIGURATION_CACHES_TAB_SIZE));
		sizeBox.getChildren().add(sizeLabel = new Label(builder.getSizeInBytes() + " B"));
		getChildren().add(sizeBox);
	}

	private void refreshSize() {
		sizeLabel.setText(builder.getSizeInBytes() + "B");
	}

}
