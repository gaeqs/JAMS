package net.jamsimulator.jams.mips.simulation.pipelined;

/**
 * Represents a {@link net.jamsimulator.jams.mips.simulation.Simulation} that supports forwarding.
 */
public interface ForwardingSupporter {

	/**
	 * Returns the {@link PipelineForwarding} of the simulation.
	 *
	 * @return the {@link PipelineForwarding}.
	 */
	PipelineForwarding getForwarding();

}
