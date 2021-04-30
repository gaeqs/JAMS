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
