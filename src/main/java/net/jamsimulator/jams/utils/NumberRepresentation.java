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

package net.jamsimulator.jams.utils;

import net.jamsimulator.jams.manager.Labeled;
import net.jamsimulator.jams.mips.interrupt.MIPSAddressException;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.cache.CacheBlock;

import java.util.function.BiFunction;

/**
 * Represents a way a number (or a set of two numbers) can be represented in a String.
 */
public class NumberRepresentation implements Labeled {

    private final String name;
    private final boolean requiresNextWord;
    private final boolean color;

    private final BiFunction<Integer, Integer, String> transformer;

    /**
     * Creates the representation.
     *
     * @param name             the name of the representation.
     * @param requiresNextWord whether this representation requires two numbers.
     * @param color            whether this representation is a color represnetation.
     * @param transformer      the function that transforms the number(s) into a String.
     */
    public NumberRepresentation(String name, boolean requiresNextWord, boolean color, BiFunction<Integer, Integer, String> transformer) {
        Validate.notNull(name, "Name cannot be null!");
        this.name = name;
        this.requiresNextWord = requiresNextWord;
        this.color = color;

        this.transformer = transformer;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the language node of this representation.
     *
     * @return the language node.
     */
    public String getLanguageNode() {
        return "NUMBER_FORMAT_" + name;
    }


    /**
     * Returns whether this representation requires the number following the first one.
     *
     * @return whether this representation requires two numbers.
     */
    public boolean requiresNextWord() {
        return requiresNextWord;
    }

    /**
     * Returns whether this representation should be used as a color.
     *
     * @return whether this representation is a color representation.
     */
    public boolean isColor() {
        return color;
    }

    /**
     * Represents the number stored in the given {@link Memory memory} at the given address.
     * <p>
     * This method will bypass all {@link net.jamsimulator.jams.mips.memory.cache.Cache cache}s and will not trigger any event inside the memory.
     * This method may also fetch the word at the following address if the {@link #requiresNextWord() representation} requires it.
     *
     * @param memory  the {@link Memory memory}.
     * @param address the address.
     * @return the represented number.
     */
    public String represent(Memory memory, int address) {
        try {
            int o1 = memory.getWord(address, false, true, true);
            int o2 = 0;
            if (requiresNextWord) {
                o2 = memory.getWord(address + 4, false, true, true);
            }

            return transformer.apply(o1, o2);
        } catch (MIPSAddressException ex) {
            System.err.println("Bad address " + StringUtils.addZeros(Integer.toHexString(ex.getBadAddress()), 8));
            ex.printStackTrace();
            return "0'";
        }
    }

    /**
     * Represents the number stored in the given {@link CacheBlock cache block} at the given address.
     * <p>
     * This method may also fetch the word at the following address if the {@link #requiresNextWord() representation} requires it.
     * If the next word cannot be fetched, a zero will be used instead.
     *
     * @param block   the {@link CacheBlock cache block}.
     * @param address the address.
     * @return the represented number.
     */
    public String represent(CacheBlock block, int address) {
        int o1 = block.getWord(address, false);
        int o2 = 0;

        if (requiresNextWord && block.getData().length > address + 4) {
            o2 = block.getWord(address + 4, false);
        }

        return transformer.apply(o1, o2);
    }

    /**
     * Represents the given two numbers.
     * <p>
     * If the representation only requires one number, the second number will be ingnored.
     *
     * @param first  the first number.
     * @param second the second number.
     * @return the represented number.
     */
    public String represent(int first, int second) {
        return transformer.apply(first, second);
    }

}
