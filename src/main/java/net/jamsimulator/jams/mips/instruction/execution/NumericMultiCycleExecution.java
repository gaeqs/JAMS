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

package net.jamsimulator.jams.mips.instruction.execution;

import net.jamsimulator.jams.mips.architecture.MultiCycleArchitecture;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;

public abstract class NumericMultiCycleExecution<Arch extends MultiCycleArchitecture, Inst extends AssembledInstruction>
        extends MultiCycleExecution<Arch, Inst> {

    protected int lowResult, highResult;

    public NumericMultiCycleExecution(MIPSSimulation<? extends Arch> simulation, Inst instruction,
                                      int address, boolean executesMemory, boolean executesWriteBack) {
        super(simulation, instruction, address, executesMemory, executesWriteBack);
    }

    /**
     * Transforms the given double into two ints.
     * The two ints will be stored in {@link #lowResult} and {@link #highResult}.
     *
     * @param d the double.
     */
    public void doubleToInts(double d) {
        long l = Double.doubleToLongBits(d);
        lowResult = (int) l;
        highResult = (int) (l >> 32);
    }

    /**
     * Transforms the given long into two ints.
     * The two ints will be stored in {@link #lowResult} and {@link #highResult}.
     *
     * @param l the long.
     */
    public void longToInts(long l) {
        lowResult = (int) l;
        highResult = (int) (l >> 32);
    }


}
