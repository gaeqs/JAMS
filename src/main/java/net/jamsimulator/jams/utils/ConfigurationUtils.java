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
import net.jamsimulator.jams.configuration.RootConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

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
                    Objects.requireNonNull(Jams.class.getResourceAsStream(DEFAULT_MAIN_CONFIGURATION_PATH))));
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
