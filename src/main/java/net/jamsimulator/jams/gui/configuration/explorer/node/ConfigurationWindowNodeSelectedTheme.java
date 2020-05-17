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

import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.theme.Theme;

public class ConfigurationWindowNodeSelectedTheme extends ConfigurationWindowNodeTheme {

	public ConfigurationWindowNodeSelectedTheme(Configuration configuration, String relativeNode,
												String languageNode, String region, Theme defaultValue) {
		super(configuration, relativeNode, languageNode, region, defaultValue);
	}

	@Override
	protected void saveValue(Theme value) {
		super.saveValue(value);
		JamsApplication.getThemeManager().setSelected(value.getName());
	}

	static class Builder implements ConfigurationWindowNodeBuilder<Theme> {

		@Override
		public ConfigurationWindowNode<Theme> create(Configuration configuration, String relativeNode, String languageNode, String region) {
			return new ConfigurationWindowNodeSelectedTheme(configuration, relativeNode,
					languageNode, region, JamsApplication.getThemeManager().getSelected());
		}
	}
}
