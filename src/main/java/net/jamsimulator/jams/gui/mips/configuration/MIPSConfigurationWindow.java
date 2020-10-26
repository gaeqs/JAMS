package net.jamsimulator.jams.gui.mips.configuration;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.theme.ThemedScene;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.project.mips.MIPSProjectData;
import net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfiguration;

public class MIPSConfigurationWindow extends SplitPane {

	private static final int WIDTH = 900;
	private static final int HEIGHT = 600;

	private final MIPSProjectData projectData;

	private final MIPSConfigurationsList list;
	private final AnchorPane displayGroup;

	private MIPSConfigurationDisplay display;

	public MIPSConfigurationWindow(MIPSProjectData projectData) {
		this.projectData = projectData;

		list = new MIPSConfigurationsList(this);
		displayGroup = new AnchorPane();

		getItems().addAll(list, displayGroup);
		Platform.runLater(() -> setDividerPosition(0, 0.3));

		list.getContents().selectFirst();
	}

	public MIPSProjectData getProjectData() {
		return projectData;
	}

	public MIPSConfigurationsList getList() {
		return list;
	}

	public void display(MIPSSimulationConfiguration configuration) {
		if (display != null) {
			if (display.getConfiguration().equals(configuration)) return;
			displayGroup.getChildren().remove(display);
		}
		if (configuration == null) {
			display = null;
			return;
		}
		display = new MIPSConfigurationDisplay(this, configuration);
		displayGroup.getChildren().add(display);
	}

	public static void open(MIPSProjectData data) {
		var window = new MIPSConfigurationWindow(data);

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
		JamsApplication.getIconManager().getOrLoadSafe(Icons.LOGO)
				.ifPresent(stage.getIcons()::add);

		JamsApplication.getActionManager().addAcceleratorsToScene(scene, true);

		stage.setOnCloseRequest(event -> data.save());

		stage.show();
	}

}
