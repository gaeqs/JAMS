package net.jamsimulator.jams.gui.mips.simulator.execution;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageTooltip;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.event.SimulationStartEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationStopEvent;

public class ExecutionButtons extends HBox {

	private final Button runOrStop, runOne, undo, reset;

	public ExecutionButtons(Simulation<?> simulation) {
		Image runOneIcon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIMULATION_PLAY_ONE, Icons.SIMULATION_PLAY_ONE_PATH,
				1024, 1024).orElse(null);
		Image undoOneIcon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIMULATION_UNDO_ONE, Icons.SIMULATION_UNDO_ONE_PATH,
				1024, 1024).orElse(null);
		Image resetIcon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIMULATION_RESET, Icons.SIMULATION_RESET_PATH,
				1024, 1024).orElse(null);

		runOrStop = new Button("", new NearestImageView(null, 16, 16));
		changeToRunAll(simulation);

		runOne = new Button("", new NearestImageView(runOneIcon, 16, 16));
		runOne.getStyleClass().add("bold-button");
		runOne.setTooltip(new LanguageTooltip(Messages.SIMULATION_BUTTON_TOOLTIP_EXECUTE_ONE, LanguageTooltip.DEFAULT_DELAY));
		runOne.setOnAction(event -> simulation.nextStep());


		undo = new Button("", new NearestImageView(undoOneIcon, 16, 16));
		undo.getStyleClass().add("bold-button");
		undo.setTooltip(new LanguageTooltip(Messages.SIMULATION_BUTTON_TOOLTIP_UNDO, LanguageTooltip.DEFAULT_DELAY));
		undo.setOnAction(event -> simulation.undoLastStep());
		undo.setDisable(!simulation.getData().isUndoEnabled());

		reset = new Button("", new NearestImageView(resetIcon, 16, 16));
		reset.getStyleClass().add("bold-button");

		reset.setTooltip(new LanguageTooltip(Messages.SIMULATION_BUTTON_TOOLTIP_RESET, LanguageTooltip.DEFAULT_DELAY));
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
		Platform.runLater(() -> {
			Image runAllIcon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIMULATION_PLAY, Icons.SIMULATION_PLAY_PATH,
					1024, 1024).orElse(null);
			((NearestImageView) runOrStop.getGraphic()).setImage(runAllIcon);
			runOrStop.getStyleClass().add("bold-button");
			runOrStop.setTooltip(new LanguageTooltip(Messages.SIMULATION_BUTTON_TOOLTIP_EXECUTE_ALL, LanguageTooltip.DEFAULT_DELAY));
			runOrStop.setOnAction(event -> simulation.executeAll());
		});
	}

	private void changeToStop(Simulation<?> simulation) {
		Platform.runLater(() -> {
			Image runAllIcon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIMULATION_STOP, Icons.SIMULATION_STOP_PATH,
					1024, 1024).orElse(null);

			((NearestImageView) runOrStop.getGraphic()).setImage(runAllIcon);
			runOrStop.getStyleClass().add("bold-button");
			runOrStop.setTooltip(new LanguageTooltip(Messages.SIMULATION_BUTTON_TOOLTIP_STOP, LanguageTooltip.DEFAULT_DELAY));
			runOrStop.setOnAction(event -> simulation.stop());
		});
	}

}
