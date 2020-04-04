package net.jamsimulator.jams.mips.parameter;

import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.parameter.parse.matcher.*;
import net.jamsimulator.jams.mips.register.RegisterSet;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents a parameter type. A parameter may be a register, a number, a label or a combination.
 */
public enum ParameterType {

	//SORTED BY PRIORITY
	COPROCESSOR_0_REGISTER("$8", new ParameterMatcherCoprocessor0Register()),
	REGISTER("$t1", new ParameterMatcherRegister()),
	EVEN_FLOAT_REGISTER("$f2", new ParameterMatcherEvenFloatRegister()),
	FLOAT_REGISTER("$f1", new ParameterMatcherFloatRegister()),
	UNSIGNED_5_BIT("5", new ParameterMatcherUnsigned5Bit()),
	SIGNED_16_BIT("-16000", new ParameterMatcherSigned16Bit()),
	UNSIGNED_16_BIT("16000", new ParameterMatcherUnsigned16Bit()),
	SIGNED_32_BIT("-32000000", new ParameterMatcherSigned32Bit()),

	SIGNED_16_BIT_REGISTER_SHIFT("-16000($t1)", new ParameterMatcherSigned16BitRegisterShift()),
	UNSIGNED_16_BIT_REGISTER_SHIFT("16000($t1)", new ParameterMatcherUnsigned16BitRegisterShift()),
	SIGNED_32_BIT_REGISTER_SHIFT("-32000000($t1)", new ParameterMatcherSigned32BitRegisterShift()),
	LABEL("label", new ParameterMatcherLabel()),
	LABEL_REGISTER_SHIFT("label($t1)", new ParameterMatcherLabelRegisterShift()),
	LABEL_SIGNED_32_BIT_SHIFT("label+32000000", new ParameterMatcherLabelSigned32BitShift()),
	LABEL_SIGNED_32_BIT_SHIFT_REGISTER_SHIFT("label+32000000($t2)", new ParameterMatcherLabelSigned32BitShiftRegisterShift());


	private String example;
	private ParameterMatcher matcher;

	ParameterType(String example, ParameterMatcher matcher) {
		this.example = example;
		this.matcher = matcher;
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
	 * Returns whether the given parameter matches this parameter type.
	 *
	 * @param parameter the given parameter, as a {@link String}.
	 * @param set       the available registers.
	 * @return whether the given parameter matches this parameter type.
	 */
	public boolean match(String parameter, RegisterSet set) {
		return matcher.match(parameter, set);
	}

	/**
	 * Parses the given parameter.
	 *
	 * @param parameter the given parameter, as a {@link String}.
	 * @param set       the available registers.
	 * @return the {@link ParameterParseResult}.
	 * @throws net.jamsimulator.jams.mips.parameter.parse.exception.ParameterParseException whether the parse goes wrong.
	 */
	public ParameterParseResult parse(String parameter, RegisterSet set) {
		return matcher.parse(parameter, set);
	}

	/**
	 * Returns the best parameter type for the given parameter, if present.
	 *
	 * @param parameter the given parameter, as a {@link String}.
	 * @return the best parameter type, if present.
	 */
	public static Optional<ParameterType> getParameterMatch(String parameter, RegisterSet set) {
		return Arrays.stream(values()).filter(target -> target.match(parameter, set)).findFirst();
	}

	/**
	 * Returns a mutable list with all the parameter types that are compatible with the given parameter.
	 *
	 * @param parameter the given parameter.
	 * @return the mutable list.
	 */
	public static List<ParameterType> getCompatibleParameterTypes(String parameter, RegisterSet set) {
		return Arrays.stream(values()).filter(target -> target.match(parameter, set)).collect(Collectors.toList());
	}
}
