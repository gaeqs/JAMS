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

package net.jamsimulator.jams.mips.simulation.multiapupipelined;

import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.interrupt.MIPSInterruptException;

public class MultiAPUPipelineSlot {

    public MultiCycleExecution<?, ?> execution;
    public int pc;
    public MIPSInterruptException exception;
    public MultiAPUPipelineSlotStatus status;


    public MultiAPUPipelineSlot(
            MultiCycleExecution<?, ?> instruction,
            int pc,
            MIPSInterruptException exception,
            MultiAPUPipelineSlotStatus status
    ) {
        this.execution = instruction;
        this.pc = pc;
        this.exception = exception;
        this.status = status;
    }

    public MultiAPUPipelineSlot copy() {
        return new MultiAPUPipelineSlot(execution, pc, exception, status);
    }

}
