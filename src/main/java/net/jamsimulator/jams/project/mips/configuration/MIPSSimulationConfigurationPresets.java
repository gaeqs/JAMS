package net.jamsimulator.jams.project.mips.configuration;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.architecture.PipelinedArchitecture;
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.memory.builder.MemoryBuilder;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

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
		PRESETS.add(new MIPSSimulationConfigurationNodePreset(ARCHITECTURE, Architecture.class, 100,
				Messages.SIMULATION_CONFIGURATION_ARCHITECTURE, SingleCycleArchitecture.INSTANCE, null));
		PRESETS.add(new MIPSSimulationConfigurationNodePreset(MEMORY, MemoryBuilder.class, 100,
				Messages.SIMULATION_CONFIGURATION_MEMORY, Jams.getMemoryBuilderManager().getDefault(), null));
		PRESETS.add(new MIPSSimulationConfigurationNodePreset(CALL_EVENTS, Boolean.class, 90,
				Messages.SIMULATION_CONFIGURATION_CALL_EVENTS, true, null));
		PRESETS.add(new MIPSSimulationConfigurationNodePreset(UNDO_ENABLED, Boolean.class, 89,
				Messages.SIMULATION_CONFIGURATION_ENABLE_UNDO, true,
				Map.of(CALL_EVENTS, new Object[]{true})));
		PRESETS.add(new MIPSSimulationConfigurationNodePreset(FORWARDING_ENABLED, Boolean.class, 80,
				Messages.SIMULATION_CONFIGURATION_ENABLE_FORWARDING, true,
				Set.of(PipelinedArchitecture.INSTANCE), null));
		PRESETS.add(new MIPSSimulationConfigurationNodePreset(BRANCH_ON_DECODE, Boolean.class, 80,
				Messages.SIMULATION_CONFIGURATION_SOLVE_BRANCH_ON_DECODE, true,
				Set.of(PipelinedArchitecture.INSTANCE), null));
		PRESETS.add(new MIPSSimulationConfigurationNodePreset(DELAY_SLOTS_ENABLED, Boolean.class, 79,
				Messages.SIMULATION_CONFIGURATION_ENABLE_DELAY_SLOTS, true,
				Set.of(PipelinedArchitecture.INSTANCE), Map.of(BRANCH_ON_DECODE, new Object[]{true})));
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
