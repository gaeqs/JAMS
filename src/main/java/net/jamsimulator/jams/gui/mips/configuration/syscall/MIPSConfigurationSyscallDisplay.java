package net.jamsimulator.jams.gui.mips.configuration.syscall;

import javafx.beans.property.Property;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.configuration.ConfigurationRegionDisplay;
import net.jamsimulator.jams.gui.util.propertyeditor.BooleanPropertyEditor;
import net.jamsimulator.jams.gui.util.propertyeditor.PropertyEditors;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

public class MIPSConfigurationSyscallDisplay extends VBox {

	private final SyscallExecutionBuilder<?> builder;

	public MIPSConfigurationSyscallDisplay(SyscallExecutionBuilder<?> builder) {
		this.builder = builder;
		setSpacing(7);
		setPadding(new Insets(5));
		populate();
	}

	private void populate() {
		getChildren().add(new ConfigurationRegionDisplay(Messages.SIMULATION_CONFIGURATION_SYSTEM_CALLS_TAB_PROPERTIES));
		getChildren().add(new Region());

		for (Property<?> property : builder.getProperties()) {
			var hBox = new HBox();
			hBox.setSpacing(5);

			var languageNode = builder.getLanguageNode() + "_PROPERTY_" + property.getName();
			var label = new LanguageLabel(languageNode);

			var editor = PropertyEditors.getEditor(property).orElse(null);
			if (editor == null) continue;

			if (editor instanceof BooleanPropertyEditor){
				var bEditor = (BooleanPropertyEditor) editor;
				hBox.getChildren().addAll(editor.thisInstanceAsNode(), label);
				label.setOnMouseClicked(event -> bEditor.setSelected(!bEditor.isSelected()));
			} else{
				hBox.getChildren().addAll(label, editor.thisInstanceAsNode());
			}
			getChildren().add(hBox);
		}
	}

}
