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

package net.jamsimulator.jams.mips.instruction.set;

import net.jamsimulator.jams.manager.ManagerResource;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.pseudo.PseudoInstruction;
import net.jamsimulator.jams.mips.parameter.InstructionParameterTypes;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.utils.Validate;

import java.util.*;

/**
 * Represents an instruction set. An instruction set stores a collection of instruction, and it's
 * used in compile time to parse the code into {@link AssembledInstruction}s.
 * <p>
 * There may be several {@link InstructionSet} instances in the same runtime. The default one is located
 * inside the main class {@link net.jamsimulator.jams.Jams}.
 * <p>
 * Two {@link BasicInstruction} with the same operation code and function code (if present)
 * inside the same instruction set will produce an <b>unpredictable</b> behavior.
 *
 * @see Instruction
 * @see BasicInstruction
 */
public class InstructionSet implements ManagerResource {

    public static final CompatibleInstructionComparator COMPARATOR = new CompatibleInstructionComparator();

    protected final ResourceProvider provider;
    protected final String name;
    protected final Set<Instruction> instructions;

    /**
     * This HashMap maps the operation code of an instruction with its {@link BasicInstruction} instances.
     * This map is used by {@link #getInstructionByInstructionCode(int)} for a faster search algorithm.
     */
    protected final Map<Integer, Set<BasicInstruction<?>>> basicInstructionsOnly;

    /**
     * This Hashmap maps the mnemonic of an instruction with its compatible {@link Instruction} instances.
     * This map is used by {@link #getInstruction(String, ParameterType[])}, {@link #getInstructionByMnemonic(String)}
     * and {@link #getBestCompatibleInstruction(String, List[])} for a faster search algorithm.
     */
    protected final Map<String, Set<Instruction>> instructionsByMnemonic;

    public InstructionSet(ResourceProvider provider, String name) {
        Validate.notNull(provider, "Provider cannot be null!");
        Validate.notNull(name, "Name cannot be null!");
        this.provider = provider;
        this.name = name;
        instructions = new HashSet<>();
        instructionsByMnemonic = new HashMap<>();
        basicInstructionsOnly = new HashMap<>();
    }

    /**
     * Sorts the given list by the best instruction.
     *
     * @param list the list.
     */
    public static void sort(List<? extends Instruction> list) {
        list.sort(COMPARATOR);
    }

