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

package net.jamsimulator.jams.mips.parameter;

import net.jamsimulator.jams.utils.Validate;

/**
 * Represents the {@link ParameterType}s of an {@link net.jamsimulator.jams.mips.instruction.Instruction}.
 * <p>
 * This class is immutable: you can made public constants with it.
 */
public class InstructionParameterTypes {

    private final ParameterType[] parameters;

    /**
     * Creates an instance.
     *
     * @param parameters the {@link ParameterType}s of the instruction.
     */
    public InstructionParameterTypes(ParameterType... parameters) {
        Validate.hasNoNulls(parameters, "One of the parameter types is null!");
        this.parameters = new ParameterType[parameters.length];
        System.arraycopy(parameters, 0, this.parameters, 0, parameters.length);
    }

    /**
     * Returns the amount of parameters this collection has.
     *
     * @return the amount of {@link ParameterType}s.
     */
    public int parametersAmount() {
        return parameters.length;
    }

    /**
     * Returns the {@link ParameterType} located at the given index.
     *
     * @param index the index.
     * @return the {@link ParameterType}.
     * @throws IndexOutOfBoundsException whether the index is negative or bigger or equals to {@link #parametersAmount()}.
     */
    public ParameterType getParameter(int index) {
        return parameters[index];
    }

    /**
     * Returns a new array with the {@link ParameterType}s inside this instance.
     *
     * @return the array.
     */
    public ParameterType[] getParameters() {
        ParameterType[] array = new ParameterType[parameters.length];
        System.arraycopy(parameters, 0, array, 0, parameters.length);
        return array;
    }

}
