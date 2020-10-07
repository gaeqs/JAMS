package net.jamsimulator.jams.mips.syscall.bundle;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;
import net.jamsimulator.jams.utils.Validate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SyscallExecutionBuilderBundle {

	private final String name;
	private final Map<Integer, String> builders;

	public SyscallExecutionBuilderBundle(String name) {
		Validate.notNull(name, "Name cannot be null!");
		this.name = name;
		this.builders = new HashMap<>();
	}

	public SyscallExecutionBuilderBundle(String name, Map<Integer, String> builders) {
		Validate.notNull(name, "Name cannot be null!");
		Validate.notNull(builders, "Builders cannot be null!");
		this.name = name;
		this.builders = new HashMap<>(builders);
	}

	public String getName() {
		return name;
	}

	public void addBuilder(int id, String name) {
		Validate.notNull(name, "Name cannot be null!");
		builders.put(id, name);
	}

	public void removeBuilder(int id) {
		builders.remove(id);
	}

	public Map<Integer, SyscallExecutionBuilder<?>> buildBundle() {
		var map = new HashMap<Integer, SyscallExecutionBuilder<?>>();

		builders.forEach((id, name) ->
				Jams.getSyscallExecutionBuilderManager().get(name).ifPresent(builder -> map.put(id, builder)));

		return map;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SyscallExecutionBuilderBundle that = (SyscallExecutionBuilderBundle) o;
		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
