package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.project.ProjectData;
import net.jamsimulator.jams.project.ProjectType;
import net.jamsimulator.jams.project.event.ProjectTypeRegisterEvent;
import net.jamsimulator.jams.project.event.ProjectTypeUnregisterEvent;
import net.jamsimulator.jams.project.mips.MIPSProjectType;
import net.jamsimulator.jams.utils.FileUtils;
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
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    protected void loadDefaultElements() {
        add(MIPSProjectType.INSTANCE);
    }
}
