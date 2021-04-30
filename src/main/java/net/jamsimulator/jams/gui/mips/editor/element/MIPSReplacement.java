package net.jamsimulator.jams.gui.mips.editor.element;

import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class MIPSReplacement implements Comparable<MIPSReplacement> {

    private final MIPSLine line;
    private final String key;
    private final String value;

    public MIPSReplacement(MIPSLine line, String key, String value) {
        this.line = line;
        this.key = key;
        this.value = value;
    }

    public MIPSLine getLine() {
        return line;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MIPSReplacement that = (MIPSReplacement) o;
        return line == that.line;
    }

    @Override
    public int hashCode() {
        return Objects.hash(line);
    }

    @Override
    public int compareTo(MIPSReplacement o) {
        return Integer.compare(line.getStart(), o.line.getStart());
    }
}
