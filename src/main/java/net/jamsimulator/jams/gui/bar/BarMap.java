package net.jamsimulator.jams.gui.bar;

import java.util.*;
import java.util.function.Consumer;

public class BarMap {

    private final HashMap<BarPosition, Bar> bars;
    private final Set<BarPaneSnapshot> registeredSnapshots;

    public BarMap() {
        bars = new HashMap<>();
        registeredSnapshots = new HashSet<>();
    }

    public Bar create(BarPosition position, BarPane pane) {
        var bar = new Bar(this, position, pane);
        bars.put(position, bar);
        return bar;
    }

    public Optional<Bar> get(BarPosition position) {
        return Optional.ofNullable(bars.get(position));
    }

    public Optional<BarPosition> getType(Bar bar) {
        for (Map.Entry<BarPosition, Bar> entry : bars.entrySet()) {
            if (entry.getValue().equals(bar)) return Optional.ofNullable(entry.getKey());
        }
        return Optional.empty();
    }

    public Set<BarPaneSnapshot> getRegisteredSnapshots() {
        return Collections.unmodifiableSet(registeredSnapshots);
    }

    public boolean registerSnapshot(BarPaneSnapshot snapshot) {
        if (!registeredSnapshots.add(snapshot)) return false;
        snapshot.setMap(this);

        if (snapshot.isEnabled()) {
            var bar = get(snapshot.getPosition());
            bar.ifPresent(target -> target.add(snapshot));
        }

        return true;
    }

    public Optional<? extends BarButton> searchButton(String name) {
        Optional<? extends BarButton> optional = Optional.empty();

        for (Bar bar : bars.values()) {
            optional = bar.getButton(name);
            if (optional.isPresent()) return optional;
        }

        return optional;
    }

    public Optional<? extends BarButton> searchButton(BarPaneSnapshot snapshot) {
        Optional<? extends BarButton> optional = Optional.empty();

        for (Bar bar : bars.values()) {
            optional = bar.getButton(snapshot);
            if (optional.isPresent()) return optional;
        }

        return optional;
    }

    public void forEachButton(Consumer<BarButton> consumer) {
        bars.forEach((position, bar) -> bar.getButtons().forEach(consumer));
    }

}
