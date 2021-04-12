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

package net.jamsimulator.jams.gui.configuration.explorer.section;

import net.jamsimulator.jams.gui.configuration.explorer.section.action.ConfigurationWindowSectionActions;
import net.jamsimulator.jams.gui.configuration.explorer.section.plugin.ConfigurationWindowSectionPlugins;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Storage for {@link ConfigurationWindowSpecialSectionBuilder}s.
 */
public class ConfigurationWindowSpecialSectionBuilders {

	private static final Map<String, ConfigurationWindowSpecialSectionBuilder> builderByName = new HashMap<>();

	static {
		//ACTIONS
		builderByName.put("action", new ConfigurationWindowSectionActions.Builder());
		builderByName.put("plugin", new ConfigurationWindowSectionPlugins.Builder());
	}


	public static Optional<ConfigurationWindowSpecialSectionBuilder> getByName(String name) {
		if (name == null) return Optional.empty();
		return Optional.ofNullable(builderByName.get(name.toLowerCase()));
	}

	public static boolean addByName(String name, ConfigurationWindowSpecialSectionBuilder builder) {
		return builderByName.putIfAbsent(name.toLowerCase(), builder) == null;
	}
}
