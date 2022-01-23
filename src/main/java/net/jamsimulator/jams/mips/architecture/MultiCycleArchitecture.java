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
import net.jamsimulator.jams.mips.simulation.multicycle.MultiCycleSimulation;

/**
 * Represents the multi-cycle architecture.
 * <p>
 * This architecture executes one instruction in multiple cycles.
 * This makes this architecture faster in real hardware, saving time
 * on instructions with fewer steps.
 */
public class MultiCycleArchitecture extends Architecture {

    public static final String NAME = "Multi-cycle";
    public static final MultiCycleArchitecture INSTANCE = new MultiCycleArchitecture(ResourceProvider.JAMS);

    protected MultiCycleArchitecture(ResourceProvider provider, String name) {
        super(provider, name);
    }

    private MultiCycleArchitecture(ResourceProvider provider) {
        super(provider, NAME);
    }

    @Override
    public MIPSSimulation<? extends MultiCycleArchitecture> createSimulation(MIPSSimulationData data) {
        return new MultiCycleSimulation(this, data);
    }
}
