package net.jamsimulator.jams.gui.util.converter;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;

import java.util.Optional;

public class InstructionSetValueConverter extends ValueConverter<InstructionSet> {

    public static final String NAME = "instruction_set";

    @Override
    public String toString(InstructionSet value) {
        return value == null ? null : value.getName();
    }

    @Override
    public Optional<InstructionSet> fromStringSafe(String value) {
        return Jams.getInstructionSetManager().get(value);
    }

    @Override
    public Class<?> conversionClass() {
        return InstructionSet.class;
    }
}
