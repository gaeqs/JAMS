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

import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.architecture.MultiAPUPipelinedArchitecture;
import net.jamsimulator.jams.mips.architecture.PipelinedArchitecture;
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.memory.builder.MemoryBuilder;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MIPSSimulationConfigurationPresets {

    public static final String ARCHITECTURE = "architecture";
    public static final String MEMORY = "memory";
    public static final String CALL_EVENTS = "call_events";
    public static final String UNDO_ENABLED = "undo_enabled";
    public static final String FORWARDING_ENABLED = "forwarding_enabled";
    public static final String BRANCH_ON_DECODE = "branch_on_decode";
    public static final String DELAY_SLOTS_ENABLED = "delay_slots_enabled";

    private final static Set<MIPSSimulationConfigurationNodePreset> PRESETS = new HashSet<>();

    static {
        var architectureManager = Manager.of(Architecture.class);
        var pipelinedArchitectures = architectureManager.stream()
                .filter(it -> it.getName().equals(PipelinedArchitecture.NAME)
                        || it.getName().equals(MultiAPUPipelinedArchitecture.NAME))
                .collect(Collectors.toSet());

        PRESETS.add(new MIPSSimulationConfigurationNodePreset(ARCHITECTURE, Architecture.class, 100,
                Messages.SIMULATION_CONFIGURATION_ARCHITECTURE, Manager.of(Architecture.class).getOrNull(SingleCycleArchitecture.NAME), null));
        PRESETS.add(new MIPSSimulationConfigurationNodePreset(MEMORY, MemoryBuilder.class, 100,
                Messages.SIMULATION_CONFIGURATION_MEMORY, Manager.ofD(MemoryBuilder.class).getDefault(), null));
        PRESETS.add(new MIPSSimulationConfigurationNodePreset(CALL_EVENTS, Boolean.class, 90,
                Messages.SIMULATION_CONFIGURATION_CALL_EVENTS, true, null));
        PRESETS.add(new MIPSSimulationConfigurationNodePreset(UNDO_ENABLED, Boolean.class, 89,
                Messages.SIMULATION_CONFIGURATION_ENABLE_UNDO, true,
                Map.of(CALL_EVENTS, new Object[]{true})));
        PRESETS.add(new MIPSSimulationConfigurationNodePreset(FORWARDING_ENABLED, Boolean.class, 80,
                Messages.SIMULATION_CONFIGURATION_ENABLE_FORWARDING, true,
                pipelinedArchitectures, null));
        PRESETS.add(new MIPSSimulationConfigurationNodePreset(BRANCH_ON_DECODE, Boolean.class, 80,
                Messages.SIMULATION_CONFIGURATION_SOLVE_BRANCH_ON_DECODE, true,
                pipelinedArchitectures, null));
        PRESETS.add(new MIPSSimulationConfigurationNodePreset(DELAY_SLOTS_ENABLED, Boolean.class, 79,
                Messages.SIMULATION_CONFIGURATION_ENABLE_DELAY_SLOTS, false,
                pipelinedArchitectures, Map.of(BRANCH_ON_DECODE, new Object[]{true})));
    }

    public static Set<MIPSSimulationConfigurationNodePreset> getPresets() {
        return new HashSet<>(PRESETS);
    }

    public static void forEachPreset(Consumer<MIPSSimulationConfigurationNodePreset> consumer) {
        PRESETS.forEach(consumer);
    }

    public static Optional<MIPSSimulationConfigurationNodePreset> getPreset(String name) {
        return PRESETS.stream().filter(target -> target.getName().equals(name)).findAny();
    }

    public static boolean addPreset(MIPSSimulationConfigurationNodePreset preset) {
        return PRESETS.add(preset);
    }


}
