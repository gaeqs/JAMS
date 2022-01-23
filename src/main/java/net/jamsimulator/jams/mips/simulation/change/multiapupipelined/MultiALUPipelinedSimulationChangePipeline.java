/*
 *  MIT License
 *
 *  Copyright (c) 2022 Gael Rial Costas
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

package net.jamsimulator.jams.mips.simulation.change.multialupipelined;

import net.jamsimulator.jams.mips.architecture.MultiALUPipelinedArchitecture;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.change.SimulationChange;
import net.jamsimulator.jams.mips.simulation.multialupipelined.MultiALUPipeline;
import net.jamsimulator.jams.mips.simulation.multialupipelined.MultiALUPipelinedSimulation;

/**
 * A {@link SimulationChange} that registers the change of the pipelin of a {@link  net.jamsimulator.jams.mips.simulation.multialupipelined.MultiALUPipelinedSimulation}.
 */
public class MultiALUPipelinedSimulationChangePipeline extends SimulationChange<MultiALUPipelinedArchitecture> {

    private final MultiALUPipeline old;

    public MultiALUPipelinedSimulationChangePipeline(MultiALUPipeline old) {
        this.old = old;
    }

    @Override
    public void restore(MIPSSimulation<? extends MultiALUPipelinedArchitecture> simulation) {
        ((MultiALUPipelinedSimulation) simulation).getPipeline().restore(old);
    }
}
