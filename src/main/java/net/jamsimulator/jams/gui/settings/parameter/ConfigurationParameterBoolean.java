package net.jamsimulator.jams.gui.settings.parameter;

import javafx.scene.control.CheckBox;
import net.jamsimulator.jams.gui.settings.ConfigurationWindowSection;

import java.util.Optional;

public class ConfigurationParameterBoolean extends ConfigurationParameter<Boolean> {


	public ConfigurationParameterBoolean(ConfigurationWindowSection section, String node, String languageNode, boolean defaultValue) {
		super(section, node, languageNode, defaultValue);
	}

	@Override
	public Boolean getValue() {
		Optional<Object> optional = section.getConfigurationWindow().getConfiguration().get(getFullNode());
		if (!optional.isPresent() || !(optional.get() instanceof Boolean)) return defaultValue;
		return (boolean) optional.get();
	}

	@Override
	public void setValue(Boolean value) {
		section.getConfigurationWindow().getConfiguration().set(getFullNode(), value);
	}

	@Override
	protected void init() {
		super.init();

		CheckBox box = new CheckBox();
		getChildren().add(box);

		box.selectedProperty().addListener((obs, old, val) -> setValue(val));
	}
}
