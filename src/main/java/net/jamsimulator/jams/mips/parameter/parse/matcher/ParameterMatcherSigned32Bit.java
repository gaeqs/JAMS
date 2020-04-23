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
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;
import net.jamsimulator.jams.utils.NumericUtils;

public class ParameterMatcherSigned32Bit implements ParameterMatcher {

	@Override
	public ParameterParseResult parse(String value, Registers registerSet) {
		try {
			return new ParameterParseResult.Builder().immediate(NumericUtils.decodeInteger(value)).build();
		} catch (Exception ex) {
			throw new ParameterParseException("Error while parsing parameter " + value + ".", ex);
		}
	}

	@Override
	public boolean match(String value, Registers registerSet) {
		try {
			NumericUtils.decodeInteger(value);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	@Override
	public boolean match(String value, RegistersBuilder builder) {
		try {
			NumericUtils.decodeInteger(value);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
}
