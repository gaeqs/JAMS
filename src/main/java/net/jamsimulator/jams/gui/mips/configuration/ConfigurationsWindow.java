package net.jamsimulator.jams.gui.mips.configuration;

import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.theme.ThemedScene;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.project.mips.MIPSProjectData;

public class ConfigurationsWindow extends SplitPane {

	public static final int WIDTH = 800, HEIGHT = 400;

	private final ConfigurationDisplay display;
	private final ConfigurationsList list;

	public ConfigurationsWindow(MIPSProjectData data) {
		display = new ConfigurationDisplay(this, data);
		list = new ConfigurationsList(this, data);

		getItems().addAll(list, display);
		SplitPane.setResizableWithParent(list, false);
		setDividerPosition(0, 0.2);
	}

	public ConfigurationDisplay getDisplay() {
		return display;
	}

	public ConfigurationsList getList() {
		return list;
	}

	public static void open(MIPSProjectData data) {
		ConfigurationsWindow window = new ConfigurationsWindow(data);

		Scene scene = new ThemedScene(window);
		Stage stage = new Stage();

		stage.initOwner(JamsApplication.getStage());
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);

		stage.setWidth(WIDTH);
		stage.setHeight(HEIGHT);
		stage.setMinWidth(WIDTH >> 1);
		stage.setMinHeight(0);

		Stage main = JamsApplication.getStage();

		stage.setX(main.getX() + main.getWidth() / 2 - (WIDTH >> 1));
		stage.setY(main.getY() + main.getHeight() / 2 - (HEIGHT >> 1));

		stage.setTitle(Jams.getLanguageManager().getSelected().getOrDefault(Messages.SIMULATION_CONFIGURATION_INFO));
		JamsApplication.getIconManager().getOrLoadSafe(Icons.LOGO, Icons.LOGO_PATH, 250, 250)
				.ifPresent(stage.getIcons()::add);

		JamsApplication.getActionManager().addAcceleratorsToScene(scene, true);

		stage.show();
	}

}
