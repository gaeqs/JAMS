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

import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.parameter.parse.matcher.*;
import net.jamsimulator.jams.mips.parameter.split.*;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a parameter type. A parameter may be a register, a number, a label or a combination.
 */
public enum ParameterType {

    //SORTED BY PRIORITY
    COPROCESSOR_0_REGISTER("$8", new ParameterMatcherCoprocessor0Register(), SimpleParameterSplitter.INSTANCE, ParameterPartType.REGISTER, false),
    REGISTER("$t1", new ParameterMatcherRegister(), SimpleParameterSplitter.INSTANCE, ParameterPartType.REGISTER, false),
    EVEN_FLOAT_REGISTER("$f2", new ParameterMatcherEvenFloatRegister(), SimpleParameterSplitter.INSTANCE, ParameterPartType.REGISTER, false),
    FLOAT_REGISTER("$f1", new ParameterMatcherFloatRegister(), SimpleParameterSplitter.INSTANCE, ParameterPartType.REGISTER, false),
    UNSIGNED_3_BIT("3", new ParameterMatcherUnsigned3Bit(), SimpleParameterSplitter.INSTANCE, ParameterPartType.IMMEDIATE, false),
    UNSIGNED_5_BIT("5", new ParameterMatcherUnsigned5Bit(), SimpleParameterSplitter.INSTANCE, ParameterPartType.IMMEDIATE, false),
    UNSIGNED_5_BIT_SUB_ONE("5", new ParameterMatcherUnsigned5BitSubOne(), SimpleParameterSplitter.INSTANCE, ParameterPartType.IMMEDIATE, false),
    SIGNED_16_BIT("-16000", new ParameterMatcherSigned16Bit(), SimpleParameterSplitter.INSTANCE, ParameterPartType.IMMEDIATE, false),
    UNSIGNED_16_BIT("16000", new ParameterMatcherUnsigned16Bit(), SimpleParameterSplitter.INSTANCE, ParameterPartType.IMMEDIATE, false),
    SIGNED_32_BIT("-32000000", new ParameterMatcherSigned32Bit(), SimpleParameterSplitter.INSTANCE, ParameterPartType.IMMEDIATE, false),
    FLOAT("0.1", new ParameterMatcherFloat(), SimpleParameterSplitter.INSTANCE, ParameterPartType.IMMEDIATE, false),
    DOUBLE("0.1", new ParameterMatcherDouble(), SimpleParameterSplitter.INSTANCE, ParameterPartType.IMMEDIATE, false),

    SIGNED_16_BIT_REGISTER_SHIFT("-16000($t1)", new ParameterMatcherSigned16BitRegisterShift(), ParenthesisParameterSplitter.INSTANCE,
            new ParameterPartType[]{ParameterPartType.IMMEDIATE, ParameterPartType.REGISTER}, false),

    UNSIGNED_16_BIT_REGISTER_SHIFT("16000($t1)", new ParameterMatcherUnsigned16BitRegisterShift(), ParenthesisParameterSplitter.INSTANCE,
            new ParameterPartType[]{ParameterPartType.IMMEDIATE, ParameterPartType.REGISTER}, false),

    SIGNED_32_BIT_REGISTER_SHIFT("-32000000($t1)", new ParameterMatcherSigned32BitRegisterShift(), ParenthesisParameterSplitter.INSTANCE,
            new ParameterPartType[]{ParameterPartType.IMMEDIATE, ParameterPartType.REGISTER}, false),

    LABEL_SIGNED_32_BIT_SHIFT_REGISTER_SHIFT("label+32000000($t2)", new ParameterMatcherLabelSigned32BitShiftRegisterShift(),
            new CharParenthesisParameterSplitter('+'),
            new ParameterPartType[]{ParameterPartType.LABEL, ParameterPartType.IMMEDIATE, ParameterPartType.REGISTER}, true),

    LABEL_REGISTER_SHIFT("label($t1)", new ParameterMatcherLabelRegisterShift(), ParenthesisParameterSplitter.INSTANCE,
            new ParameterPartType[]{ParameterPartType.LABEL, ParameterPartType.REGISTER}, true),

    LABEL_SIGNED_32_BIT_SHIFT("label+32000000", new ParameterMatcherLabelSigned32BitShift(), new CharParameterSplitter('+'),
            new ParameterPartType[]{ParameterPartType.LABEL, ParameterPartType.IMMEDIATE}, true),

    LABEL("label", new ParameterMatcherLabel(), SimpleParameterSplitter.INSTANCE, ParameterPartType.LABEL, true);


    private final ParameterSplitter splitter;
    private final String example;
    private final ParameterMatcher matcher;
    private final ParameterPartType[] parts;

    private final boolean hasLabel;

