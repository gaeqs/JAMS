package net.jamsimulator.jams.gui.bar;

import net.jamsimulator.jams.utils.Validate;

import java.util.*;
import java.util.function.Consumer;

/**
 * Represents a collection of {@link Bar}s that may share {@link BarSnapshot snapshot}s.
 * <p>
 * {@link Bar}s are related to their {@link BarPosition position}s: two or more {@link Bar}s cannot
 * share the same {@link BarPosition position}.
 */
public class BarMap {

    private final HashMap<BarPosition, Bar> bars;
    private final Set<BarSnapshot> registeredSnapshots;

    /**
     * Creates the bar map.
     */
    public BarMap() {
        bars = new HashMap<>();
        registeredSnapshots = new HashSet<>();
    }

    /**
     * Creates a bar at the given position and linked to the given {@link BarPane}.
     *
     * @param position the position.
     * @param pane     bar paned linked to the bar.
     * @return the new bar.
     * @see Bar#getBarPane()
     * @see BarPane
     */
    public Bar create(BarPosition position, BarPane pane) {
        var bar = new Bar(this, position, pane);
        bars.put(position, bar);
        return bar;
    }

    /**
     * Returns the {@link Bar} at the given {@link BarPosition position} if present.
     *
     * @param position the {@link BarPosition position}.
     * @return the {@link Bar} if present.
     */
    public Optional<Bar> get(BarPosition position) {
        return Optional.ofNullable(bars.get(position));
    }

    /**
     * Returns a unmodifiable {@link Set} containing all {@link BarSnapshot snapshots} registered by this map.
     *
     * @return the {@link Set}.
     * @see Collections#unmodifiableSet(Set)
     */
    public Set<BarSnapshot> getRegisteredSnapshots() {
        return Collections.unmodifiableSet(registeredSnapshots);
    }

    /**
     * Registers the given {@link BarSnapshot snapshot} in this map and, if enabled,
     * adds it to the corresponding {@link Bar}.
     *
     * @param snapshot the {@link BarSnapshot snapshot} to register.
     * @return whether the operation was successful.
     * @throws IllegalArgumentException when the given snapshot is already registered in another map.
     */
    public boolean registerSnapshot(BarSnapshot snapshot) {
        if (!registeredSnapshots.add(snapshot)) return false;
        Validate.isTrue(snapshot.getMap().isEmpty(), "The given snapshot is already registered in another map!");
        snapshot.setMap(this);

        if (snapshot.isEnabled()) {
            var bar = get(snapshot.getPosition());
            bar.ifPresent(target -> target.add(snapshot));
        }

        return true;
    }

    /**
     * Returns the button containing the {@link BarSnapshot snapshot} that matches the given name if present.
     * <p>
     * The button must be inside any of the {@link Bar}s of this map.
     *
     * @param name the name.
     * @return the button if present.
     */
    public Optional<? extends BarButton> searchButton(String name) {
        Optional<? extends BarButton> optional = Optional.empty();

        for (Bar bar : bars.values()) {
            optional = bar.getButton(name);
            if (optional.isPresent()) return optional;
        }

        return optional;
    }

    /**
     * Returns the button containing the given {@link BarSnapshot snapshot} if present.
     * <p>
     * The button must be inside any of the {@link Bar}s of this map.
     *
     * @param snapshot the {@link BarSnapshot} snapshot.
     * @return the button if present.
     */
    public Optional<? extends BarButton> searchButton(BarSnapshot snapshot) {
        Optional<? extends BarButton> optional = Optional.empty();

        for (Bar bar : bars.values()) {
            optional = bar.getButton(snapshot);
            if (optional.isPresent()) return optional;
        }

        return optional;
    }

    /**
     * Executes the given {@link Consumer} for every button inside the {@link Bar}s of this map.
     *
     * @param consumer the code to execute.
     */
    public void forEachButton(Consumer<BarButton> consumer) {
        bars.forEach((position, bar) -> bar.getButtons().forEach(consumer));
    }

}
