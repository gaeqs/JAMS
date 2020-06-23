package net.jamsimulator.jams.gui.mips.simulator.execution;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.mips.simulation.Simulation;

public class ExecutionButtons extends HBox {

	public ExecutionButtons(Simulation<?> simulation) {
		Image runOneIcon = JamsApplication.getIconManager().getOrLoadSafe(Icons.PROJECT_PLAY_ONE, Icons.PROJECT_PLAY_ONE_PATH,
				1024, 1024).orElse(null);
		Image runAllIcon = JamsApplication.getIconManager().getOrLoadSafe(Icons.PROJECT_PLAY, Icons.PROJECT_PLAY_PATH,
				1024, 1024).orElse(null);

		Button runOne = new Button("", new NearestImageView(runOneIcon, 16, 16));
		runOne.getStyleClass().add("bold-button");
		runOne.setTooltip(new Tooltip("Run one instruction"));
		runOne.setOnAction(event -> simulation.nextStep());


		Button runAll = new Button("", new NearestImageView(runAllIcon, 16, 16));
		runAll.getStyleClass().add("bold-button");
		runAll.setTooltip(new Tooltip("Run all"));
		runAll.setOnAction(event -> simulation.executeAll());

		getChildren().addAll(runOne, runAll);
	}

}
