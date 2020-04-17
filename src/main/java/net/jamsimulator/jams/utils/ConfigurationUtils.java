package net.jamsimulator.jams.utils;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.RootConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConfigurationUtils {

	public static final String CONFIGURATION_FOLDER = "configuration";
	public static final String MAIN_CONFIGURATION = "config.jconfig";
	public static final String DEFAULT_MAIN_CONFIGURATION_PATH = "/configuration/main_config.jconfig";

	private static File configurationFolder = null;

	public static RootConfiguration loadMainConfiguration() {

		File file = new File(getConfigurationFolder(), MAIN_CONFIGURATION);

		if (!file.exists()) {
			if (!FolderUtils.moveFromResources(Jams.class, DEFAULT_MAIN_CONFIGURATION_PATH, file))
				throw new RuntimeException("Couldn't create main configuration file!");
		}
		if (file.isDirectory()) throw new RuntimeException("Couldn't create main configuration file! There's " +
				"a directory with the same name!");

		try {
			RootConfiguration config = new RootConfiguration(file);
			RootConfiguration def = new RootConfiguration(new InputStreamReader(
					Jams.class.getResourceAsStream(DEFAULT_MAIN_CONFIGURATION_PATH)));
			config.addNotPresentValues(def);
			config.save(true);
			return config;
		} catch (IOException ex) {
			throw new RuntimeException("Couldn't load main configuration file!", ex);
		}
	}

	public static File getConfigurationFolder() {
		if (configurationFolder == null) {
			configurationFolder = new File(Jams.getMainFolder(), CONFIGURATION_FOLDER);

			if (!FolderUtils.checkFolder(configurationFolder))
				throw new RuntimeException("Couldn't create configuration folder!");
		}
		return configurationFolder;
	}
}