    public static int bestInstructionOf(List<? extends Instruction> list) {
        if (list.isEmpty()) throw new IllegalArgumentException("List is empty");
        Instruction best = list.get(0);
        int bestI = 0;

        int i = 1;
        var it = list.iterator();
        Instruction current;
        it.next();
        while (it.hasNext()) {
            current = it.next();
            if (COMPARATOR.compare(best, current) > 0) {
                best = current;
                bestI = i;
            }
            i++;
        }
        return bestI;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ResourceProvider getResourceProvider() {
        return provider;
    }

    /**
     * Returns an unmodifiable {@link Set} with all {@link Instruction}s
     * registered in this instruction set.
     * <p>
     * Any attempt to modify this {@link Set} results in an {@link UnsupportedOperationException}.
     *
     * @return the unmodifiable {@link Set}.
     * @see Collections#unmodifiableSet(Set)
     */
    public Set<Instruction> getInstructions() {
        return Collections.unmodifiableSet(instructions);
    }

    /**
     * Returns a new {@link Set} with all {@link Instruction}s that match the given mnemonic.
     *
     * @param mnemonic the mnemonic.
     * @return the {@link Set}.
     */
    public Set<Instruction> getInstructionByMnemonic(String mnemonic) {
        var set = instructionsByMnemonic.get(mnemonic);
        if (set == null) return Collections.emptySet();
        return Collections.unmodifiableSet(set);
    }

    /**
     * Returns the {@link Instruction} that matches the given mnemonic and parameter types, if present.
     *
     * @param mnemonic   the given mnemonic.
     * @param parameters the given parameters.
     * @return the {@link Instruction}, if present.
     * @throws NullPointerException if the mnemonic, the parameters array or any parameter are null.
     */
    public Optional<Instruction> getInstruction(String mnemonic, ParameterType[] parameters) {
        Validate.notNull(mnemonic, "The given mnemonic cannot be null!");
        Validate.notNull(parameters, "The given parameters array cannot be null!");
        Validate.hasNoNulls(parameters, "One of the parameter types is null!");

        var set = instructionsByMnemonic.get(mnemonic);
        if (set == null) return Optional.empty();
        return set.stream().filter(target -> target.match(mnemonic, parameters)).findFirst();
    }

    /**
     * Returns the {@link Instruction} that matches the given mnemonic and parameter types, if present.
     *
     * @param mnemonic   the given mnemonic.
     * @param parameters the given parameters.
     * @return the {@link Instruction}, if present.
     * @throws NullPointerException if the mnemonic, the parameters array or any parameter are null.
     */
    public Optional<Instruction> getInstruction(String mnemonic, InstructionParameterTypes parameters) {
        Validate.notNull(mnemonic, "The given mnemonic cannot be null!");
        Validate.notNull(parameters, "The given parameters array cannot be null!");

        var set = instructionsByMnemonic.get(mnemonic);
        if (set == null) return Optional.empty();
        return set.stream().filter(target -> target.match(mnemonic, parameters)).findFirst();
    }

    /**
     * Returns the best {@link Instruction} that is compatible with a combination of the given parameters.
     * The possible combinations are represented by an array of lists. Each list has the possible combinations
     * for the parameter in the same index.
     * <p>
     * This method will return the best option using a comparator that uses {@link ParameterType#ordinal()} to
     * compare compatible instructions. {@link BasicInstruction}s have priority over {@link PseudoInstruction}s.
     *
     * @param mnemonic   the mnemonic of the instruction.
     * @param parameters the given possible parameter types.
     * @return the best {@link Instruction}, if present.
     */
    public Optional<Instruction> getBestCompatibleInstruction(String mnemonic, List<ParameterType>[] parameters) {
        Validate.notNull(mnemonic, "Mnemonic cannot be null!");
        Validate.notNull(parameters, "Parameters cannot be null!");
        Validate.hasNoNulls(parameters, "Parameters cannot have null lists!");

        var set = instructionsByMnemonic.get(mnemonic);
        if (set == null) return Optional.empty();
        return set.stream().filter(target -> target.match(mnemonic, parameters)).min(COMPARATOR);
    }

    /**
     * Returns the first {@link BasicInstruction} that matches the given instruction code, if present.
     *
     * @param instructionCode the given instruction code.
     * @return the {@link BasicInstruction}, if present.
     */
    public Optional<? extends BasicInstruction<?>> getInstructionByInstructionCode(int instructionCode) {
        var set = basicInstructionsOnly.get(instructionCode >>> AssembledInstruction.OPERATION_CODE_SHIFT);
        if (set == null || set.isEmpty()) return Optional.empty();
        for (BasicInstruction<?> basic : set) {
            if (basic.match(instructionCode)) return Optional.of(basic);
        }
        return Optional.empty();
    }

    /**
     * Registers an instruction to the instruction set. If an instruction with the same
     * mnemonic and parameters is registered in this instruction set the given instruction
     * will not be added and the method will return {@code false}.
     * <p>
     * Remember that two {@link BasicInstruction} with the same operation code and function code (if present)
     * inside the same instruction set will produce an <b>unpredictable</b> behavior.
     * <p>
     * If you're not sure whether an instruction with the same operation and instruction codes
     * you can use {@link #getInstructionByInstructionCode(int)}.
     *
     * @param instruction the instruction to register.
     * @return whether the instruction was registered.
     */
    public boolean registerInstruction(Instruction instruction) {
        Validate.notNull(instruction, "The given instruction cannot be null!");

        if (instruction instanceof BasicInstruction<?> basic) {
            basicInstructionsOnly.computeIfAbsent(basic.getOperationCode(), k -> new HashSet<>()).add(basic);
        }

        instructionsByMnemonic.computeIfAbsent(instruction.getMnemonic(), k -> new HashSet<>()).add(instruction);
        return instructions.add(instruction);
    }

    public static class CompatibleInstructionComparator implements Comparator<Instruction> {

        @Override
        public int compare(Instruction o1, Instruction o2) {
            //Remember: if negative, o1 has priority.

            //If one of them is a basic instruction, it has priority.
            if (o1 instanceof BasicInstruction && o2 instanceof PseudoInstruction)
                return -1;
            if (o1 instanceof PseudoInstruction && o2 instanceof BasicInstruction)
                return 1;

            //They must have the same length.
            ParameterType[] t1 = o1.getParameters();
            ParameterType[] t2 = o2.getParameters();

            int difference;
            //The last parameter has the biggest priority.
            for (int i = t1.length - 1; i >= 0; i--) {
                difference = t1[i].ordinal() - t2[i].ordinal();
                if (difference != 0) return difference;
            }

            return 0;
        }

    }
}
