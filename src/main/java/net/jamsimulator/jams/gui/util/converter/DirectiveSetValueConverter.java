package net.jamsimulator.jams.gui.util.converter;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.mips.directive.set.DirectiveSet;

import java.util.Optional;

public class DirectiveSetValueConverter extends ValueConverter<DirectiveSet> {

    public static final String NAME = "directive_set";

    @Override
    public String toString(DirectiveSet value) {
        return value == null ? null : value.getName();
    }

    @Override
    public Optional<DirectiveSet> fromStringSafe(String value) {
        return Jams.getDirectiveSetManager().get(value);
    }

    @Override
    public Class<?> conversionClass() {
        return DirectiveSet.class;
    }
}
