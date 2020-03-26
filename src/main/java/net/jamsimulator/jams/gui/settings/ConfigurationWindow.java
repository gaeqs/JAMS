package net.jamsimulator.jams.gui.settings;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.settings.explorer.ConfigurationWindowExplorer;
import net.jamsimulator.jams.gui.settings.explorer.ConfigurationWindowSection;

public class ConfigurationWindow extends SplitPane {

	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;

	private Stage stage;

	private Configuration configuration;
	private Configuration meta;

	private ConfigurationWindowExplorer explorer;
	private VBox sectionDisplay;

	public ConfigurationWindow(Configuration configuration, Configuration meta) {
		this.stage = null;
		this.configuration = configuration;
		this.meta = meta;

		this.explorer = new ConfigurationWindowExplorer(this);
		this.sectionDisplay = new VBox();
		this.sectionDisplay.getStyleClass().add("configuration-window-display");
		init();
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public Configuration getMeta() {
		return meta;
	}

	private void init() {
		getItems().add(explorer);
		getItems().add(sectionDisplay);
	}

	public void display(ConfigurationWindowSection section) {
		sectionDisplay.getChildren().clear();
		section.getNodes().forEach(sectionDisplay.getChildren()::add);
	}

	public void open() {
		if (stage == null) {
			stage = new Stage();
			Scene scene = new Scene(this);
			stage.initOwner(JamsApplication.getStage());
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setScene(scene);
			stage.setWidth(WIDTH);
			stage.setHeight(HEIGHT);
			scene.getStylesheets().add("gui/style/dark_style.css");

			Stage main = JamsApplication.getStage();

			stage.setX(main.getX() + main.getWidth() / 2 - (WIDTH >> 1));
			stage.setY(main.getY() + main.getHeight() / 2 - (HEIGHT >> 1));
		}
		stage.show();
		Platform.runLater(() -> setDividerPosition(0, 0.3));
	}
}
