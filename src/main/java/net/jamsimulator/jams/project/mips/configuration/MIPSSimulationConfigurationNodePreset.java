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

package net.jamsimulator.jams.project.mips.configuration;

import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.utils.Validate;

import java.util.*;

public class MIPSSimulationConfigurationNodePreset {

    private final String name;
    private final Class<?> type;

    private final int priority;
    private final String languageNode;

    private final Object defaultValue;

    private final Set<Architecture> supportedArchitectures;
    private final boolean supportsAllArchitectures;

    private final Map<String, Object[]> dependencies;


    public MIPSSimulationConfigurationNodePreset(String name, Class<?> type, int priority, String languageNode, Object defaultValue, Map<String, Object[]> dependencies) {
        Validate.notNull(name, "Name cannot be null!");
        Validate.notNull(type, "Type cannot be null!");
        Validate.notNull(languageNode, "Language node cannot be null!");
        Validate.notNull(defaultValue, "Default value cannot be null!");
        Validate.isTrue(type.isInstance(defaultValue), "Type doesn't match default value! "
                + defaultValue
                + " (" + defaultValue.getClass() + ")"
                + " -> " + type);
        this.name = name;
        this.type = type;
        this.priority = priority;
        this.languageNode = languageNode;

        this.defaultValue = defaultValue;

        this.supportedArchitectures = null;
        this.supportsAllArchitectures = true;
        this.dependencies = new HashMap<>();

        if (dependencies != null) {
            dependencies.forEach((key, value) -> {
                Object[] array = new Object[value.length];
                System.arraycopy(value, 0, array, 0, array.length);
                this.dependencies.put(key, array);
            });
        }
    }

    public MIPSSimulationConfigurationNodePreset(String name, Class<?> type, int priority, String languageNode, Object defaultValue, Set<Architecture> supportedArchitectures, Map<String, Object[]> dependencies) {
        Validate.notNull(name, "Name cannot be null!");
        Validate.notNull(type, "Type cannot be null!");
        Validate.notNull(languageNode, "Language node cannot be null!");
        Validate.notNull(defaultValue, "Default value cannot be null!");
        Validate.isTrue(type.isInstance(defaultValue), "Type doesn't match default value!");
        this.name = name;
        this.type = type;
        this.priority = priority;
        this.languageNode = languageNode;

        this.defaultValue = defaultValue;

        this.supportedArchitectures = supportedArchitectures == null ? new HashSet<>() : new HashSet<>(supportedArchitectures);
        this.supportsAllArchitectures = false;
        this.dependencies = new HashMap<>();

        if (dependencies != null) {
            dependencies.forEach((key, value) -> {
                Object[] array = new Object[value.length];
                System.arraycopy(value, 0, array, 0, array.length);
                this.dependencies.put(key, array);
            });
        }
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public int getPriority() {
        return priority;
    }

    public String getLanguageNode() {
        return languageNode;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public boolean supportArchitecture(Architecture architecture) {
        return supportsAllArchitectures || supportedArchitectures.contains(architecture);
    }

    public void addSupportForArchitecture(Architecture architecture) {
        if (supportsAllArchitectures) return;
        supportedArchitectures.add(architecture);
    }

    public boolean supportsNode(String node, Object state) {
        if (dependencies.isEmpty()) return true;
        Object[] array = dependencies.get(node);
        if (array == null) return true;

        for (Object o : array) {
            if (o.equals(state)) return true;
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MIPSSimulationConfigurationNodePreset that = (MIPSSimulationConfigurationNodePreset) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
