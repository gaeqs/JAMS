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

package net.jamsimulator.jams.mips.simulation.multialupipelined.event;

import net.jamsimulator.jams.mips.simulation.multialupipelined.MultiALUPipeline;
import net.jamsimulator.jams.mips.simulation.multialupipelined.MultiALUPipelinedSimulation;

/**
 * This event is called when a {@link MultiALUPipeline} shifts its instructions.
 */
public class MultiALUPipelineShiftEvent extends MultiALUPipelinedSimulationEvent {

    protected final long cycle;
    protected final MultiALUPipeline pipeline;

    private MultiALUPipelineShiftEvent(MultiALUPipelinedSimulation simulation, MultiALUPipeline pipeline) {
        super(simulation);
        this.cycle = simulation.getCycles();
        this.pipeline = pipeline;
    }

    /**
     * Returns the cycle being executed on the simulation when the pipeline is being shifted.
     *
     * @return the cycle.
     */
    public long getCycle() {
        return cycle;
    }

    /**
     * Returns the {@link MultiALUPipeline} that is shifting.
     *
     * @return the {@link MultiALUPipeline}.
     */
    public MultiALUPipeline getPipeline() {
        return pipeline;
    }

    /**
     * This event is called before a {@link MultiALUPipeline}'s shift.
     */
    public static class Before extends MultiALUPipelineShiftEvent {

        public Before(MultiALUPipelinedSimulation simulation, MultiALUPipeline status) {
            super(simulation, status);
        }

    }

    /**
     * This event is called after a {@link MultiALUPipeline}'s shift.
     */
    public static class After extends MultiALUPipelineShiftEvent {

        public After(MultiALUPipelinedSimulation simulation, MultiALUPipeline status) {
            super(simulation, status);
        }
    }
}
