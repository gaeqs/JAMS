package net.jamsimulator.jams.project.mips.configuration;

import javafx.beans.property.Property;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.builder.MemoryBuilder;
import net.jamsimulator.jams.mips.memory.cache.CacheBuilder;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;
import net.jamsimulator.jams.mips.syscall.bundle.SyscallExecutionBuilderBundle;
import net.jamsimulator.jams.mips.syscall.bundle.defaults.MARSSyscallExecutionBuilderBundle;
import net.jamsimulator.jams.utils.NumericUtils;
import net.jamsimulator.jams.utils.Validate;

import java.util.*;

public class MIPSSimulationConfiguration {

	protected String name;

	protected Map<MIPSSimulationConfigurationNodePreset, Object> nodes;
	protected Map<String, Object> rawValues;

	protected List<CacheBuilder<?>> cacheBuilders;
	protected Map<Integer, SyscallExecutionBuilder<?>> syscallExecutionBuilders;

	public MIPSSimulationConfiguration(String name) {
		this(name, new ArrayList<>(), Jams.getSyscallExecutionBuilderManager()
				.getBundle(MARSSyscallExecutionBuilderBundle.NAME)
				.map(SyscallExecutionBuilderBundle::buildBundle).orElseGet(HashMap::new));
	}

	public MIPSSimulationConfiguration(String name, List<CacheBuilder<?>> cacheBuilders, Map<Integer, SyscallExecutionBuilder<?>> syscallExecutionBuilders) {
		Validate.notNull(name, "Name cannot be null!");
		Validate.isTrue(!name.isEmpty(), "Name cannot be empty!");

		this.name = name;

		this.nodes = new HashMap<>();
		this.rawValues = new HashMap<>();

		MIPSSimulationConfigurationPresets.forEachPreset(preset -> nodes.put(preset, preset.getDefaultValue()));

		this.cacheBuilders = cacheBuilders;
		this.syscallExecutionBuilders = syscallExecutionBuilders;
	}

	public MIPSSimulationConfiguration(String name, MIPSSimulationConfiguration configuration) {
		Validate.notNull(name, "Name cannot be null!");
		Validate.notNull(configuration, "Configuration cannot be null!");
		this.name = name;

		this.nodes = new HashMap<>(configuration.nodes);
		this.rawValues = new HashMap<>();

		this.cacheBuilders = new ArrayList<>();
		configuration.cacheBuilders.forEach(cache -> cacheBuilders.add(cache.copy()));

		this.syscallExecutionBuilders = new HashMap<>();
		configuration.syscallExecutionBuilders.forEach((key, builder) -> syscallExecutionBuilders.put(key, builder.copy()));
	}

	public MIPSSimulationConfiguration(String name, Configuration configuration) {
		Validate.notNull(name, "Name cannot be null!");
		Validate.isTrue(!name.isEmpty(), "Name cannot be empty!");
		this.name = name;

		this.nodes = new HashMap<>();
		this.rawValues = new HashMap<>();

		Configuration nodesConfiguration = configuration.getOrCreateConfiguration("node");
		nodesConfiguration.getAll(false).forEach((key, value) -> {
			var preset = MIPSSimulationConfigurationPresets.getPreset(key).orElse(null);
			if (preset != null) {
				var optional = nodesConfiguration.getAndConvert(preset.getName(), preset.getType());
				nodes.put(preset, optional.orElseGet(preset::getDefaultValue));
			} else {
				System.out.println("Raw value " + key + " found. Value: " + value);
				rawValues.put(key, value);
			}
		});

		loadSyscalls(configuration);
		loadCaches(configuration);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		Validate.notNull(name, "Name cannot be null!");
		Validate.isTrue(!name.isEmpty(), "Name cannot be empty!");
		this.name = name;
	}

	public Map<MIPSSimulationConfigurationNodePreset, Object> getNodes() {
		return new HashMap<>(nodes);
	}

