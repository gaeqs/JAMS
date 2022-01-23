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

package net.jamsimulator.jams.mips.instruction.assembled;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.pseudo.PseudoInstruction;

import java.util.Objects;

/**
 * Represents an assembled instruction.
 */
public abstract class AssembledInstruction {

    /**
     * The shift used by the operation code.
     */
    public static final int OPERATION_CODE_SHIFT = 26;

    /**
     * The instruction code of this assembled instruction.
     */
    protected int value;

    /**
     * The instruction this assembled instruction originates from.
     */
    protected Instruction origin;

    /**
     * The basic instruction this assembled instruction originates from. This value can be the same as {@link #origin}
     * if this assembled instruction originates from a basic instruction.
     */
    protected BasicInstruction<?> basicOrigin;

    /**
     * Creates the assembled instruction.
     *
     * @param code        the operation code.
     * @param origin      the instruction (basic or pseudoinstruction) this assembled instruction originates from.
     * @param basicOrigin the basic instruction this assembled instruction originates from.
     *                    It can be the same instruction as the origin instruction.
     */
    AssembledInstruction(int code, Instruction origin, BasicInstruction<?> basicOrigin) {
        this.value = code;
        this.origin = origin;
        this.basicOrigin = basicOrigin;
    }

    /**
     * Returns the numeric representation of the assembled instruction.
     *
     * @return the numeric representation.
     */
    public int getCode() {
        return value;
    }

    /**
     * Returns the original {@link Instruction} of the assembled instruction.
     * <p>
     * Several assembled instructions may have the same {@link Instruction}, is this was
     * a {@link PseudoInstruction}.
     *
     * @return the original {@link Instruction}.
     */
    public Instruction getOrigin() {
        return origin;
    }

    /**
     * Returns the original {@link BasicInstruction} of the assembled instruction.
     * If the origin instruction was a {@link BasicInstruction} this method will return the same
     * result as the method {@link #getOrigin()}.
     *
     * @return the original {@link BasicInstruction}.
     */
    public BasicInstruction<? extends AssembledInstruction> getBasicOrigin() {
        return basicOrigin;
    }

    /**
     * Returns the operation code of the instruction.
     *
     * @return the operation code.
     */
    public int getOperationCode() {
        return value >>> OPERATION_CODE_SHIFT;
    }

    /**
     * Returns a string representing all parameters of this assembled instruction.
     *
     * @param registersStart the start of the registers.
     * @return the parameters as a string.
     */
    public abstract String parametersToString(String registersStart);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssembledInstruction that = (AssembledInstruction) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
