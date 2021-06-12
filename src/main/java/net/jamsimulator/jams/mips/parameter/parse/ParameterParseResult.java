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

package net.jamsimulator.jams.mips.parameter.parse;

import net.jamsimulator.jams.mips.parameter.parse.exception.BadParameterParseResultException;

/**
 * Represents a parse result of a parameter from a {@link String} to a parameter.
 * <p>
 * A conversion may have a label, a register, an immediate or a combination of them.
 * <p>
 * An instance of this class should be created using the {@link Builder} class.
 */
public class ParameterParseResult {

    private String label;
    private int register;
    private int immediate;
    private int labelValue;
    private boolean hasLabel, hasRegister, hasImmediate;

    ParameterParseResult(String label, int register, int immediate, boolean hasLabel, boolean hasRegister, boolean hasImmediate) {
        if (!hasLabel && !hasRegister && !hasImmediate)
            throw new BadParameterParseResultException("Result has no label, register or result.");
        this.label = label;
        this.register = register;
        this.immediate = immediate;
        this.hasLabel = hasLabel;
        this.hasRegister = hasRegister;
        this.hasImmediate = hasImmediate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getLabel() {
        return label;
    }

    public int getRegister() {
        return register;
    }

    public int getImmediate() {
        return immediate;
    }

    public boolean isHasLabel() {
        return hasLabel;
    }

    public boolean isHasRegister() {
        return hasRegister;
    }

    public boolean isHasImmediate() {
        return hasImmediate;
    }

    public int getLabelValue() {
        return labelValue;
    }

    public void setLabelValue(int labelValue) {
        this.labelValue = labelValue;
    }

    /**
     * Merges two results into one. Values from the called result has priority.
     *
     * @param other the second result.
     * @return the merged result.
     */
    public ParameterParseResult and(ParameterParseResult other) {
        Builder builder = new Builder();
        if (hasLabel) builder.label(label);
        else if (other.hasLabel) builder.label(other.label);
        if (hasRegister) builder.register(register);
        else if (other.hasRegister) builder.register(other.register);
        if (hasImmediate) builder.immediate(immediate);
        else if (other.hasImmediate) builder.immediate(other.immediate);
        return builder.build();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("ParameterParseResult{");

        if (hasLabel) {
            builder.append("label='").append(label).append('\'');
        }
        if (hasRegister) {
            if (hasLabel) builder.append(", ");
            builder.append("register='").append(register).append('\'');
        }
        if (hasImmediate) {
            if (hasLabel || hasRegister) builder.append(", ");
            builder.append("immediate='").append(immediate).append('\'');
        }
        return builder.append('}').toString();
    }

    /**
     * Represents the builder for the {@link ParameterParseResult} class. Any instance of
     * {@link ParameterParseResult} should be created using this builder.
     */
    public static class Builder {

        private String label;
        private int register;
        private int immediate;

        private boolean hasLabel, hasRegister, hasImmediate;

        public Builder() {
            this.label = null;
            this.register = 0;
            this.immediate = 0;
            this.hasLabel = false;
            this.hasRegister = false;
            this.hasImmediate = false;
        }

        public Builder reset() {
            this.label = null;
            this.register = 0;
            this.immediate = 0;
            this.hasLabel = false;
            this.hasRegister = false;
            this.hasImmediate = false;
            return this;
        }

        public Builder label(String label) {
            this.label = label;
            this.hasLabel = label != null;
            return this;
        }

        public Builder register(int register) {
            this.register = register;
            this.hasRegister = true;
            return this;
        }

        public Builder immediate(int immediate) {
            this.immediate = immediate;
            this.hasImmediate = true;
            return this;
        }

        /**
         * Builds a {@link ParameterParseResult}. If no label, register or immediate are given, this method
         * will throw a {@link BadParameterParseResultException}.
         *
         * @return the {@link ParameterParseResult}.
         * @throws BadParameterParseResultException when no data is given.
         */
        public ParameterParseResult build() {
            return new ParameterParseResult(label, register, immediate, hasLabel, hasRegister, hasImmediate);
        }
    }
}
