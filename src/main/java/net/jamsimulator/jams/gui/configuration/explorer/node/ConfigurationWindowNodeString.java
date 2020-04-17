package net.jamsimulator.jams.gui.configuration.explorer.node;

import javafx.scene.control.TextField;
import net.jamsimulator.jams.configuration.Configuration;

import java.util.Optional;

public class ConfigurationWindowNodeString extends ConfigurationWindowNode<String> {

	protected TextField field;

	public ConfigurationWindowNodeString(Configuration configuration, String relativeNode,
										 String languageNode, String defaultValue) {
		super(configuration, relativeNode, languageNode, defaultValue);
	}


	@Override
	protected void init() {
		super.init();

		field = new TextField();
		field.setText(getValue());
		getChildren().add(field);

		field.setOnAction(target -> saveValue(field.getText()));
		field.focusedProperty().addListener((obs, old, val) -> {
			if (!val) saveValue(field.getText());
		});
	}

	@Override
	public String getValue() {
		Optional<String> optional = configuration.getString(relativeNode);
		return optional.orElse(defaultValue);
	}

	@Override
	public void setValue(String value) {
		saveValue(value);
		field.setText(value);
	}

	@Override
	protected void saveValue(String value) {
		super.saveValue(value);
	}

	static class Builder implements ConfigurationWindowNodeBuilder<String> {

		@Override
		public ConfigurationWindowNode<String> create(Configuration configuration, String relativeNode, String languageNode) {
			return new ConfigurationWindowNodeString(configuration, relativeNode, languageNode, "");
		}
	}
}
