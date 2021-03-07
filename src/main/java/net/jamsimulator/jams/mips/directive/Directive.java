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

package net.jamsimulator.jams.mips.directive;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.mips.assembler.MIPS32AssemblingFile;
import net.jamsimulator.jams.mips.directive.parameter.DirectiveParameterType;
import net.jamsimulator.jams.utils.Validate;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a directive. Directive are the direct equivalent to the preprocessor code in C.
 * They are used to give orders to the assembler.
 */
public abstract class Directive {

    private final String name;

    private final DirectiveParameterType[] parameters;
    private final boolean repeatLastParameter, optionalParameters;

    public Directive(String name, DirectiveParameterType[] parameters, boolean repeatLastParameter, boolean optionalParameters) {
        Validate.notNull(name, "Name cannot be null!");
        Validate.hasNoNulls(parameters, "The parameters array cannot contain null elements!");
        this.name = name;
        this.parameters = parameters;
        this.repeatLastParameter = repeatLastParameter;
        this.optionalParameters = optionalParameters;
    }

    /**
     * Returns the name of this directive.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the documentation of the instruction.
     * This string is a HTML-like formatted text containing a complete description of the directive.
     * <p>
     * This documentation depends on the current language of JAMS.
     *
     * @return the documentation.
     */
    public String getDocumentation() {
        var sufix = name.toUpperCase().replace('.', '_');
        return Jams.getLanguageManager().getSelected().getOrDefault("DIRECTIVE_" + sufix + "_DOCUMENTATION");
    }

    /**
     * Returns whether this directive may have parameters.
     *
     * @return whether this directive may have parameters.
     */
    public boolean hasParameters() {
        return parameters.length > 0;
    }

    /**
     * Returns the amount of parameters this directive has.
     * This doesn't count whether this directive can repeat the last parameter.
     *
     * @return the amount of parameters.
     */
    public int getParametersAmount() {
        return parameters.length;
    }

    /**
     * Returns an unmodifiable array with all {@link DirectiveParameterType parameter types} of this directive.
     *
     * @return the array.
     */
    public DirectiveParameterType[] getParameters() {
        return Arrays.copyOf(parameters, parameters.length);
    }

    /**
     * Returns the {@link DirectiveParameterType} for the parameter of the given index.
     *
     * @param index the index of the parameter.
     * @return the type, or null if not found.
     */
    public DirectiveParameterType getParameterTypeFor(int index) {
        //Check if parameter is out of bounds.
        if (index < 0 || index >= parameters.length && !repeatLastParameter) return DirectiveParameterType.ANY;
        return parameters[Math.min(index, parameters.length - 1)];
    }

    /**
     * Returns whether the last parameter of this directive can be repeated.
     * <p>
     * If true, this directive can have an undefined amount of parameters of the same type.
     *
     * @return whether the last parameter of this directive can be repeated.
     */
    public boolean canRepeatLastParameter() {
        return repeatLastParameter;
    }

    /**
     * Returns when the parameters of this directive are optional.
     *
     * @return when the parameters of this directive are optional.
     */
    public boolean areParametersOptional() {
        return optionalParameters;
    }

    /**
     * Returns whether the parameter at the given index is valid for this directive.
     *
     * @param index the index of the parameter.
     * @param value the parameter.
     * @return whether the parameter is valid.
     */
    public boolean isParameterValid(int index, String value) {
        var type = getParameterTypeFor(index);
        return type != null && type.matches(value);
    }

    /**
     * Executes the directive in the given assembler.
     *
     * @param lineNumber the line number the directive is at.
     * @param line       the line of the directive.
     * @param parameters the parameters of the directive.
     * @param file       the file where this directive is at.
     * @return the start address of the directive.
     */
    public abstract int execute(int lineNumber, String line, String[] parameters, MIPS32AssemblingFile file);

    /**
     * This method is executed after all labels, instructions and directives had been decoded.
     *
     * @param parameters the parameters of the directive.
     * @param file       the file where the directive is located at.
     * @param lineNumber the line number the directive is at.
     * @param address    the start of the memory address dedicated to this directive in the method {@link #execute(int, String, String[], MIPS32AssemblingFile)}.
     */
    public abstract void postExecute(String[] parameters, MIPS32AssemblingFile file, int lineNumber, int address);

    public abstract boolean isParameterValidInContext(int index, String value, MIPSFileElements context);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Directive directive = (Directive) o;
        return name.equals(directive.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
