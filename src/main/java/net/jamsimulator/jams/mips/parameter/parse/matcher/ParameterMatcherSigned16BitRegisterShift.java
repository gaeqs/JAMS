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

package net.jamsimulator.jams.mips.parameter.parse.matcher;

import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.parameter.parse.exception.ParameterParseException;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;

public class ParameterMatcherSigned16BitRegisterShift implements ParameterMatcher {

	@Override
	public ParameterParseResult parse(String value, Registers registerSet) {
		//Gets the number and the register
		if (value.length() < 5 || !value.contains("(") || !value.endsWith(")"))
			throw new ParameterParseException("Bad parameter format: " + value + ".");
		int parenthesisIndex = value.indexOf('(');
		//Parses
		String register = value.substring(parenthesisIndex + 1, value.length() - 1);
		ParameterParseResult result = ParameterType.REGISTER.parse(register, registerSet);
		System.out.println(ParameterType.SIGNED_16_BIT.parse(value.substring(0, parenthesisIndex), registerSet));
		return result.and(ParameterType.SIGNED_16_BIT.parse(value.substring(0, parenthesisIndex), registerSet));
	}

	@Override
	public boolean match(String value, Registers registerSet) {
		//Gets the number and the register
		if (value.length() < 5 || !value.contains("(") || !value.endsWith(")")) return false;
		int parenthesisIndex = value.indexOf('(');
		String register = value.substring(parenthesisIndex + 1, value.length() - 1);
		//If the register is not valid, return false.
		if (!ParameterType.REGISTER.match(register, registerSet)) return false;
		//Checks the label
		return ParameterType.SIGNED_16_BIT.match(value.substring(0, parenthesisIndex), registerSet);
	}

	@Override
	public boolean match(String value, RegistersBuilder builder) {
		//Gets the number and the register
		if (value.length() < 5 || !value.contains("(") || !value.endsWith(")")) return false;
		int parenthesisIndex = value.indexOf('(');
		String register = value.substring(parenthesisIndex + 1, value.length() - 1);
		//If the register is not valid, return false.
		if (!ParameterType.REGISTER.match(register, builder)) return false;
		//Checks the label
		return ParameterType.SIGNED_16_BIT.match(value.substring(0, parenthesisIndex), builder);
	}
}
