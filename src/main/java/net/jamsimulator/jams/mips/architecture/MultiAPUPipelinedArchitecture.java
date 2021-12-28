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

package net.jamsimulator.jams.mips.architecture;

import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.MIPSSimulationData;
import net.jamsimulator.jams.mips.simulation.multiapupipelined.MultiAPUPipelinedSimulation;

/**
 * Represents the multiple APU pipelined architecture.
 * <p>
 * Behaves the same as the pipelined simulations, but it adds support for multiple APUs.
 * These APUs can be executed simultaneously and may need diferent ticks to be executed.
 */
public class MultiAPUPipelinedArchitecture extends PipelinedArchitecture {

    public static final String NAME = "MultiAPU Pipelined";
    public static final MultiAPUPipelinedArchitecture INSTANCE = new MultiAPUPipelinedArchitecture(ResourceProvider.JAMS);

    protected MultiAPUPipelinedArchitecture(ResourceProvider provider, String name) {
        super(provider, name);
    }

    private MultiAPUPipelinedArchitecture(ResourceProvider provider) {
        super(provider, NAME);
    }

    @Override
    public MIPSSimulation<? extends MultiAPUPipelinedArchitecture> createSimulation(MIPSSimulationData data) {
        return new MultiAPUPipelinedSimulation(this, data);
    }
}
