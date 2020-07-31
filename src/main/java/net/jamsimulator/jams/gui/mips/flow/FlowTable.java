package net.jamsimulator.jams.gui.mips.flow;

import javafx.geometry.Bounds;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.event.ConfigurationNodeChangeEvent;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.mips.flow.multicycle.MultiCycleFlowTable;
import net.jamsimulator.jams.gui.mips.flow.singlecycle.SingleCycleFlowTable;
import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.architecture.MultiCycleArchitecture;
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.simulation.Simulation;
import org.reactfx.util.TriFunction;

import java.util.HashMap;
import java.util.Map;

/**
 * This table watches the instruction flow inside a {@link Simulation}.
 * <p>
 * This class must be extended to provide specific functionalities.
 */
public class FlowTable extends VBox {

	//region static

	public static Map<Architecture, TriFunction<Simulation<?>, ScrollPane, Slider, FlowTable>> FLOW_PER_ARCHITECTURE = new HashMap<>();

	static {
		FLOW_PER_ARCHITECTURE.put(SingleCycleArchitecture.INSTANCE, (s, p, sl) ->
				new SingleCycleFlowTable((Simulation<? extends SingleCycleArchitecture>) s, p, sl));
		FLOW_PER_ARCHITECTURE.put(MultiCycleArchitecture.INSTANCE, (s, p, sl) ->
				new MultiCycleFlowTable((Simulation<? extends MultiCycleArchitecture>) s, p, sl));
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

	protected Simulation<?> simulation;
	protected ScrollPane scrollPane;
	protected double stepSize = 40;
	protected int maxItems;

	protected FlowEntry selected;

	public FlowTable(Simulation<?> simulation, ScrollPane scrollPane, Slider sizeSlider) {
		this.simulation = simulation;
		this.scrollPane = scrollPane;

		maxItems = (int) Jams.getMainConfiguration().get("simulation.mips.flow_max_items").orElse(100);

		sizeSlider.setValue(stepSize);
		sizeSlider.valueProperty().addListener((obs, old, val) -> setStepSize(val.doubleValue()));

		getChildren().add(sizeSlider);

		initSwipeListeners();
		Jams.getMainConfiguration().registerListeners(this, true);
	}

	/**
	 * Returns the {@link Simulation} this table is watching.
	 *
	 * @return the {@link Simulation}.
	 */
	public Simulation<?> getSimulation() {
		return simulation;
	}

	/**
	 * Returns the size of the steps inside a {@link FlowEntry}.
	 *
	 * @return the size.
	 */
	public double getStepSize() {
		return stepSize;
	}

	/**
	 * Sets the size of the steps inside a {@link FlowEntry}.
	 *
	 * @param stepSize the size.
	 */
	public void setStepSize(double stepSize) {
		this.stepSize = stepSize;
	}

	/**
	 * Selects the given {@link FlowEntry}, giving it the selected style class.
	 *
	 * @param entry the {@link FlowEntry} to select.
	 */
	public void select(FlowEntry entry) {
		if (selected != null) {
			selected.getStyleClass().remove("flow-entry-selected");
		}
		selected = entry;
		if (selected != null) {
			selected.getStyleClass().add("flow-entry-selected");
		}
	}

	//MOUSE SCROLL VARIABLES
	private double x, y;
	private double h, v;

	private void initSwipeListeners() {
		setOnMousePressed(event -> {
			x = event.getSceneX();
			y = event.getSceneY();
			h = scrollPane.getHvalue();
			v = scrollPane.getVvalue();
		});

		setOnMouseDragged(event -> {
			double dx = event.getSceneX() - x;
			double dy = event.getSceneY() - y;
			Bounds bounds = scrollPane.getViewportBounds();
			scrollPane.setHvalue(h - dx / (getWidth() - bounds.getWidth()));
			scrollPane.setVvalue(v - dy / (getHeight() - bounds.getHeight()));
		});

	}

	@Listener
	private void onConfigurationNodeChange(ConfigurationNodeChangeEvent.After event) {
		if (event.getNode().equals("simulation.mips.flow_max_items")) {
			maxItems = (int) event.getNewValueAs().orElse(maxItems);
			if (getChildren().size() > maxItems) {
				getChildren().remove(maxItems, getChildren().size());
			}
		}
	}

}
