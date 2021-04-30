package net.jamsimulator.jams.project;

import net.jamsimulator.jams.utils.Validate;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Represents an unloaded {@link Project}.
 * This record storages only the name and the path of the represented {@link Project}.
 */
public record ProjectSnapshot(String name, String path) {

    /**
     * Creates a new {@link ProjectSnapshot} using the data inside the given {@link JSONObject JSON}.
     *
     * @param object the {@link JSONObject JSON}.
     * @return the new snapshot.
     */
    public static ProjectSnapshot of(JSONObject object) {
        return new ProjectSnapshot(
                object.getString("name"),
                object.getString("path")
        );
    }

    /**
     * Creates a new {@link ProjectSnapshot} using the data of the given {@link Project}.
     *
     * @param project the {@link Project}.
     * @return the new snapshot.
     */
    public static ProjectSnapshot of(Project project) {
        return new ProjectSnapshot(
                project.getName(),
                project.getFolder().getAbsolutePath()
        );
    }

    /**
     * Creates the new project snapshot
     *
     * @param name the name of the project.
     * @param path the path of the project.
     */
    public ProjectSnapshot {
        Validate.notNull(name, "Name cannot be null!");
        Validate.notNull(path, "Path cannot be null!");
    }

    /**
     * Converts this snapshot into a {@link JSONObject JSON}.
     *
     * @return the {@link JSONObject JSON}.
     */
    public JSONObject toJSON() {
        var object = new JSONObject();
        object.put("name", name);
        object.put("path", path);
        return object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectSnapshot that = (ProjectSnapshot) o;
        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
