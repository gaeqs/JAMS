package net.jamsimulator.jams.project.mips;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.memory.builder.MemoryBuilder;
import net.jamsimulator.jams.utils.Validate;

import java.util.Objects;
import java.util.Optional;

public class MipsSimulationConfiguration {

	protected String name;

	protected Architecture architecture;
	protected MemoryBuilder memoryBuilder;

	public MipsSimulationConfiguration(String name, Architecture architecture, MemoryBuilder memoryBuilder) {
		Validate.notNull(name, "Name cannot be null!");
		Validate.isTrue(!name.isEmpty(), "Name cannot be empty!");
		Validate.notNull(architecture, "Architecture cannot be null!");
		Validate.notNull(memoryBuilder, "Memory builder cannot be null!");
		this.name = name;
		this.architecture = architecture;
		this.memoryBuilder = memoryBuilder;
	}

	public MipsSimulationConfiguration(String name, Configuration configuration) {
		Validate.notNull(name, "Name cannot be null!");
		Validate.isTrue(!name.isEmpty(), "Name cannot be empty!");
		this.name = name;

		Optional<Architecture> archOptional = configuration.getString("architecture").flatMap(Jams.getArchitectureManager()::get);
		architecture = archOptional.orElseGet(() -> Jams.getArchitectureManager().getDefault());

		Optional<MemoryBuilder> memOptional = configuration.getString("memory").flatMap(Jams.getMemoryBuilderManager()::get);
		memoryBuilder = memOptional.orElseGet(() -> Jams.getMemoryBuilderManager().getDefault());

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

	public void save(Configuration configuration, String prefix) {
		configuration.set(prefix + "." + name + ".architecture", architecture.getName());
		configuration.set(prefix + "." + name + ".memory", memoryBuilder.getName());
	}

	@Override
	public String toString() {
		return "MipsSimulationConfiguration{" +
				"name='" + name + '\'' +
				", architecture=" + architecture +
				", memoryBuilder=" + memoryBuilder +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MipsSimulationConfiguration that = (MipsSimulationConfiguration) o;
		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
