package net.jamsimulator.jams.gui.mips.simulator.execution;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.event.SimulationLockEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationUnlockEvent;

public class ExecutionButtons extends HBox {

	private final Button runOne, runAll, undo, reset;

	public ExecutionButtons(Simulation<?> simulation) {
		Image runOneIcon = JamsApplication.getIconManager().getOrLoadSafe(Icons.PROJECT_PLAY_ONE, Icons.PROJECT_PLAY_ONE_PATH,
				1024, 1024).orElse(null);
		Image runAllIcon = JamsApplication.getIconManager().getOrLoadSafe(Icons.PROJECT_PLAY, Icons.PROJECT_PLAY_PATH,
				1024, 1024).orElse(null);
		Image undoOneIcon = JamsApplication.getIconManager().getOrLoadSafe(Icons.PROJECT_UNDO_ONE, Icons.PROJECT_UNDO_ONE_PATH,
				1024, 1024).orElse(null);
		Image resetIcon = JamsApplication.getIconManager().getOrLoadSafe(Icons.PROJECT_RESET, Icons.PROJECT_RESET_PATH,
				1024, 1024).orElse(null);

		runOne = new Button("", new NearestImageView(runOneIcon, 16, 16));
		runOne.getStyleClass().add("bold-button");
		runOne.setTooltip(new Tooltip("Execute one step"));
		runOne.setOnAction(event -> simulation.nextStep());


		runAll = new Button("", new NearestImageView(runAllIcon, 16, 16));
		runAll.getStyleClass().add("bold-button");
		runAll.setTooltip(new Tooltip("Execute all"));
		runAll.setOnAction(event -> simulation.executeAll());


		undo = new Button("", new NearestImageView(undoOneIcon, 16, 16));
		undo.getStyleClass().add("bold-button");
		undo.setTooltip(new Tooltip("Undo last step"));
		undo.setOnAction(event -> simulation.undoLastStep());

		reset = new Button("", new NearestImageView(resetIcon, 16, 16));
		reset.getStyleClass().add("bold-button");
		reset.setTooltip(new Tooltip("Reset"));
		reset.setOnAction(event -> simulation.reset());

		getChildren().addAll(runAll, runOne, undo, reset);

		simulation.registerListeners(this, true);
	}


	@Listener
	private void onSimulationLock(SimulationLockEvent event) {
		runOne.setDisable(true);
		runAll.setDisable(true);
	}

	@Listener
	private void onSimulationUnlock (SimulationUnlockEvent event) {
		runOne.setDisable(false);
		runAll.setDisable(false);
	}

}
