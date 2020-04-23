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

public class ParameterMatcherLabelSigned32BitShiftRegisterShift implements ParameterMatcher {

	@Override
	public ParameterParseResult parse(String value, Registers registerSet) {
		int plusIndex = value.indexOf('+');
		if (plusIndex == -1) throw new ParameterParseException("Bad parameter format: " + value + ".");
		//Checks the label and the number
		String label = value.substring(0, plusIndex);
		ParameterParseResult result = ParameterType.LABEL.parse(label, registerSet);
		return result.and(ParameterType.SIGNED_32_BIT_REGISTER_SHIFT.parse(value.substring(plusIndex + 1), registerSet));
	}

	@Override
	public boolean match(String value, Registers registerSet) {
		int plusIndex = value.indexOf('+');
		if (plusIndex == -1) return false;
		//Checks the label and the number
		String label = value.substring(0, plusIndex);
		return ParameterType.LABEL.match(label, registerSet) && ParameterType.SIGNED_32_BIT_REGISTER_SHIFT
				.match(value.substring(plusIndex + 1), registerSet);
	}

	@Override
	public boolean match(String value, RegistersBuilder builder) {
		int plusIndex = value.indexOf('+');
		if (plusIndex == -1) return false;
		//Checks the label and the number
		String label = value.substring(0, plusIndex);
		return ParameterType.LABEL.match(label, builder) && ParameterType.SIGNED_32_BIT_REGISTER_SHIFT
				.match(value.substring(plusIndex + 1), builder);
	}
}