	public <T> T getNodeValue(String node) {
		var preset = MIPSSimulationConfigurationPresets.getPreset(node);
		try {
			if (preset.isEmpty()) {
				return (T) rawValues.get(node);
			}

			if (rawValues.containsKey(node)) {
				Object value = rawValues.remove(node);

				if (!preset.get().getType().isInstance(value)) {
					var optional = ValueConverters.getByType(preset.get().getType());

					if (optional.isPresent()) {
						value = optional.get().fromString(value.toString());
						if (value == null) value = preset.get().getDefaultValue();
					} else {
						value = preset.get().getDefaultValue();
					}
				}

				nodes.put(preset.get(), value);
			}

			return (T) nodes.getOrDefault(preset.get(), preset.get().getDefaultValue());
		} catch (ClassCastException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public boolean setNodeValue(String node, Object value) {
		var preset = MIPSSimulationConfigurationPresets.getPreset(node);
		if (preset.isEmpty()) {
			rawValues.put(node, value);
		} else {
			if (!preset.get().getType().isInstance(value)) return false;
			nodes.put(preset.get(), value);
		}
		return true;
	}

	public List<CacheBuilder<?>> getCacheBuilders() {
		return cacheBuilders;
	}

	public Memory generateNewMemory() {
		MemoryBuilder builder = getNodeValue("memory");
		Memory current = builder.createMemory();
		ListIterator<CacheBuilder<?>> iterator = cacheBuilders.listIterator(cacheBuilders.size());
		while (iterator.hasPrevious()) {
			current = iterator.previous().build(current);
		}

		return current;
	}

	public Map<Integer, SyscallExecutionBuilder<?>> getSyscallExecutionBuilders() {
		return syscallExecutionBuilders;
	}

	public void save(Configuration configuration, String prefix) {
		prefix = prefix + "." + name;

		String nodePrefix = prefix + ".node.";


		rawValues.forEach((key, value) -> configuration.set(nodePrefix + key, value));
		nodes.forEach((key, value) -> configuration.convertAndSet(nodePrefix + key.getName(), value, key.getType()));

		configuration.remove(prefix + "." + name + ".syscalls");
		String syscallsPrefix = prefix + ".syscalls.";
		syscallExecutionBuilders.forEach((key, builder) -> {
			configuration.set(syscallsPrefix + key + ".name", builder.getName());
			for (Property<?> property : builder.getProperties()) {
				configuration.set(syscallsPrefix + key + "." + property.getName(), property.getValue());
			}
		});

		configuration.remove(prefix + "." + name + ".caches");
		String cachesPrefix = prefix + ".caches.";
		int index = 0;
		for (CacheBuilder<?> builder : cacheBuilders) {
			configuration.set(cachesPrefix + index + ".name", builder.getName());
			for (Property<?> property : builder.getProperties()) {
				configuration.set(cachesPrefix + index + "." + property.getName(), property.getValue());
			}
			index++;
		}
	}

	@Override
	public String toString() {
		return "MIPSSimulationConfiguration{" +
				"name='" + name + '\'' +
				", presets=" + nodes +
				", rawValues=" + rawValues +
				", cacheBuilders=" + cacheBuilders +
				", syscallExecutionBuilders=" + syscallExecutionBuilders +
				'}';
	}

	private void loadSyscalls(Configuration configuration) {
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
						if (property.getValue() instanceof Enum) {
							property.setValue(Enum.valueOf((Class<Enum>) property.getValue().getClass(), o.toString()));
						} else {
							property.setValue(o);
						}
					} catch (Exception ignore) {
					}
				}

				syscallExecutionBuilders.put(NumericUtils.decodeInteger(key), builder);
			});
		}
	}

	public void loadCaches(Configuration configuration) {
		cacheBuilders = new ArrayList<>();
		Optional<Configuration> cachesOptional = configuration.get("caches");
		if (cachesOptional.isPresent()) {
			Configuration caches = cachesOptional.get();
			int index = 0;
			Optional<?> indexOptional;
			while ((indexOptional = caches.get(String.valueOf(index))).isPresent()) {
				if (!(indexOptional.get() instanceof Configuration)) return;

				Configuration config = (Configuration) indexOptional.get();

				CacheBuilder<?> builder = Jams.getCacheBuilderManager()
						.get(config.getString("name").orElse("")).orElse(null);
				if (builder == null) return;
				builder = builder.makeNewInstance();

				for (Property property : builder.getProperties()) {
					Object o = config.get(property.getName()).orElse(null);
					if (o == null) continue;
					try {
						if (property.getValue() instanceof Enum) {
							property.setValue(Enum.valueOf((Class<Enum>) property.getValue().getClass(), o.toString()));
						} else {
							property.setValue(o);
						}
					} catch (Exception ignore) {
					}
				}

				cacheBuilders.add(builder);

				index++;
			}
		}
	}
}
