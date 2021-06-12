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

package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.project.ProjectData;
import net.jamsimulator.jams.project.ProjectType;
import net.jamsimulator.jams.project.event.ProjectTypeRegisterEvent;
import net.jamsimulator.jams.project.event.ProjectTypeUnregisterEvent;
import net.jamsimulator.jams.project.mips.MIPSProjectType;
import net.jamsimulator.jams.utils.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * This singleton stores all {@link ProjectType}s that projects may use.
 * <p>
 * To register an {@link ProjectType} use {@link #add(Object)}.
 * To unregister an {@link ProjectType} use {@link #remove(Object)}.
 * An {@link ProjectType}'s removal from the manager doesn't make editors to stop using
 * it inmediatelly.
 */
public class ProjectTypeManager extends Manager<ProjectType<?>> {

    public static final ProjectTypeManager INSTANCE = new ProjectTypeManager();

    /**
     * Creates the manager.
     */
    public ProjectTypeManager() {
        super(ProjectTypeRegisterEvent.Before::new, ProjectTypeRegisterEvent.After::new,
                ProjectTypeUnregisterEvent.Before::new, ProjectTypeUnregisterEvent.After::new);
    }

    public Optional<ProjectType<?>> getByProjectfolder(File folder) {
        var metadataFolder = new File(folder, ProjectData.METADATA_FOLDER_NAME);
        if (!metadataFolder.isDirectory()) return Optional.empty();
        var dataFile = new File(metadataFolder, ProjectData.METADATA_DATA_NAME);
        if (!dataFile.isFile()) return Optional.empty();

        try {
            var data = new JSONObject(FileUtils.readAll(dataFile));
            var type = data.get("type");
            if (type == null) return Optional.empty();
            return get(type.toString());
        } catch (IOException | JSONException e) {
            return Optional.empty();
        }
    }

    @Override
    protected void loadDefaultElements() {
        add(MIPSProjectType.INSTANCE);
    }
}
