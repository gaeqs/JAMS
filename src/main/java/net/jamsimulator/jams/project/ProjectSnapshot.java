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

import net.jamsimulator.jams.utils.Validate;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Represents an unloaded {@link Project}.
 * This record storages only the name and the path of the represented {@link Project}.
 */
public record ProjectSnapshot(String name, String path) {

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
