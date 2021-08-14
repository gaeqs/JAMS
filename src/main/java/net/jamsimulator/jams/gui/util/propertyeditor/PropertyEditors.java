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

package net.jamsimulator.jams.gui.util.propertyeditor;

import javafx.beans.property.Property;
import net.jamsimulator.jams.mips.assembler.builder.AssemblerBuilder;
import net.jamsimulator.jams.mips.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.builder.MemoryBuilder;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;
import net.jamsimulator.jams.utils.Validate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class PropertyEditors {

    private static final Map<Class<?>, Function<Property<?>, PropertyEditor<?>>> EDITORS = new HashMap<>();

    static {
        registerEditor(Boolean.class, p -> new BooleanPropertyEditor((Property<Boolean>) p));
        registerEditor(Double.class, p -> new DoublePropertyEditor((Property<Double>) p));
        registerEditor(Float.class, p -> new FloatPropertyEditor((Property<Float>) p));
        registerEditor(Integer.class, p -> new IntegerPropertyEditor((Property<Integer>) p));
        registerEditor(String.class, p -> new StringPropertyEditor((Property<String>) p));
        registerEditor(Enum.class, p -> new EnumPropertyEditor((Property<? super Enum<?>>) p));
        registerEditor(MemoryBuilder.class, p -> new MemoryBuilderPropertyEditor((Property<MemoryBuilder>) p));
        registerEditor(InstructionSet.class, p -> new InstructionSetPropertyEditor((Property<InstructionSet>) p));
        registerEditor(DirectiveSet.class, p -> new DirectiveSetPropertyEditor((Property<DirectiveSet>) p));
        registerEditor(RegistersBuilder.class, p -> new RegistersBuilderPropertyEditor((Property<RegistersBuilder>) p));
        registerEditor(AssemblerBuilder.class, p -> new AssemblerBuilderPropertyEditor((Property<AssemblerBuilder>) p));
    }

    public static void registerEditor(Class<?> clazz, Function<Property<?>, PropertyEditor<?>> editor) {
        Validate.notNull(editor, "Editor cannot be null!");
        EDITORS.put(clazz, editor);
    }

    public static Optional<PropertyEditor<?>> getEditor(Property<?> property) {
        Class<?> clazz = property.getValue().getClass();
        if (clazz.isEnum()) clazz = Enum.class;

        var editor = EDITORS.get(clazz);
        if (editor != null) return Optional.ofNullable(editor.apply(property));

        // If the property has no direct editor, check its superclasses.
        for (var entry : EDITORS.entrySet()) {
            if (entry.getKey().isInstance(property.getValue())) {
                return Optional.ofNullable(entry.getValue().apply(property));
            }
        }

        return Optional.empty();
    }

}
