package net.jamsimulator.jams.gui.bar;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BarMap {

    private final HashMap<BarPosition, Bar> bars;

    public BarMap() {
        bars = new HashMap<>();
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


}
