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

import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.mips.editor.MIPSSpaces;
import net.jamsimulator.jams.gui.mips.simulator.instruction.MIPSAssembledInstructionViewerOrder;
import net.jamsimulator.jams.gui.theme.Theme;
import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.assembler.builder.AssemblerBuilder;
import net.jamsimulator.jams.mips.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.builder.MemoryBuilder;
import net.jamsimulator.jams.mips.memory.cache.CacheBuilder;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;
import net.jamsimulator.jams.mips.syscall.bundle.SyscallExecutionBuilderBundle;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ValueConverters {

    private static final Map<String, ValueConverter<?>> converterByName = new HashMap<>();
    private static final Map<Class<?>, ValueConverter<?>> converterByType = new HashMap<>();

    static {
        //ACTION
        var actionConverter = new ActionValueConverter();
        converterByName.put(ActionValueConverter.NAME, actionConverter);
        converterByType.put(Action.class, actionConverter);

        //ARCHITECTURE
        var architectureConverter = new ArchitectureValueConverter();
        converterByName.put(ArchitectureValueConverter.NAME, architectureConverter);
        converterByType.put(Architecture.class, architectureConverter);

        //BOOLEAN
        var booleanConverter = new BooleanValueConverter();
        converterByName.put(BooleanValueConverter.NAME, booleanConverter);
        converterByType.put(Boolean.class, booleanConverter);
        converterByType.put(boolean.class, booleanConverter);

        //CacheBuilder
        var cacheBuilderConverter = new CacheBuilderValueConverter();
        converterByName.put(CacheBuilderValueConverter.NAME, cacheBuilderConverter);
        converterByType.put(CacheBuilder.class, cacheBuilderConverter);

        //DOUBLE
        var doubleConverter = new DoubleValueConverter();
        converterByName.put(DoubleValueConverter.NAME, doubleConverter);
        converterByType.put(Double.class, doubleConverter);
        converterByType.put(double.class, doubleConverter);

        //FLOAT
        var floatConverter = new FloatValueConverter();
        converterByName.put(FloatValueConverter.NAME, floatConverter);
        converterByType.put(Float.class, floatConverter);
        converterByType.put(float.class, floatConverter);

        //INTEGER
        var integerConverter = new IntegerValueConverter();
        converterByName.put(IntegerValueConverter.NAME, integerConverter);
        converterByType.put(Integer.class, integerConverter);
        converterByType.put(int.class, integerConverter);

        //LANGUAGES
        var languageConverter = new LanguageValueConverter();
        converterByName.put(LanguageValueConverter.NAME, languageConverter);
        converterByType.put(Language.class, languageConverter);

        //MEMORY BUILDER
        var memoryBuilderConverter = new MemoryBuilderValueConverter();
        converterByName.put(MemoryBuilderValueConverter.NAME, memoryBuilderConverter);
        converterByType.put(MemoryBuilder.class, memoryBuilderConverter);

        //MIPS SPACE
        var mipsSpacesConverter = new MIPSSpacesValueConverter();
        converterByName.put(MIPSSpacesValueConverter.NAME, mipsSpacesConverter);
        converterByType.put(MIPSSpaces.class, mipsSpacesConverter);

        //STRING
        var stringConverter = new StringValueConverter();
        converterByName.put(StringValueConverter.NAME, stringConverter);
        converterByType.put(String.class, stringConverter);

        //SYSCALL EXECUTION BUILDER
        var syscallExecutionBuilderConverter = new SyscallExecutionBuilderValueConverter();
        converterByName.put(SyscallExecutionBuilderValueConverter.NAME, syscallExecutionBuilderConverter);
        converterByType.put(SyscallExecutionBuilder.class, syscallExecutionBuilderConverter);

        //SYSCALL EXECUTION BUILDER BUNDLE
        var syscallExecutionBuilderBundleConverter = new SyscallExecutionBuilderBundleValueConverter();
        converterByName.put(SyscallExecutionBuilderBundleValueConverter.NAME, syscallExecutionBuilderBundleConverter);
        converterByType.put(SyscallExecutionBuilderBundle.class, syscallExecutionBuilderBundleConverter);

        //THEME
        var themeConverter = new ThemeValueConverter();
        converterByName.put(ThemeValueConverter.NAME, themeConverter);
        converterByType.put(Theme.class, themeConverter);

        //MIPS COMPILED INSTRUCTION VIEWER ORDER
        var compiledViewerConverter = new MIPSAssembledInstructionViewerOrderValueConverter();
        converterByName.put(MIPSAssembledInstructionViewerOrderValueConverter.NAME, compiledViewerConverter);
        converterByType.put(MIPSAssembledInstructionViewerOrder.class, compiledViewerConverter);

        // MIPS INSTRUCTION SET
        var instructionSetConverter = new InstructionSetValueConverter();
        converterByName.put(InstructionSetValueConverter.NAME, instructionSetConverter);
        converterByType.put(InstructionSet.class, instructionSetConverter);

        // MIPS DIRECTIVE SET
        var directiveSetConverter = new DirectiveSetValueConverter();
        converterByName.put(DirectiveSetValueConverter.NAME, directiveSetConverter);
        converterByType.put(DirectiveSet.class, directiveSetConverter);

        // MIPS REGISTERS BUILDER
        var registersBuilderConverter = new RegistersBuilderValueConverter();
        converterByName.put(RegistersBuilderValueConverter.NAME, registersBuilderConverter);
        converterByType.put(RegistersBuilder.class, registersBuilderConverter);

        // MIPS ASSEMBLER BUILDER
        var assemblerBuilderConverter = new AssemblerBuilderValueConverter();
        converterByName.put(AssemblerBuilderValueConverter.NAME, assemblerBuilderConverter);
        converterByType.put(AssemblerBuilder.class, assemblerBuilderConverter);
    }


    public static Optional<ValueConverter<?>> getByName(String name) {
        if (name == null) return Optional.empty();
        return Optional.ofNullable(converterByName.get(name.toLowerCase()));
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<ValueConverter<T>> getByType(Class<T> clazz) {
        if (clazz == null) return Optional.empty();
        return Optional.ofNullable((ValueConverter<T>) converterByType.get(clazz));
    }

    public static <T> ValueConverter<T> getByTypeUnsafe(Class<T> clazz) {
        return getByType(clazz).orElse(null);
    }

    public static boolean add(Class<?> clazz, ValueConverter<?> converter) {
        return converterByType.putIfAbsent(clazz, converter) == null;
    }
}
