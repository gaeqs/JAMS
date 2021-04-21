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

public class RecentProjects extends SimpleEventBroadcast implements Iterable<ProjectSnapshot> {

    public static final String FILE_NAME = "recent_projects.dat";

    private final LinkedList<ProjectSnapshot> list;

    public RecentProjects() {
        list = new LinkedList<>();
        load();
    }

    public int size() {
        return list.size();
    }

    public Optional<ProjectSnapshot> get(int index) {
        if (index < 0 || index >= list.size()) return Optional.empty();
        return Optional.of(list.get(index));
    }

    public void add(ProjectSnapshot snapshot) {
        Validate.notNull(snapshot, "Snapshot cannot be null!");

        var before = callEvent(new RecentProjectAddEvent.Before(snapshot));
        if (before.isCancelled()) return;

        list.remove(snapshot);
        list.addFirst(snapshot);

        callEvent(new RecentProjectAddEvent.After(snapshot));
    }

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
