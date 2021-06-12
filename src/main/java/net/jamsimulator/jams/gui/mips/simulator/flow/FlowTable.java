/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.gui.mips.simulator.flow;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.event.ConfigurationNodeChangeEvent;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.ActionRegion;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.mips.simulator.flow.multicycle.MultiCycleFlowTable;
import net.jamsimulator.jams.gui.mips.simulator.flow.pipelined.PipelinedFlowTable;
import net.jamsimulator.jams.gui.mips.simulator.flow.singlecycle.SingleCycleFlowTable;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.gui.util.PixelScrollPane;
import net.jamsimulator.jams.gui.util.ScalableNode;
import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.architecture.MultiCycleArchitecture;
import net.jamsimulator.jams.mips.architecture.PipelinedArchitecture;
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * This table watches the instruction flow inside a {@link MIPSSimulation}.
 * <p>
 * This class must be extended to provide specific functionalities.
 */
@SuppressWarnings("unchecked")
public abstract class FlowTable extends AnchorPane implements ActionRegion {

    //region static

    private static final Map<Architecture, Function<MIPSSimulation<?>, FlowTable>> FLOW_PER_ARCHITECTURE = new HashMap<>();

    static {
        FLOW_PER_ARCHITECTURE.put(SingleCycleArchitecture.INSTANCE, s ->
                new SingleCycleFlowTable((MIPSSimulation<? extends SingleCycleArchitecture>) s));
        FLOW_PER_ARCHITECTURE.put(MultiCycleArchitecture.INSTANCE, s ->
                new MultiCycleFlowTable((MIPSSimulation<? extends MultiCycleArchitecture>) s));
        FLOW_PER_ARCHITECTURE.put(PipelinedArchitecture.INSTANCE, s ->
                new PipelinedFlowTable((MIPSSimulation<? extends MultiCycleArchitecture>) s));
    }

    protected MIPSSimulation<?> simulation;
    protected ScrollPane scrollPane;

    //endregion
    protected double stepSize = 40;
    protected int maxItems;
    protected Slider sizeSlider;
    protected VBox flows;
    protected FlowTableCycleVisualizer cycleVisualizer;
    protected ScalableNode scalableNode;
    protected FlowEntry selected;
    /**
     * Creates the flow table.
     *
     * @param simulation the simulation.
     */
    public FlowTable(MIPSSimulation<?> simulation) {
        this.simulation = simulation;
        this.scrollPane = new PixelScrollPane();
        this.sizeSlider = new Slider();

        flows = new VBox();
        cycleVisualizer = new FlowTableCycleVisualizer(this, scrollPane);

        var anchorScrollPane = new AnchorPane();
        anchorScrollPane.getChildren().addAll(cycleVisualizer, flows);
        AnchorUtils.setAnchor(flows, 20, 0, 0, 0);

        scalableNode = new ScalableNode(anchorScrollPane, scrollPane);

        maxItems = (int) Jams.getMainConfiguration().get("simulation.mips.flow_max_items").orElse(100);
        sizeSlider.setValue(stepSize);
        sizeSlider.valueProperty().addListener((obs, old, val) -> setStepSize(val.doubleValue()));
        sizeSlider.setPrefHeight(20);
        AnchorUtils.setAnchor(sizeSlider, -1, 0, 2, 2);


        scrollPane.setContent(scalableNode);
        scrollPane.setFitToHeight(true);
        scrollPane.setPannable(true);
        AnchorUtils.setAnchor(scrollPane, 0, 20, 0, 0);


        cycleVisualizer.setMaxHeight(20);
        AnchorUtils.setAnchor(cycleVisualizer, 0, -1, 0, 0);

        getChildren().addAll(scrollPane, sizeSlider);

        cycleVisualizer.toFront();

        Jams.getMainConfiguration().registerListeners(this, true);
    }

    /**
     * Registers a flow table builder for the given {@link Architecture}.
     *
     * @param architecture the {@link Architecture}.
     * @param builder      the builder.
     */
    public static void registerFlow(Architecture architecture, Function<MIPSSimulation<?>, FlowTable> builder) {
        FLOW_PER_ARCHITECTURE.put(architecture, builder);
    }

    /**
     * Creates a flow table for the given {@link MIPSSimulation}.
     * <p>
     * If the simulation's {@link Architecture} has no flow table registered,
     * an empty flow table will be created.
     * <p>
     * Use {@link #registerFlow(Architecture, Function)} to register a flow table.
     *
     * @param simulation the simulation.
     * @return the flow table.
     */
    public static FlowTable createFlow(MIPSSimulation<?> simulation) {
        Function<MIPSSimulation<?>, FlowTable> builder = FLOW_PER_ARCHITECTURE.get(simulation.getArchitecture());
        if (builder == null) return new FlowTable(simulation) {
            @Override
            public long getFirstCycle() {
                return 0;
            }

            @Override
            public long getLastCycle() {
                return 0;
            }
        };
        return builder.apply(simulation);
    }

    /**
     * Returns the {@link MIPSSimulation} this table is watching.
     *
     * @return the {@link MIPSSimulation}.
     */
    public MIPSSimulation<?> getSimulation() {
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
        cycleVisualizer.setStepSize(stepSize);
    }

    /**
     * Refreshes the cycle visualizer of this flow table.
     */
    public void refreshVisualizer() {
        cycleVisualizer.refresh();
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

    /**
     * Returns the first cycle represented in this flow table.
     *
     * @return the first represented cycle.
     */
    public abstract long getFirstCycle();

    /**
     * Returns the last cycle represented in this flow table.
     *
     * @return the last represented cycle.
     */
    public abstract long getLastCycle();

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
