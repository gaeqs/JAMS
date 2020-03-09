package net.jamsimulator.jams.gui.settings.explorer.node;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;

import java.util.Optional;

public class ConfigurationWindowNode<E> extends HBox {

	private Configuration configuration;
	private String relativeNode;
	private String languageNode;
	private E defaultValue;


	public ConfigurationWindowNode(Configuration configuration, String relativeNode,
								   String languageNode, E defaultValue) {
		getStyleClass().add("configuration-window-node");
		this.configuration = configuration;
		this.relativeNode = relativeNode;
		this.languageNode = languageNode;
		this.defaultValue = defaultValue;
		init();
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public String getRelativeNode() {
		return relativeNode;
	}

	public String getLanguageNode() {
		return languageNode;
	}

	public E getDefaultValue() {
		return defaultValue;
	}

	public E getValue() {
		try {
			Optional<E> optional = configuration.get(relativeNode);
			return optional.orElse(defaultValue);
		} catch (ClassCastException ex) {
			return defaultValue;
		}
	}

	public void setValue(E value) {
		configuration.set(relativeNode, value);
	}

	protected void init() {
		Label label = languageNode == null ? new Label(relativeNode) : new LanguageLabel(languageNode);
		getChildren().add(label);
	}
}
