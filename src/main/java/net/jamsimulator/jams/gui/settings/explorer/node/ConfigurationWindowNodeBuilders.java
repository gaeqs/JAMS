package net.jamsimulator.jams.gui.settings.explorer.node;

import net.jamsimulator.jams.language.Language;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ConfigurationWindowNodeBuilders {

	private static final Map<String, ConfigurationWindowNodeBuilder<?>> builderByName = new HashMap<>();
	private static final Map<Class<?>, ConfigurationWindowNodeBuilder<?>> buildersByType = new HashMap<>();

	static {
		ConfigurationWindowNodeBoolean.Builder booleanBuilder = new ConfigurationWindowNodeBoolean.Builder();
		builderByName.put("boolean", booleanBuilder);
		buildersByType.put(boolean.class, booleanBuilder);
		buildersByType.put(Boolean.class, booleanBuilder);

		//LANGUAGES
		ConfigurationWindowNodeLanguage.Builder languageBuilder = new ConfigurationWindowNodeLanguage.Builder();
		builderByName.put("language", languageBuilder);
		buildersByType.put(Language.class, languageBuilder);
		ConfigurationWindowNodeSelectedLanguage.Builder selectedLanguageBuilder = new ConfigurationWindowNodeSelectedLanguage.Builder();
		builderByName.put("selected_language", selectedLanguageBuilder);
	}


	public static Optional<ConfigurationWindowNodeBuilder<?>> getByName(String name) {
		return Optional.ofNullable(builderByName.get(name.toLowerCase()));
	}

	public static Optional<ConfigurationWindowNodeBuilder<?>> getByType(Class<?> clazz) {
		return Optional.ofNullable(buildersByType.get(clazz));
	}

	public static boolean addByName(String name, ConfigurationWindowNodeBuilder<?> builder) {
		return builderByName.putIfAbsent(name.toLowerCase(), builder) == null;
	}

	public static boolean addByType(Class<?> clazz, ConfigurationWindowNodeBuilder<?> builder) {
		return buildersByType.putIfAbsent(clazz, builder) == null;
	}
}
