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
import net.jamsimulator.jams.mips.simulation.event.SimulationStartEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationStopEvent;

public class ExecutionButtons extends HBox {

	private final Button runOrStop, runOne, undo, reset;

	public ExecutionButtons(Simulation<?> simulation) {
		Image runOneIcon = JamsApplication.getIconManager().getOrLoadSafe(Icons.PROJECT_PLAY_ONE, Icons.PROJECT_PLAY_ONE_PATH,
				1024, 1024).orElse(null);
		Image undoOneIcon = JamsApplication.getIconManager().getOrLoadSafe(Icons.PROJECT_UNDO_ONE, Icons.PROJECT_UNDO_ONE_PATH,
				1024, 1024).orElse(null);
		Image resetIcon = JamsApplication.getIconManager().getOrLoadSafe(Icons.PROJECT_RESET, Icons.PROJECT_RESET_PATH,
				1024, 1024).orElse(null);

		runOrStop = new Button("", new NearestImageView(null, 16, 16));
		changeToRunAll(simulation);

		runOne = new Button("", new NearestImageView(runOneIcon, 16, 16));
		runOne.getStyleClass().add("bold-button");
		runOne.setTooltip(new Tooltip("Execute one step"));
		runOne.setOnAction(event -> simulation.nextStep());


		undo = new Button("", new NearestImageView(undoOneIcon, 16, 16));
		undo.getStyleClass().add("bold-button");
		undo.setTooltip(new Tooltip("Undo last step"));
		undo.setOnAction(event -> simulation.undoLastStep());
		undo.setDisable(!simulation.getData().isEnableUndo());

		reset = new Button("", new NearestImageView(resetIcon, 16, 16));
		reset.getStyleClass().add("bold-button");
		reset.setTooltip(new Tooltip("Reset"));
		reset.setOnAction(event -> simulation.reset());

		getChildren().addAll(runOrStop, runOne, undo, reset);

		simulation.registerListeners(this, true);
	}


	@Listener
	private void onSimulationStart(SimulationStartEvent event) {
		runOne.setDisable(true);
		changeToStop(event.getSimulation());
	}

	@Listener
	private void onSimulationStop(SimulationStopEvent event) {
		runOne.setDisable(false);
		changeToRunAll(event.getSimulation());
	}

	private void changeToRunAll(Simulation<?> simulation) {
		Image runAllIcon = JamsApplication.getIconManager().getOrLoadSafe(Icons.PROJECT_PLAY, Icons.PROJECT_PLAY_PATH,
				1024, 1024).orElse(null);

		((NearestImageView) runOrStop.getGraphic()).setImage(runAllIcon);
		runOrStop.getStyleClass().add("bold-button");
		runOrStop.setTooltip(new Tooltip("Execute all"));
		runOrStop.setOnAction(event -> simulation.executeAll());
	}

	private void changeToStop(Simulation<?> simulation) {
		Image runAllIcon = JamsApplication.getIconManager().getOrLoadSafe(Icons.PROJECT_STOP, Icons.PROJECT_STOP_PATH,
				1024, 1024).orElse(null);

		((NearestImageView) runOrStop.getGraphic()).setImage(runAllIcon);
		runOrStop.getStyleClass().add("bold-button");
		runOrStop.setTooltip(new Tooltip("Stop"));
		runOrStop.setOnAction(event -> simulation.stop());
	}

}
