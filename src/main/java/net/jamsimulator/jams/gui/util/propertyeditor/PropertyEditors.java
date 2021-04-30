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
