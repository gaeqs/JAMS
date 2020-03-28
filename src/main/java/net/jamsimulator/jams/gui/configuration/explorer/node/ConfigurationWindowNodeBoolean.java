package net.jamsimulator.jams.gui.configuration.explorer.node;

import javafx.scene.control.CheckBox;
import net.jamsimulator.jams.configuration.Configuration;

public class ConfigurationWindowNodeBoolean extends ConfigurationWindowNode<Boolean> {

	protected CheckBox box;

	public ConfigurationWindowNodeBoolean(Configuration configuration, String relativeNode,
										  String languageNode, Boolean defaultValue) {
		super(configuration, relativeNode, languageNode, defaultValue);
	}


	@Override
	protected void init() {
		super.init();
		box = new CheckBox();
		box.setSelected(getValue());
		getChildren().add(box);

		box.selectedProperty().addListener((obs, old, val) -> saveValue(val));
	}

	@Override
	public void setValue(Boolean value) {
		saveValue(value);
		box.setSelected(value);
	}

	@Override
	protected void saveValue(Boolean value) {
		super.saveValue(value);
	}

	static class Builder implements ConfigurationWindowNodeBuilder<Boolean> {

		@Override
		public ConfigurationWindowNode<Boolean> create(Configuration configuration, String relativeNode, String languageNode) {
			return new ConfigurationWindowNodeBoolean(configuration, relativeNode, languageNode, false);
		}
	}
}
