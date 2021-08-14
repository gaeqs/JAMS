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

package net.jamsimulator.jams.mips.directive.parameter;

import net.jamsimulator.jams.mips.directive.parameter.matcher.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Represents all types of parameters a directive can have.
 * <p>
 * Each type have their {@link DirectiveParameterMatcher}, allowing to check whether a parameter is valid.
 */
public enum DirectiveParameterType {

    ANY(v -> true),
    STRING(new DirectiveParameterMatcherString()),
    BOOLEAN(new DirectiveParameterMatcherBoolean()),
    BYTE(new DirectiveParameterMatcherByte()),
    BYTE_OR_CHAR(new DirectiveParameterMatcherByteOrChar()),
    SHORT(new DirectiveParameterMatcherShort()),
    INT(new DirectiveParameterMatcherInt()),
    INT_OR_LABEL(new DirectiveParameterMatcherIntOrLabel()),
    POSITIVE_INT(new DirectiveParameterMatcherPositiveInt()),
    LONG(new DirectiveParameterMatcherLong()),
    FLOAT(new DirectiveParameterMatcherFloat()),
    DOUBLE(new DirectiveParameterMatcherDouble()),

    LABEL(new DirectiveParameterMatcherLabel()),
    NUMBER_2_BITS(new DirectiveParameterMatcher2BitsNumber());


    private final DirectiveParameterMatcher matcher;

    DirectiveParameterType(DirectiveParameterMatcher matcher) {
        this.matcher = matcher;
    }

    /**
     * Returns all directive parameter types that matches the given parameter.
     *
     * @param value the given parameter.
     * @return the {@link java.util.Set} with all directives.
     */
    public static Collection<DirectiveParameterType> getAllCandidates(String value) {
        return Arrays.stream(values()).filter(target -> target.matches(value)).collect(Collectors.toSet());
    }

    /**
     * Returns whether the given parameter is valid for this directive parameter type.
     *
     * @param value the parameter.
     * @return whether is valid.
     */
    public boolean matches(String value) {
        return matcher.matches(value);
    }

    public boolean mayBeLabel() {
        return this == LABEL || this == INT_OR_LABEL;
    }
}
