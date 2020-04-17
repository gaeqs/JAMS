package net.jamsimulator.jams.gui.configuration.explorer.section;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ConfigurationWindowSpecialSectionBuilders {

	private static final Map<String, ConfigurationWindowSpecialSectionBuilder> builderByName = new HashMap<>();

	static {
		//ACTIONS
		builderByName.put("action", new ConfigurationWindowSectionActions.Builder());
	}


	public static Optional<ConfigurationWindowSpecialSectionBuilder> getByName(String name) {
		if (name == null) return Optional.empty();
		return Optional.ofNullable(builderByName.get(name.toLowerCase()));
	}

	public static boolean addByName(String name, ConfigurationWindowSpecialSectionBuilder builder) {
		return builderByName.putIfAbsent(name.toLowerCase(), builder) == null;
	}
}
