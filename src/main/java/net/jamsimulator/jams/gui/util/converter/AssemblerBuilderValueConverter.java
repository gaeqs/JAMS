package net.jamsimulator.jams.gui.util.converter;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.mips.assembler.builder.AssemblerBuilder;

import java.util.Optional;

public class AssemblerBuilderValueConverter extends ValueConverter<AssemblerBuilder> {

    public static final String NAME = "assembler_builder";

    @Override
    public String toString(AssemblerBuilder value) {
        return value == null ? null : value.getName();
    }

    @Override
    public Optional<AssemblerBuilder> fromStringSafe(String value) {
        return Jams.getAssemblerBuilderManager().get(value);
    }

    @Override
    public Class<?> conversionClass() {
        return AssemblerBuilder.class;
    }
}
