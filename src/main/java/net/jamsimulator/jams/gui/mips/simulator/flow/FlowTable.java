package net.jamsimulator.jams.gui.mips.simulator.flow;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.event.ConfigurationNodeChangeEvent;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.ActionRegion;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.mips.simulator.flow.multicycle.MultiCycleFlowTable;
import net.jamsimulator.jams.gui.mips.simulator.flow.pipelined.PipelinedFlowTable;
import net.jamsimulator.jams.gui.mips.simulator.flow.singlecycle.SingleCycleFlowTable;
import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.architecture.MultiCycleArchitecture;
import net.jamsimulator.jams.mips.architecture.PipelinedArchitecture;
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
public class FlowTable extends VBox implements ActionRegion {

	//region static

	private static final Map<Architecture, TriFunction<Simulation<?>, ScrollPane, Slider, FlowTable>> FLOW_PER_ARCHITECTURE = new HashMap<>();

	static {
		FLOW_PER_ARCHITECTURE.put(SingleCycleArchitecture.INSTANCE, (s, p, sl) ->
				new SingleCycleFlowTable((Simulation<? extends SingleCycleArchitecture>) s, p, sl));
		FLOW_PER_ARCHITECTURE.put(MultiCycleArchitecture.INSTANCE, (s, p, sl) ->
				new MultiCycleFlowTable((Simulation<? extends MultiCycleArchitecture>) s, p, sl));
		FLOW_PER_ARCHITECTURE.put(PipelinedArchitecture.INSTANCE, (s, p, sl) ->
				new PipelinedFlowTable((Simulation<? extends MultiCycleArchitecture>) s, p, sl));
	}

	/**
	 * Registers a flow table builder for the given {@link Architecture}.
	 *
	 * @param architecture the {@link Architecture}.
	 * @param builder      the builder.
	 */
	public static void registerFlow(Architecture architecture, TriFunction<Simulation<?>, ScrollPane, Slider, FlowTable> builder) {
		FLOW_PER_ARCHITECTURE.put(architecture, builder);
	}

	/**
	 * Creates a flow table for the given {@link Simulation}.
	 * <p>
	 * If the simulation's {@link Architecture} has no flow table registered,
	 * an empty flow table will be created.
	 * <p>
	 * Use {@link #registerFlow(Architecture, TriFunction)} to register a flow table.
	 *
	 * @param simulation the simulation.
	 * @param pane       the {@link ScrollPane} where this flow table will be inside of.
	 * @param slider     the {@link Slider} that controls the size of the entries.
	 * @return the flow table.
	 */
	public static FlowTable createFlow(Simulation<?> simulation, ScrollPane pane, Slider slider) {
		TriFunction<Simulation<?>, ScrollPane, Slider, FlowTable> builder =
				FLOW_PER_ARCHITECTURE.get(simulation.getArchitecture());
		if (builder == null) return new FlowTable(simulation, pane, slider);
		return builder.apply(simulation, pane, slider);
	}

	//endregion

	protected Simulation<?> simulation;
	protected ScrollPane scrollPane;
	protected double stepSize = 40;
	protected int maxItems;

	protected FlowEntry selected;

	/**
	 * Creates the flow table.
	 *
	 * @param simulation the simulation.
	 * @param scrollPane the {@link ScrollPane} where this flow table will be inside of.
	 * @param sizeSlider the {@link Slider} that controls the size of the entries.
	 */
	public FlowTable(Simulation<?> simulation, ScrollPane scrollPane, Slider sizeSlider) {
		this.simulation = simulation;
		this.scrollPane = scrollPane;

		maxItems = (int) Jams.getMainConfiguration().get("simulation.mips.flow_max_items").orElse(100);

		sizeSlider.setValue(stepSize);
		sizeSlider.valueProperty().addListener((obs, old, val) -> setStepSize(val.doubleValue()));

		getChildren().add(sizeSlider);

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

	@Override
	public boolean supportsActionRegion(String region) {
		return RegionTags.MIPS_SIMULATION.equals(region);
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