    ParameterType(String example, ParameterMatcher matcher, ParameterSplitter splitter, ParameterPartType part, boolean hasLabel) {
        this.example = example;
        this.matcher = matcher;
        this.splitter = splitter;
        this.parts = new ParameterPartType[]{part};
        this.hasLabel = hasLabel;
    }

    ParameterType(String example, ParameterMatcher matcher, ParameterSplitter splitter, ParameterPartType[] parts, boolean hasLabel) {
        this.example = example;
        this.matcher = matcher;
        this.splitter = splitter;
        this.parts = parts;
        this.hasLabel = hasLabel;
    }

    /**
     * Returns the best parameter type for the given parameter, if present.
     *
     * @param parameter the given parameter, as a {@link String}.
     * @return the best parameter type, if present.
     */
    public static Optional<ParameterType> getParameterMatch(String parameter, Registers set) {
        for (ParameterType value : values()) {
            if (value.match(parameter, set)) return Optional.of(value);
        }
        return Optional.empty();
    }

    /**
     * Returns a mutable list with all the parameter types that are compatible with the given parameter.
     *
     * @param parameter the given parameter.
     * @param set       the set containing all registers.
     * @return the mutable list.
     */
    public static List<ParameterType> getCompatibleParameterTypes(String parameter, Registers set) {
        LinkedList<ParameterType> types = new LinkedList<>();
        for (ParameterType value : values()) {
            if (value.match(parameter, set)) types.add(value);
        }
        return types;
    }

    /**
     * Returns a mutable list with all the parameter types that are compatible with the given parameter.
     *
     * @param parameter the given parameter.
     * @param builder   the builder containing all registers' names.
     * @return the mutable list.
     */
    public static List<ParameterType> getCompatibleParameterTypes(String parameter, RegistersBuilder builder) {
        LinkedList<ParameterType> types = new LinkedList<>();
        for (ParameterType value : values()) {
            if (value.match(parameter, builder)) types.add(value);
        }
        return types;
    }

    /**
     * Returns the example for the register type.
     *
     * @return the example.
     */
    public String getExample() {
        return example;
    }

    /**
     * Returns whether this parameter type contains a label.
     *
     * @return whether it contains a label.
     */
    public boolean hasLabel() {
        return hasLabel;
    }

    public ParameterPartType getPart(int index) {
        return parts[index];
    }

    public ParameterPartType getPartAt(int index, String parameter, AtomicInteger start) {
        if (parts.length == 1) return parts[0];
        int offset = 0;


        for (int i = 0; i < parts.length - 1; i++) {
            var next = parts[i + 1];
            switch (next) {
                case IMMEDIATE -> {
                    int charIndex = parameter.lastIndexOf('+', parameter.length() - offset);
                    if (charIndex == -1 || charIndex >= index) {
                        start.set(offset);
                        return parts[i];
                    }
                    offset = charIndex + 1;
                }
                case REGISTER -> {
                    int charIndex = parameter.lastIndexOf('(', parameter.length() - offset);
                    if (charIndex == -1 || charIndex >= index) {
                        start.set(offset);
                        return parts[i];
                    }
                    offset = charIndex + 1;
                }
            }
        }

        start.set(offset);
        return parts[parts.length - 1];
    }

    public int getAmountOfParts() {
        return parts.length;
    }

    /**
     * Returns whether the given parameter matches this parameter type.
     *
     * @param parameter the given parameter, as a {@link String}.
     * @param set       the available registers.
     * @return whether the given parameter matches this parameter type.
     */
    public boolean match(String parameter, Registers set) {
        return matcher.match(parameter, set);
    }

    /**
     * Returns whether the given parameter matches this parameter type.
     *
     * @param parameter the given parameter, as a {@link String}.
     * @param builder   the available registers.
     * @return whether the given parameter matches this parameter type.
     */
    public boolean match(String parameter, RegistersBuilder builder) {
        return matcher.match(parameter, builder);
    }

    /**
     * Splits the parameter into its respective parts.
     * <p>
     * This splitter only work with valid parameters.
     * Use {@link #match(String, Registers)} to check if a parameter is valid.
     * <p>
     * This method returns an array of ints with the start and length of each part.
     *
     * @param parameter the parameter to split.
     * @return the array.
     */
    public int[] split(String parameter) {
        return splitter.split(parameter);
    }

    /**
     * Parses the given parameter.
     *
     * @param parameter the given parameter, as a {@link String}.
     * @param set       the available registers.
     * @return the {@link ParameterParseResult}.
     * @throws net.jamsimulator.jams.mips.parameter.parse.exception.ParameterParseException whether the parse goes wrong.
     */
    public ParameterParseResult parse(String parameter, Registers set) {
        return matcher.parse(parameter, set);
    }
}
