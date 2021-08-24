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

package net.jamsimulator.jams.mips.instruction.pseudo;

import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.parameter.InstructionParameterTypes;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a pseudo-instruction. Pseudo-instructions are MIPS instructions that are separated into several
 * {@link BasicInstruction} in compile time. Pseudo-instructions don't have a direct translation into machine code.
 */
public abstract class PseudoInstruction implements Instruction {

    /**
     * The $zero register as a {@link ParameterParseResult}.
     */
    protected static final ParameterParseResult ZERO = ParameterParseResult.builder().register(0).build();
    /**
     * The $at register as a {@link ParameterParseResult}.
     */
    protected static final ParameterParseResult AT = ParameterParseResult.builder().register(1).build();
    protected final String mnemonic;
    protected final ParameterType[] parameters;

    public PseudoInstruction(String mnemonic, ParameterType[] parameters) {
        this.mnemonic = mnemonic;
        this.parameters = parameters;
    }

    public PseudoInstruction(String mnemonic, InstructionParameterTypes parameters) {
        this.mnemonic = mnemonic;
        this.parameters = parameters.getParameters();
    }

    /**
     * Returns the {@link BasicInstruction} that matches the given mnemonic and parameter types.
     *
     * @param set        the {@link InstructionSet} containing the instruction.
     * @param mnemonic   the mnemonic of the instruction.
     * @param parameters the parameters of the instruction.
     * @return the instruction.
     * @throws AssemblerException when no instruction matches the mnemonic and parameters.
     */
    protected static BasicInstruction<?> instruction(InstructionSet set, String mnemonic, ParameterType[] parameters) {
        var instruction = set.getInstruction(mnemonic, parameters).orElse(null);
        if (instruction instanceof BasicInstruction) return (BasicInstruction<?>) instruction;
        throw new AssemblerException("Basic instruction '" + mnemonic + "' not found.");
    }

    /**
     * Returns the {@link BasicInstruction} that matches the given mnemonic and parameter types.
     *
     * @param set        the {@link InstructionSet} containing the instruction.
     * @param mnemonic   the mnemonic of the instruction.
     * @param parameters the parameters of the instruction.
     * @return the instruction.
     * @throws AssemblerException when no instruction matches the mnemonic and parameters.
     */
    protected static BasicInstruction<?> instruction(InstructionSet set, String mnemonic, InstructionParameterTypes parameters) {
        var instruction = set.getInstruction(mnemonic, parameters).orElse(null);
        if (instruction instanceof BasicInstruction) return (BasicInstruction<?>) instruction;
        throw new AssemblerException("Basic instruction '" + mnemonic + "' not found.");
    }

    /**
     * Returns the {@link BasicInstruction} of the given class.
     * <p>
     * The class requires the following fields for this method to work.
     * <p>
     * - public static [final] String MNEMONIC
     * <p>
     * - public static [final] InstructionParameterTypes PARAMETER_TYPES
     *
     * @param set   the {@link InstructionSet} containing the instruction.
     * @param clazz the class of the instruction.
     * @return the instruction.
     * @throws AssemblerException when the instruction is not inside the set or the class hasn't got the required fields.
     */
    protected static BasicInstruction<?> instruction(InstructionSet set, Class<? extends BasicInstruction<?>> clazz) {
        try {
            var mnemonicField = clazz.getField("MNEMONIC");
            var typesField = clazz.getField("PARAMETER_TYPES");

            var mnemonic = (String) mnemonicField.get(null);
            var types = (InstructionParameterTypes) typesField.get(null);

            var instruction = set.getInstruction(mnemonic, types).orElse(null);
            if (instruction instanceof BasicInstruction) return (BasicInstruction<?>) instruction;
            throw new AssemblerException("Basic instruction '" + mnemonic + "' not found.");

        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
            throw new AssemblerException(e);
        }
    }

    /**
     * Returns an array with the {@link BasicInstruction}s of the given classes.
     * <p>
     * All classes require the following fields for this method to work.
     * <p>
     * - public static [final] String MNEMONIC
     * <p>
     * - public static [final] InstructionParameterTypes PARAMETER_TYPES
     *
     * @param set     the {@link InstructionSet} containing the instruction.
     * @param classes the classes of the instructions.
     * @return the instructions.
     * @throws AssemblerException when an instruction is not inside the set or one of the classes hasn't got the required fields.
     */
    @SuppressWarnings("unchecked")
    protected static BasicInstruction<?>[] instructions(InstructionSet set, Class<?>... classes) {
        var array = new BasicInstruction<?>[classes.length];
        for (int i = 0; i < classes.length; i++) {
            array[i] = instruction(set, (Class<? extends BasicInstruction<?>>) classes[i]);
        }
        return array;
    }

    /**
     * Returns an array containing the given {@link ParameterParseResult}s.
     *
     * @param results the {@link ParameterParseResult}s.
     * @return the array.
     */
    protected static ParameterParseResult[] parameters(ParameterParseResult... results) {
        return results;
    }

