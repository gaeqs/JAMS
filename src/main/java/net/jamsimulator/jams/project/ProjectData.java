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

package net.jamsimulator.jams.project;

import net.jamsimulator.jams.configuration.RootConfiguration;
import net.jamsimulator.jams.configuration.format.ConfigurationFormat;
import net.jamsimulator.jams.configuration.format.ConfigurationFormatJSON;
import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.utils.FolderUtils;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.io.IOException;

public abstract class ProjectData extends SimpleEventBroadcast {

    public static final String METADATA_FOLDER_NAME = ".jams";
    public static final String FILES_FOLDER_NAME = "Simulation files";
    public static final String METADATA_DATA_NAME = "data.json";

    public static final String NAME_NODE = "name";
    public static final String TYPE_NODE = "type";

    protected final ProjectType<?> type;

    protected final File metadataFolder;
    protected final File filesFolder;
    protected boolean loaded;
    protected RootConfiguration data;

    protected String name;

    public ProjectData(ProjectType<?> type, File projectFolder) {
        Validate.notNull(type, "Type cannot be null!");
        Validate.notNull(projectFolder, "Folder cannot be null!");
        this.type = type;
        metadataFolder = new File(projectFolder, METADATA_FOLDER_NAME);
        if (!FolderUtils.checkFolder(metadataFolder)) {
            throw new RuntimeException("Couldn't create data folder!");
        }
        filesFolder = new File(projectFolder, FILES_FOLDER_NAME);
        if (!FolderUtils.checkFolder(filesFolder)) {
            throw new RuntimeException("Couldn't create files folder!");
        }
    }

    public ProjectType<?> getType() {
        return type;
    }

    public File getMetadataFolder() {
        return metadataFolder;
    }

    public File getFilesFolder() {
        return filesFolder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void save() {
        data.set(NAME_NODE, name);
        data.set(TYPE_NODE, type.getName());
        try {
            data.save(Manager.of(ConfigurationFormat.class).getOrNull(ConfigurationFormatJSON.NAME), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        if (loaded) return;
        loaded = true;
        try {
            data = new RootConfiguration(
                    new File(metadataFolder, METADATA_DATA_NAME),
                    Manager.of(ConfigurationFormat.class).getOrNull(ConfigurationFormatJSON.NAME)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        name = data.getString(NAME_NODE).orElse(metadataFolder.getParentFile().getName());
    }
}
