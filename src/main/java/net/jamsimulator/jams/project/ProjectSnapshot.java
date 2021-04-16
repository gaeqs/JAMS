package net.jamsimulator.jams.project;

import net.jamsimulator.jams.utils.Validate;
import org.json.JSONObject;

import java.util.Objects;

public record ProjectSnapshot(String name, String path) {

    public static ProjectSnapshot of(JSONObject object) {
        return new ProjectSnapshot(
                object.getString("name"),
                object.getString("path")
        );
    }

    public static ProjectSnapshot of(Project project) {
        return new ProjectSnapshot(
                project.getName(),
                project.getFolder().getAbsolutePath()
        );
    }

    public ProjectSnapshot {
        Validate.notNull(name, "Name cannot be null!");
        Validate.notNull(path, "Path cannot be null!");
    }

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
