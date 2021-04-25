package net.jamsimulator.jams.gui.util.converter;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;

import java.util.Optional;

public class RegistersBuilderValueConverter extends ValueConverter<RegistersBuilder> {

    public static final String NAME = "registers_builder";

    @Override
    public String toString(RegistersBuilder value) {
        return value == null ? null : value.getName();
    }

    @Override
    public Optional<RegistersBuilder> fromStringSafe(String value) {
        return Jams.getRegistersBuilderManager().get(value);
    }

    @Override
    public Class<?> conversionClass() {
        return RegistersBuilder.class;
    }
}
