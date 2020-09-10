package net.jamsimulator.jams.mips.simulation.pipelined.event;

import net.jamsimulator.jams.mips.simulation.pipelined.Pipeline;
import net.jamsimulator.jams.mips.simulation.pipelined.PipelinedSimulation;

public class PipelineShiftEvent extends PipelinedSimulationEvent {

	protected final long cycle;
	protected final Pipeline pipeline;
	protected int toFetch;

	private PipelineShiftEvent(PipelinedSimulation simulation, Pipeline pipeline, int toFetch) {
		super(simulation);
		this.cycle = simulation.getCycles();
		this.pipeline = pipeline;
		this.toFetch = toFetch;
	}


	public long getCycle() {
		return cycle;
	}

	public Pipeline getPipeline() {
		return pipeline;
	}

	public int getToFetch() {
		return toFetch;
	}

	public static class Before extends PipelineShiftEvent {

		public Before(PipelinedSimulation simulation, Pipeline status, int toFetch) {
			super(simulation, status, toFetch);
		}

		public void setToFetch(int toFetch) {
			this.toFetch = toFetch;
		}
	}


	public static class After extends PipelineShiftEvent {

		public After(PipelinedSimulation simulation, Pipeline status, int toFetch) {
			super(simulation, status, toFetch);
		}
	}
}
