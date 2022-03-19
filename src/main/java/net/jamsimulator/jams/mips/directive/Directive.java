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

package net.jamsimulator.jams.mips.directive;

import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.mips.assembler.MIPS32AssemblerLine;
import net.jamsimulator.jams.mips.directive.parameter.DirectiveParameterType;
import net.jamsimulator.jams.utils.Validate;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;

/**
 * Represents a directive. Directive are the direct equivalent to the preprocessor code in C.
 * They are used to giving orders to the assembler.
 */
public abstract class Directive {

    private final String name;

    private final DirectiveParameterType[] parameters;
    private final boolean repeatLastParameter, optionalParameters, providesAddress;

    /**
     * Creates a new directive.
     *
     * @param name                the name of the directive (without the starting dot).
     * @param parameters          the parameters' types of te directive.
     * @param repeatLastParameter whether the last parameter can be repeated.
     * @param optionalParameters  whether the parameters of this directive are optional.
     * @param providesAddress     whether this directive provides an address to the assembler.
     */
    public Directive(String name, DirectiveParameterType[] parameters,
                     boolean repeatLastParameter, boolean optionalParameters, boolean providesAddress) {
        Validate.notNull(name, "Name cannot be null!");
        Validate.hasNoNulls(parameters, "The parameters array cannot contain null elements!");
        this.name = name;
        this.parameters = parameters;
        this.repeatLastParameter = repeatLastParameter;
        this.optionalParameters = optionalParameters;
        this.providesAddress = providesAddress;
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
     * This string is an HTML-like formatted text containing a complete description of the directive.
     * <p>
     * This documentation depends on the current language of JAMS.
     *
     * @return the documentation.
     */
    public String getDocumentation() {
        var sufix = name.toUpperCase().replace('.', '_');
        return Manager.ofS(Language.class).getSelected().getOrDefault("DIRECTIVE_" + sufix + "_DOCUMENTATION");
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
     * Returns whether the parameters of this directive are optional.
     *
     * @return whether the parameters of this directive are optional.
     */
    public boolean areParametersOptional() {
        return optionalParameters;
    }

    /**
     * Returns whether this directive provides an address to the assembler.
     *
     * @return whether this directive provides an address to the assembler.
     */
    public boolean providesAddress() {
        return providesAddress;
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
     * This method is executed when this label is found in the discovery step of an assembler.
     *
     * @param line          the line where this directive is located.
     * @param parameters    the parameters of this directive.
     * @param rawParameters the parameters in its raw format.
     * @param equivalents   the equivalents used by the step. This map is mutable.
     */
    public void onDiscovery(MIPS32AssemblerLine line, String[] parameters, String rawParameters, Map<String, String> equivalents) {
    }

    /**
     * This method is executed when this label is reached in the expansion step of an assembler.
     *
     * @param line          the line where this directive is located.
     * @param parameters    the parameters of this directive.
     * @param rawParameters the parameters in its raw format.
     */
    public void onExpansion(MIPS32AssemblerLine line, String[] parameters, String rawParameters) {
    }

    /**
     * This method is executed when this label is reached in the address assignation step of an assembler.
     * <p>
     * This method can assign an address to the line. This is used by directives that require memory.
     *
     * @param line          the line where this directive is located.
     * @param parameters    the parameters of this directive.
     * @param rawParameters the parameters in its raw format.
     * @return the address of the start of the memory reserved by this directive, or empty if no memory has been reserved.
     */
    public OptionalInt onAddressAssignation(MIPS32AssemblerLine line, String[] parameters, String rawParameters) {
        return OptionalInt.empty();
    }

    /**
     * This method is executed when this label is reached in the value assignation step of an assembler.
     *
     * @param line          the line where this directive is located.
     * @param parameters    the parameters of this directive.
     * @param rawParameters the parameters in its raw format.
     */
    public void onValueAssignation(MIPS32AssemblerLine line, String[] parameters, String rawParameters) {
    }

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
