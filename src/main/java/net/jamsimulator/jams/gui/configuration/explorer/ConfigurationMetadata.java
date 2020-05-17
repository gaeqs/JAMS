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

package net.jamsimulator.jams.gui.configuration.explorer;

import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.utils.Validate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ConfigurationMetadata {

	public static final String TYPE_KEY = "type";
	public static final String LANGUAGE_NODE_KEY = "language_node";
	public static final String REGION_KEY = "region";
	public static final String REGIONS_KEY = "regions";

	private final String type;
	private final String languageNode;
	private final String region;
	private final Map<String, Integer> regions;

	public ConfigurationMetadata(Configuration configuration) {
		Validate.notNull(configuration, "Configuration cannot be null!");
		this.type = configuration.getString(TYPE_KEY).orElse(null);
		this.languageNode = configuration.getString(LANGUAGE_NODE_KEY).orElse(null);
		this.region = configuration.getString(REGION_KEY).orElse(null);

		this.regions = new HashMap<>();

		Optional<Configuration> optional = configuration.get(REGIONS_KEY);
		if (!optional.isPresent()) return;

		Configuration regionsConfig = optional.get();
		regionsConfig.getAll(false).forEach((key, value) -> {
			if (value instanceof Number) {
				regions.put(key, ((Number) value).intValue());
			}
		});

	}

	public String getType() {
		return type;
	}

	public String getLanguageNode() {
		return languageNode;
	}

	public String getRegion() {
		return region;
	}

	public Map<String, Integer> getRegions() {
		return regions;
	}
}
