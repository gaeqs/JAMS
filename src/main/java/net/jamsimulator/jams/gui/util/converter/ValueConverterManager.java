/*
 *  MIT License
 *
 *  Copyright (c) 2022 Gael Rial Costas
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

import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.utils.ReflectionUtils;

import java.util.Optional;

public class ValueConverterManager extends Manager<ValueConverter> {
    public static final String NAME = "value_converter";
    public static final ValueConverterManager INSTANCE = new ValueConverterManager(ResourceProvider.JAMS, NAME);

    public ValueConverterManager(ResourceProvider provider, String name) {
        super(provider, name, ValueConverter.class, false);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<ValueConverter<T>> getByType(Class<T> clazz) {
        if (clazz == null) return Optional.empty();
        return (Optional<ValueConverter<T>>) stream()
                .filter(it -> it.conversionClass().equals(clazz))
                .map(it -> (T) it).findAny();
    }

    public <T> ValueConverter<T> getByTypeUnsafe(Class<T> clazz) {
        return getByType(clazz).orElse(null);
    }

    @Override
    protected void loadDefaultElements() {
        add(new ActionValueConverter());
        add(new ALUCollectionSnapshotValueConverter());
        add(new ALUTypeValueConverter());
        add(new ALUValueConverter());
        add(new ArchitectureValueConverter());
        add(new AssemblerBuilderValueConverter());
        add(new BooleanValueConverter());
        add(new CacheBuilderValueConverter());
        add(new DirectiveSetValueConverter());
        add(new DoubleValueConverter());
        add(new FloatValueConverter());
        add(new InstructionSetValueConverter());
        add(new IntegerValueConverter());
        add(new LanguageValueConverter());
        add(new MemoryBuilderValueConverter());
        add(new MIPSAssembledInstructionViewerOrderValueConverter());
        add(new MIPSSpacesValueConverter());
        add(new RegistersBuilderValueConverter());
        add(new StringValueConverter());
        add(new SyscallExecutionBuilderBundleValueConverter());
        add(new SyscallExecutionBuilderValueConverter());
        add(new ThemeValueConverter());
    }

}
