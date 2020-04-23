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

import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.parameter.parse.exception.ParameterParseException;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;

import java.util.Optional;

public class ParameterMatcherFloatRegister implements ParameterMatcher {

	@Override
	public ParameterParseResult parse(String value, Registers registerSet) {
		try {
			Optional<Register> register = registerSet.getCoprocessor1Register(value.substring(1));
			if (!register.isPresent())
				throw new ParameterParseException("No register found");
			return new ParameterParseResult.Builder().register(register.get().getIdentifier()).build();
		} catch (Exception ex) {
			throw new ParameterParseException("Error while parsing parameter " + value + ".", ex);
		}
	}

	@Override
	public boolean match(String value, Registers registerSet) {
		if (value.isEmpty()) return false;
		char c = value.charAt(0);
		return value.length() >= 2
				&& registerSet.getValidRegistersStarts().contains(c)
				&& registerSet.getCoprocessor1Register(value.substring(1)).isPresent();
	}

	@Override
	public boolean match(String value, RegistersBuilder builder) {
		if (value.isEmpty()) return false;
		char c = value.charAt(0);
		return value.length() >= 2
				&& builder.getValidRegistersStarts().contains(c)
				&& builder.getCoprocessor1RegistersNames().contains(value.substring(1));
	}
}
