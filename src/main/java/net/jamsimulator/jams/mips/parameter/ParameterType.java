/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.mips.parameter;

import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.parameter.parse.matcher.*;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents a parameter type. A parameter may be a register, a number, a label or a combination.
 */
public enum ParameterType {

	//SORTED BY PRIORITY
	COPROCESSOR_0_REGISTER("$8", new ParameterMatcherCoprocessor0Register(), false),
	REGISTER("$t1", new ParameterMatcherRegister(), false),
	EVEN_FLOAT_REGISTER("$f2", new ParameterMatcherEvenFloatRegister(), false),
	FLOAT_REGISTER("$f1", new ParameterMatcherFloatRegister(), false),
	UNSIGNED_5_BIT("5", new ParameterMatcherUnsigned5Bit(), false),
	SIGNED_16_BIT("-16000", new ParameterMatcherSigned16Bit(), false),
	UNSIGNED_16_BIT("16000", new ParameterMatcherUnsigned16Bit(), false),
	SIGNED_32_BIT("-32000000", new ParameterMatcherSigned32Bit(), false),

	SIGNED_16_BIT_REGISTER_SHIFT("-16000($t1)", new ParameterMatcherSigned16BitRegisterShift(), false),
	UNSIGNED_16_BIT_REGISTER_SHIFT("16000($t1)", new ParameterMatcherUnsigned16BitRegisterShift(), false),
	SIGNED_32_BIT_REGISTER_SHIFT("-32000000($t1)", new ParameterMatcherSigned32BitRegisterShift(), false),
	LABEL("label", new ParameterMatcherLabel(), true),
	LABEL_REGISTER_SHIFT("label($t1)", new ParameterMatcherLabelRegisterShift(), true),
	LABEL_SIGNED_32_BIT_SHIFT("label+32000000", new ParameterMatcherLabelSigned32BitShift(), true),
	LABEL_SIGNED_32_BIT_SHIFT_REGISTER_SHIFT("label+32000000($t2)", new ParameterMatcherLabelSigned32BitShiftRegisterShift(), true);


	private final String example;
	private final ParameterMatcher matcher;
	private final boolean hasLabel;

	ParameterType(String example, ParameterMatcher matcher, boolean hasLabel) {
		this.example = example;
		this.matcher = matcher;
		this.hasLabel = hasLabel;
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
}
