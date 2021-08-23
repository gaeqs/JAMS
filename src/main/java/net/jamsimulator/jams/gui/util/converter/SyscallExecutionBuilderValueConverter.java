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

package net.jamsimulator.jams.gui.util.converter;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

import java.util.Optional;

public class SyscallExecutionBuilderValueConverter extends ValueConverter<SyscallExecutionBuilder<?>> {

    public static final String NAME = "syscall_execution_builder";

    @Override
    public String toString(SyscallExecutionBuilder<?> value) {
        return value == null ? null : value.getName();
    }

    @Override
    public Optional<SyscallExecutionBuilder<?>> fromStringSafe(String value) {
        return Jams.getSyscallExecutionBuilderManager().get(value).map(it -> (SyscallExecutionBuilder<?>) it);
    }

    @Override
    public Class<?> conversionClass() {
        return SyscallExecutionBuilder.class;
    }
}
