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

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.project.event.RecentProjectAddEvent;
import net.jamsimulator.jams.utils.FileUtils;
import net.jamsimulator.jams.utils.Validate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * The instance of this class stores the recent projects opened by the user.
 * <p>
 * The elements of this collection are {@link ProjectSnapshot}s.
 */
public class RecentProjects extends SimpleEventBroadcast implements Iterable<ProjectSnapshot> {

    public static final String FILE_NAME = "recent_projects.dat";

    private final LinkedList<ProjectSnapshot> list;

    /**
     * Creates the collection.
     */
    public RecentProjects() {
        list = new LinkedList<>();
        load();
    }

    /**
     * Returns the amount of recent projects this collection has.
     *
     * @return the amount of recent projects.
     */
    public int size() {
        return list.size();
    }

    public Optional<ProjectSnapshot> get(int index) {
        if (index < 0 || index >= list.size()) return Optional.empty();
        return Optional.of(list.get(index));
    }

    /**
     * Adds a recent project.
     * <p>
     * This element will be added at the start of the collection.
     * If this project is already inside this collection, the project will be moved to the start of the collection.
     *
     * @param snapshot the recent project.
     */
    public void add(ProjectSnapshot snapshot) {
        Validate.notNull(snapshot, "Snapshot cannot be null!");

        var before = callEvent(new RecentProjectAddEvent.Before(snapshot));
        if (before.isCancelled()) return;

        list.remove(snapshot);
        list.addFirst(snapshot);

        callEvent(new RecentProjectAddEvent.After(snapshot));
    }

    /**
     * Saves the recent projects.
     */
    public void save() {
        var file = new File(Jams.getMainFolder(), FILE_NAME);
        var array = new JSONArray();
        list.forEach(snapshot -> array.put(snapshot.toJSON()));

        try {
            FileUtils.writeAll(file, array.toString(1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void load() {
        var file = new File(Jams.getMainFolder(), FILE_NAME);
        if (!file.isFile()) return;
        try {
            var object = new JSONArray(FileUtils.readAll(file));

            for (Object o : object) {
                try {
                    if (o instanceof JSONObject json) {
                        var snapshot = ProjectSnapshot.of(json);
                        if (!new File(snapshot.path()).isDirectory()) continue;
                        add(snapshot);
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Iterator<ProjectSnapshot> iterator() {
        return list.iterator();
    }

    @Override
    public void forEach(Consumer<? super ProjectSnapshot> consumer) {
        list.forEach(consumer);
    }

    @Override
    public Spliterator<ProjectSnapshot> spliterator() {
        return list.spliterator();
    }
}