    /**
     * Creates a {@link ParameterParseResult} with the given immediate.
     *
     * @param immediate the immediate.
     * @return the {@link ParameterParseResult}.
     */
    protected static ParameterParseResult immediate(int immediate) {
        return ParameterParseResult.builder().immediate(immediate).build();
    }

    /**
     * Creates a {@link ParameterParseResult} with the given register.
     *
     * @param register the register.
     * @return the {@link ParameterParseResult}.
     */
    protected static ParameterParseResult register(int register) {
        return ParameterParseResult.builder().register(register).build();
    }

    // region helpers

    /**
     * Creates a {@link ParameterParseResult} with the given register and immediate.
     *
     * @param register  the register.
     * @param immediate the immediate.
     * @return the {@link ParameterParseResult}.
     */
    protected static ParameterParseResult registerImmediate(int register, int immediate) {
        return ParameterParseResult.builder().register(register).immediate(immediate).build();
    }

    /**
     * Calculates the shifted address of a {@link ParameterParseResult} created by a parameter with the format immediate($register).
     *
     * @param result the {@link ParameterParseResult}.
     * @return the shifted address.
     */
    protected static int shiftedAddress(ParameterParseResult result) {
        return result.getLabelValue() + result.getImmediate();
    }

    /**
     * Returns the lower 16 bits of the given integer.
     *
     * @param i the integer.
     * @return the lower 16 bits.
     */
    protected static int lower(int i) {
        return i & 0xFFFF;
    }

    /**
     * Returns the upper 16 bits of the given integer.
     *
     * @param i the integer.
     * @return the upper 16 bits.
     */
    protected static int upper(int i) {
        return i >> 16;
    }

    /**
     * Calculates a branch offset with the given label address and the address of the branch.
     *
     * @param label   the label address.
     * @param address the branch address.
     * @return the offset.
     */
    protected static int offset(int label, int address) {
        return (label - address - 4) >> 2;
    }

    @Override
    public String getName() {
        var sufix = mnemonic.toUpperCase().replace('.', '_');
        return Manager.ofS(Language.class).getSelected().getOrDefault("INSTRUCTION_" + sufix);
    }

    @Override
    public String getDocumentation() {
        var sufix = mnemonic.toUpperCase().replace('.', '_');
        return Manager.ofS(Language.class).getSelected().getOrDefault("INSTRUCTION_" + sufix + "_DOCUMENTATION");
    }

    @Override
    public String getMnemonic() {
        return mnemonic;
    }

    @Override
    public ParameterType[] getParameters() {
        return Arrays.copyOf(parameters, parameters.length);
    }

    @Override
    public boolean hasParameters() {
        return parameters.length > 0;
    }

    @Override
    public boolean match(String mnemonic, ParameterType[] parameters) {
        return this.mnemonic.equals(mnemonic) && Arrays.equals(this.parameters, parameters);
    }

    @Override
    public boolean match(String mnemonic, List<ParameterType>[] parameters) {
        if (!this.mnemonic.equalsIgnoreCase(mnemonic)) return false;
        if (parameters.length != this.parameters.length) return false;
        int i = 0;
        for (List<ParameterType> possibilities : parameters) {
            if (!possibilities.contains(this.parameters[i])) return false;
            i++;
        }
        return true;
    }

    /**
     * Returns the amount of {@link AssembledInstruction}s the
     * pseudo-instruction will compile at if the given non-compiled parameters are given to it.
     *
     * @param parameters the non-compiled parameters.
     * @return the amount.
     */
    public int getInstructionAmount(Collection<String> parameters) {
        return getInstructionAmount(parameters.toArray(new String[0]));
    }

    /**
     * Returns the amount of {@link AssembledInstruction}s the
     * pseudo-instruction will compile at if the given non-compiled parameters are given to it.
     *
     * @param parameters the non-compiled parameters.
     * @return the amount.
     */
    public abstract int getInstructionAmount(String[] parameters);

    /**
     * Assembles the given instructions using the given parameters.
     *
     * @param instructions the {@link BasicInstruction}s.
     * @param results      the {@link ParameterParseResult}s.
     * @return the {@link AssembledInstruction}s.
     */
    protected AssembledInstruction[] assemble(BasicInstruction<?>[] instructions, ParameterParseResult[]... results) {
        var array = new AssembledInstruction[instructions.length];
        for (int i = 0; i < instructions.length; i++) {
            array[i] = instructions[i].assembleBasic(results[i], this);
        }
        return array;
    }


    //endregion

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PseudoInstruction that = (PseudoInstruction) o;
        return mnemonic.equals(that.mnemonic) &&
                Arrays.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(mnemonic);
        result = 31 * result + Arrays.hashCode(parameters);
        return result;
    }
}
