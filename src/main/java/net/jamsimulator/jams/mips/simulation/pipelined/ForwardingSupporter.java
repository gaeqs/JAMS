package net.jamsimulator.jams.mips.simulation.pipelined;

import net.jamsimulator.jams.mips.simulation.MIPSSimulation;

/**
 * Represents a {@link MIPSSimulation} that supports forwarding.
 */
public interface ForwardingSupporter {

	/**
	 * Returns the {@link PipelineForwarding} of the simulation.
	 *
	 * @return the {@link PipelineForwarding}.
	 */
	PipelineForwarding getForwarding();

}
