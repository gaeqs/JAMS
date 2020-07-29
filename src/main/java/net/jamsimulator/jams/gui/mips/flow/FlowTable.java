package net.jamsimulator.jams.gui.mips.flow;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.mips.flow.singlecycle.SingleCycleFlowTable;
import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.simulation.Simulation;
import org.reactfx.util.TriFunction;

import java.util.HashMap;
import java.util.Map;

public class FlowTable extends VBox {

	//region static

	public static Map<Architecture, TriFunction<Simulation<?>, ScrollPane, Slider, FlowTable>> FLOW_PER_ARCHITECTURE = new HashMap<>();

	static {
		FLOW_PER_ARCHITECTURE.put(SingleCycleArchitecture.INSTANCE, (s, p, sl) ->
				new SingleCycleFlowTable((Simulation<? extends SingleCycleArchitecture>) s, p, sl));
	}

	public static void registerFlow(Architecture architecture, TriFunction<Simulation<?>, ScrollPane, Slider, FlowTable> builder) {
		FLOW_PER_ARCHITECTURE.put(architecture, builder);
	}

	public static FlowTable createFlow(Architecture architecture, Simulation<?> simulation, ScrollPane pane, Slider slider) {
		TriFunction<Simulation<?>, ScrollPane, Slider, FlowTable> builder = FLOW_PER_ARCHITECTURE.get(architecture);
		if (builder == null) return new FlowTable(simulation, pane, slider);
		return builder.apply(simulation, pane, slider);
	}

	//endregion

	public static final int MAX_ITEMS = 100;

	protected Simulation<?> simulation;
	protected ScrollPane scrollPane;
	protected double stepSize = 40;

	public FlowTable(Simulation<?> simulation, ScrollPane scrollPane, Slider sizeSlider) {
		this.simulation = simulation;
		this.scrollPane = scrollPane;

		sizeSlider.setValue(stepSize);
		sizeSlider.valueProperty().addListener((obs, old, val) -> setStepSize(val.doubleValue()));

		getChildren().add(sizeSlider);
	}

	public Simulation<?> getSimulation() {
		return simulation;
	}

	public double getStepSize() {
		return stepSize;
	}

	public void setStepSize(double stepSize) {
		this.stepSize = stepSize;
	}
}
