/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.mips.architecture;

import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.SimulationData;
import net.jamsimulator.jams.mips.simulation.multicycle.MultiCycleSimulation;

/**
 * Represents the single-cycle architecture.
 * <p>
 * This architecture executes one instruction per cycle, starting and finishing the
 * execution of an instruction on the same cycle. This makes this architecture slow,
 * having high seconds per cycle.
 */
public class MultiCycleArchitecture extends Architecture {

	public static final MultiCycleArchitecture INSTANCE = new MultiCycleArchitecture();

	public static final String NAME = "Multi-cycle";

	protected MultiCycleArchitecture(String name) {
		super(name);
	}

	protected MultiCycleArchitecture() {
		super(NAME);
	}

	@Override
	public MIPSSimulation<? extends MultiCycleArchitecture> createSimulation(InstructionSet instructionSet,
                                                                             Registers registers,
                                                                             Memory memory,
                                                                             int instructionStackBottom,
                                                                             int kernelStackBottom,
                                                                             SimulationData data) {
		return new MultiCycleSimulation(this, instructionSet, registers, memory, instructionStackBottom, kernelStackBottom, data);
	}
}
