package net.jamsimulator.jams.project.mips;

import javafx.beans.property.Property;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.memory.builder.MemoryBuilder;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;
import net.jamsimulator.jams.utils.NumericUtils;
import net.jamsimulator.jams.utils.Validate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class MIPSSimulationConfiguration {

	protected String name;

	protected Architecture architecture;
	protected MemoryBuilder memoryBuilder;
	protected boolean callEvents, undoEnabled;
	protected Map<Integer, SyscallExecutionBuilder<?>> syscallExecutionBuilders;

	public MIPSSimulationConfiguration(String name) {
		this(name, Jams.getArchitectureManager().getDefault(), Jams.getMemoryBuilderManager().getDefault(), true, true, new HashMap<>());
	}

	public MIPSSimulationConfiguration(String name, Architecture architecture, MemoryBuilder memoryBuilder,
									   boolean callEvents, boolean undoEnabled, Map<Integer, SyscallExecutionBuilder<?>> syscallExecutionBuilders) {
		Validate.notNull(name, "Name cannot be null!");
		Validate.isTrue(!name.isEmpty(), "Name cannot be empty!");
		Validate.notNull(architecture, "Architecture cannot be null!");
		Validate.notNull(memoryBuilder, "Memory builder cannot be null!");
		this.name = name;
		this.architecture = architecture;
		this.memoryBuilder = memoryBuilder;
		this.callEvents = callEvents;
		this.undoEnabled = undoEnabled;
		this.syscallExecutionBuilders = syscallExecutionBuilders;
	}

	public MIPSSimulationConfiguration(String name, Configuration configuration) {
		Validate.notNull(name, "Name cannot be null!");
		Validate.isTrue(!name.isEmpty(), "Name cannot be empty!");
		this.name = name;

		Optional<Architecture> archOptional = configuration.getString("architecture").flatMap(Jams.getArchitectureManager()::get);
		architecture = archOptional.orElseGet(() -> Jams.getArchitectureManager().getDefault());

		Optional<MemoryBuilder> memOptional = configuration.getString("memory").flatMap(Jams.getMemoryBuilderManager()::get);
		memoryBuilder = memOptional.orElseGet(() -> Jams.getMemoryBuilderManager().getDefault());

		Optional<Boolean> callEventsOptional = configuration.get("call_events");
		callEvents = callEventsOptional.orElse(true);
		Optional<Boolean> undoEnabledOptional = configuration.get("undo_enabled");
		undoEnabled = undoEnabledOptional.orElse(true);

		syscallExecutionBuilders = new HashMap<>();
		Optional<Configuration> syscallsOptional = configuration.get("syscalls");
		if (syscallsOptional.isPresent()) {
			Configuration syscalls = syscallsOptional.get();
			syscalls.getAll(false).forEach((key, value) -> {
				if (!(value instanceof Configuration)) return;
				Configuration config = (Configuration) value;

				if (!NumericUtils.isInteger(key)) return;
				SyscallExecutionBuilder<?> builder = Jams.getSyscallExecutionBuilderManager()
						.get(config.getString("name").orElse("")).orElse(null);
				if (builder == null) return;
				builder = builder.makeNewInstance();

				for (Property property : builder.getProperties()) {
					Object o = config.get(property.getName()).orElse(null);
					if (o == null) continue;
					try {
						property.setValue(o);
					} catch (Exception ignore) {
					}
				}

				syscallExecutionBuilders.put(NumericUtils.decodeInteger(key), builder);
			});
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		Validate.notNull(name, "Name cannot be null!");
		Validate.isTrue(!name.isEmpty(), "Name cannot be empty!");
		this.name = name;
	}

	public Architecture getArchitecture() {
		return architecture;
	}

	public void setArchitecture(Architecture architecture) {
		Validate.notNull(architecture, "Architecture cannot be null!");
		this.architecture = architecture;
	}

	public MemoryBuilder getMemoryBuilder() {
		return memoryBuilder;
	}

	public void setMemoryBuilder(MemoryBuilder memoryBuilder) {
		Validate.notNull(memoryBuilder, "Memory builder cannot be null!");
		this.memoryBuilder = memoryBuilder;
	}

	public Map<Integer, SyscallExecutionBuilder<?>> getSyscallExecutionBuilders() {
		return syscallExecutionBuilders;
	}

	public boolean isCallEvents() {
		return callEvents;
	}

	public void setCallEvents(boolean callEvents) {
		this.callEvents = callEvents;
	}

	public boolean isUndoEnabled() {
		return undoEnabled;
	}

	public void setUndoEnabled(boolean undoEnabled) {
		this.undoEnabled = undoEnabled;
	}

	public void save(Configuration configuration, String prefix) {
		prefix = prefix + "." + name;
		configuration.set(prefix + ".architecture", architecture.getName());
		configuration.set(prefix + ".memory", memoryBuilder.getName());
		configuration.set(prefix + ".call_events", callEvents);
		configuration.set(prefix + ".undo_enabled", undoEnabled);

		configuration.remove(prefix + "." + name + ".syscalls");

		String syscallsPrefix = prefix + ".syscalls.";
		syscallExecutionBuilders.forEach((key, builder) -> {
			configuration.set(syscallsPrefix + key + ".name", builder.getName());
			for (Property<?> property : builder.getProperties()) {
				configuration.set(syscallsPrefix + key + "." + property.getName(), property.getValue());
			}
		});
	}

	@Override
	public String toString() {
		return "MipsSimulationConfiguration{" +
				"name='" + name + '\'' +
				", architecture=" + architecture +
				", memoryBuilder=" + memoryBuilder +
				", callEvents=" + callEvents +
				", syscallExecutionBuilders=" + syscallExecutionBuilders +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MIPSSimulationConfiguration that = (MIPSSimulationConfiguration) o;
		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}