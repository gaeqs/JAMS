/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.gui.configuration.explorer.node;

import javafx.scene.text.Font;
import net.jamsimulator.jams.gui.theme.Theme;
import net.jamsimulator.jams.language.Language;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ConfigurationWindowNodeBuilders {

	private static final Map<String, ConfigurationWindowNodeBuilder<?>> builderByName = new HashMap<>();
	private static final Map<Class<?>, ConfigurationWindowNodeBuilder<?>> buildersByType = new HashMap<>();

	static {
		//BOOLEAN
		ConfigurationWindowNodeBoolean.Builder booleanBuilder = new ConfigurationWindowNodeBoolean.Builder();
		builderByName.put("boolean", booleanBuilder);
		buildersByType.put(boolean.class, booleanBuilder);
		buildersByType.put(Boolean.class, booleanBuilder);

		//STRING
		ConfigurationWindowNodeString.Builder stringBuilder = new ConfigurationWindowNodeString.Builder();
		builderByName.put("string", stringBuilder);
		buildersByType.put(String.class, stringBuilder);

		//LANGUAGES
		ConfigurationWindowNodeLanguage.Builder languageBuilder = new ConfigurationWindowNodeLanguage.Builder();
		builderByName.put("language", languageBuilder);
		buildersByType.put(Language.class, languageBuilder);

		//THEMES
		ConfigurationWindowNodeTheme.Builder themeBuilder = new ConfigurationWindowNodeTheme.Builder();
		builderByName.put("theme", themeBuilder);
		buildersByType.put(Theme.class, themeBuilder);

		//FONTS
		ConfigurationWindowNodeFont.Builder fontBuilder = new ConfigurationWindowNodeFont.Builder();
		builderByName.put("font", fontBuilder);
		buildersByType.put(Font.class, fontBuilder);
	}


	public static Optional<ConfigurationWindowNodeBuilder<?>> getByName(String name) {
		if (name == null) return Optional.empty();
		return Optional.ofNullable(builderByName.get(name.toLowerCase()));
	}

	public static Optional<ConfigurationWindowNodeBuilder<?>> getByType(Class<?> clazz) {
		if (clazz == null) return Optional.empty();
		return Optional.ofNullable(buildersByType.get(clazz));
	}

	public static boolean addByName(String name, ConfigurationWindowNodeBuilder<?> builder) {
		return builderByName.putIfAbsent(name.toLowerCase(), builder) == null;
	}

	public static boolean addByType(Class<?> clazz, ConfigurationWindowNodeBuilder<?> builder) {
		return buildersByType.putIfAbsent(clazz, builder) == null;
	}
}
