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

package net.jamsimulator.jams.mips.simulation.pipelined;

import net.jamsimulator.jams.mips.register.Register;

import java.util.OptionalInt;

/**
 * This class implements the forwarding algorithm for {@link PipelinedSimulation}s.
 */
public class PipelineForwarding {

    private Register execution, memory;
    private int executionValue, memoryValue;

    /**
     * Forwards the given value for the given {@link Register}.
     * <p>
     * If {@code memory} is true this value will be stored at the memory slot.
     * Else it will be stored at the execution slot.
     *
     * @param register the {@link Register}.
     * @param value    the value to forward.
     * @param memory   whether the value should be stored at the memory slot.
     */
    public void forward(Register register, int value, boolean memory) {
        if (memory) {
            this.memory = register;
            this.memoryValue = value;
        } else {
            this.execution = register;
            this.executionValue = value;
        }
    }

    /**
     * Returns the forwarded value of the given {@link Register} if present.
     *
     * @param register the {@link Register}.
     * @return the value if present.
     */
    public OptionalInt get(Register register) {
        if (register.equals(execution)) {
            return OptionalInt.of(executionValue);
        }

        if (register.equals(memory)) {
            return OptionalInt.of(memoryValue);
        }

        return OptionalInt.empty();
    }

    /**
     * Clears the forwarding values.
     */
    public void clear() {
        memory = null;
        execution = null;
    }

}
