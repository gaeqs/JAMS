package net.jamsimulator.jams.gui.configuration;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.configuration.RootConfiguration;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.configuration.explorer.ConfigurationWindowExplorer;
import net.jamsimulator.jams.gui.configuration.explorer.ConfigurationWindowSection;
import net.jamsimulator.jams.gui.theme.ThemedScene;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.event.SelectedLanguageChangeEvent;

import java.io.IOException;

public class ConfigurationWindow extends SplitPane {

	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;

	private Stage stage;

	private final RootConfiguration configuration;
	private final Configuration meta;

	private final ConfigurationWindowExplorer explorer;
	private final VBox sectionDisplay;

	public ConfigurationWindow(RootConfiguration configuration, Configuration meta) {
		this.stage = null;
		this.configuration = configuration;
		this.meta = meta;

		this.explorer = new ConfigurationWindowExplorer(this, null);

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

	public Stage getStage() {
		return stage;
	}

	private void init() {
		getItems().add(explorer);
		getItems().add(sectionDisplay);
	}

	public void display(ConfigurationWindowSection section) {
		sectionDisplay.getChildren().clear();
		if (section.isSpecial()) {
			sectionDisplay.getChildren().add(section.getSpecialNode());
		} else {
			sectionDisplay.getChildren().addAll(sectionDisplay.getChildren());
		}
	}

	public void open() {
		if (stage == null) {
			stage = new Stage();
			Scene scene = new ThemedScene(this);
			stage.initOwner(JamsApplication.getStage());
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setScene(scene);

			stage.setWidth(WIDTH);
			stage.setHeight(HEIGHT);
			stage.setMinWidth(WIDTH >> 1);
			stage.setMinHeight(HEIGHT >> 1);

			Stage main = JamsApplication.getStage();

			stage.setX(main.getX() + main.getWidth() / 2 - (WIDTH >> 1));
			stage.setY(main.getY() + main.getHeight() / 2 - (HEIGHT >> 1));

			stage.setTitle(Jams.getLanguageManager().getSelected().getOrDefault(Messages.CONFIG));

			stage.setOnCloseRequest(event -> {
				try {
					configuration.save(true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			Jams.getLanguageManager().registerListeners(this);
		}

		stage.show();
		Platform.runLater(() -> setDividerPosition(0, 0.3));
	}

	@Listener
	public void onSelectedLanguageChange(SelectedLanguageChangeEvent.After event) {
		stage.setTitle(Jams.getLanguageManager().getSelected().getOrDefault(Messages.CONFIG));
	}
}
