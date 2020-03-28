package net.jamsimulator.jams.gui.configuration.explorer.node;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;

import java.util.Optional;

public class ConfigurationWindowNode<E> extends HBox {

	protected Configuration configuration;
	protected String relativeNode;
	protected String languageNode;
	protected E defaultValue;


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
		Optional<E> optional = configuration.get(relativeNode);
		return optional.orElse(defaultValue);
	}

	public void setValue(E value) {
		saveValue(value);
	}

	protected void saveValue(E value) {
		configuration.set(relativeNode, value);
	}

	protected void init() {
		setAlignment(Pos.CENTER_LEFT);
		Label label = languageNode == null ? new Label(relativeNode) : new LanguageLabel(languageNode);
		getChildren().add(label);
	}
}
