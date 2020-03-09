package net.jamsimulator.jams.gui.settings.explorer.node;

import javafx.scene.control.CheckBox;
import net.jamsimulator.jams.configuration.Configuration;

public class ConfigurationWindowNodeBoolean extends ConfigurationWindowNode<Boolean> {

	public ConfigurationWindowNodeBoolean(Configuration configuration, String relativeNode,
										  String languageNode, Boolean defaultValue) {
		super(configuration, relativeNode, languageNode, defaultValue);
	}


	@Override
	protected void init() {
		CheckBox box = new CheckBox();
		box.setSelected(getValue());
		getChildren().add(box);
		super.init();

		box.selectedProperty().addListener((obs, old, val) -> setValue(val));
	}
}
