package net.jamsimulator.jams.gui.settings.parameter;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.explorer.ExplorerBasicElement;
import net.jamsimulator.jams.gui.settings.ConfigurationWindowSection;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;

public abstract class ConfigurationParameter<E> extends ExplorerBasicElement {

	protected ConfigurationWindowSection section;

	protected String node;
	protected String languageNode;

	protected E defaultValue;

	public ConfigurationParameter(ConfigurationWindowSection section, String node, String languageNode, E defaultValue) {
		this.section = section;
		this.node = node;
		this.languageNode = languageNode;
		this.defaultValue = defaultValue;
	}

	public ConfigurationWindowSection getSection() {
		return section;
	}

	public String getNode() {
		return node;
	}

	public String getFullNode() {
		return section.getFullNode() + "." + node;
	}

	public String getLanguageNode() {
		return languageNode;
	}

	public E getDefaultValue() {
		return defaultValue;
	}

	public abstract E getValue();

	public abstract void setValue(E value);


	protected void init() {
		Label label = languageNode == null ? new Label(node) : new LanguageLabel(languageNode);
		getChildren().add(label);
	}

}
