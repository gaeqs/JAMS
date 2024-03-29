/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.utils;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.AttachmentConfiguration;
import net.jamsimulator.jams.configuration.ConfigurationAttachment;
import net.jamsimulator.jams.configuration.MainConfiguration;
import net.jamsimulator.jams.configuration.RootConfiguration;
import net.jamsimulator.jams.configuration.format.ConfigurationFormatJSON;
import net.jamsimulator.jams.manager.ResourceProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConfigurationUtils {

    public static final String CONFIGURATION_FOLDER = "configuration";
    public static final String MAIN_CONFIGURATION = "config.json";
    public static final String DEFAULT_MAIN_CONFIGURATION_PATH = "/configuration/main_config.json";
    public static final String MAIN_CONFIGURATION_META_PATH = "/configuration/main_config_meta.json";

    private static File configurationFolder = null;


    public static MainConfiguration loadMainConfiguration() {
        var mainConfiguration = new MainConfiguration(loadMainConfigurationData(),
                new AttachmentConfiguration());
        mainConfiguration.metadata().addAttachment(new ConfigurationAttachment(
                ResourceProvider.JAMS,
                loadMainConfigurationMetadata(),
                0
        ));
        return mainConfiguration;
    }

    private static RootConfiguration loadMainConfigurationData() {

        File file = new File(getConfigurationFolder(), MAIN_CONFIGURATION);

        if (!file.exists()) {
            if (!FolderUtils.moveFromResources(Jams.class, DEFAULT_MAIN_CONFIGURATION_PATH, file))
                throw new RuntimeException("Couldn't create main configuration file!");
        }
        if (file.isDirectory()) throw new RuntimeException("Couldn't create main configuration file! There's " +
                "a directory with the same name!");

        // We can't use managers yet!
        var format = ConfigurationFormatJSON.INSTANCE;

        RootConfiguration config;
        try {
            config = new RootConfiguration(file, format);
        } catch (IOException ex) {
            throw new RuntimeException("Couldn't load main configuration file!", ex);
        }

        try (var resource = Jams.class.getResourceAsStream(DEFAULT_MAIN_CONFIGURATION_PATH)) {
            if (resource != null) {
                var reader = new InputStreamReader(resource);
                var defaultConfiguration = new RootConfiguration(reader, format);
                config.addNotPresentValues(defaultConfiguration);
                config.save(format, true);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Couldn't load default main configuration file!", ex);
        }

        return config;
    }

    private static RootConfiguration loadMainConfigurationMetadata() {
        // We can't use managers yet!
        var format = ConfigurationFormatJSON.INSTANCE;
        try (var resource = Jams.class.getResourceAsStream(MAIN_CONFIGURATION_META_PATH)) {
            if (resource != null) {
                var reader = new InputStreamReader(resource);
                return new RootConfiguration(reader, format);
            }
            throw new RuntimeException("Couldn't load main configuration metadata file!");
        } catch (IOException ex) {
            throw new RuntimeException("Couldn't load main configuration metadata file!", ex);
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
