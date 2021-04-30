package net.jamsimulator.jams.gui.bar;

import javafx.scene.Node;
import javafx.scene.image.Image;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.event.ConfigurationNodeChangeEvent;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.bar.mode.BarSnapshotViewMode;
import net.jamsimulator.jams.utils.Validate;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents a {@link Node} that can be added as a tool in a {@link Bar}.
 * <p>
 * These snapshots should be registered in a {@link BarMap} using {@link BarMap#registerSnapshot(BarSnapshot)}.
 * This method will add the snapshot to the bar that matches the given {@link BarPosition}.
 */
public class BarSnapshot {

    public static final String CONFIGURATION_NODE_VIEW_MODE = "invisible.bar.%s.viewmode";
    public static final String CONFIGURATION_NODE_ENABLED = "invisible.bar.%s.enabled";
    public static final String CONFIGURATION_NODE_POSITION = "invisible.bar.%s.position";

    private final String name;
    private final Node node;
    private final Image icon;
    private final String languageNode;

    private BarPosition position;
    private BarSnapshotViewMode viewMode;
    private boolean enabled;

    private BarMap map;
    private BarButton button;

    /**
     * Creates the snapshot.
     *
     * @param name            the name of the snapshot. This name must be unique.
     * @param node            the {@link Node} represented by this snapshot.
     * @param defaultPosition the default position for the snapshot. This may be replaced by the configuration value.
     * @param defaultViewMode the default view mode. This may be replaced by the configuration value.
     * @param defaultEnable   whether this snapshot is enabled by default. This may be replaced by the configuration value.
     * @param icon            the icon to show in the {@link BarButton} or null.
     * @param languageNode    the language node for this snapshot or null.
     */
    public BarSnapshot(String name, Node node, BarPosition defaultPosition, BarSnapshotViewMode defaultViewMode, boolean defaultEnable,
                       Image icon, String languageNode) {
        Validate.notNull(name, "Name cannot be null!");
        Validate.notNull(node, "Node cannot be null!");
        Validate.notNull(defaultPosition, "Default position cannot be null!");
        this.name = name;
        this.node = node;
        this.icon = icon;
        this.languageNode = languageNode;

        this.position = Jams.getMainConfiguration()
                .getEnum(BarPosition.class, String.format(CONFIGURATION_NODE_POSITION, name))
                .orElse(defaultPosition);

        this.viewMode = Jams.getMainConfiguration()
                .getString(String.format(CONFIGURATION_NODE_VIEW_MODE, name))
                .flatMap(JamsApplication.getBarSnapshotViewModeManager()::get)
                .orElse(defaultViewMode);

        this.enabled = (boolean) Jams.getMainConfiguration()
                .get(String.format(CONFIGURATION_NODE_ENABLED, name)).orElse(defaultEnable);

        Jams.getMainConfiguration().registerListeners(this, true);
    }


    /**
     * Returns the name of the snapshot. This name must be unique.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * The {@link Node} being represented by this snapshot.
     *
     * @return the {@link Node}.
     */
    public Node getNode() {
        return node;
    }

    /**
     * The {@link Image icon} to show in the {@link BarButton} if present.
     *
     * @return the {@link Image icon} if present.
     */
    public Optional<Image> getIcon() {
        return Optional.ofNullable(icon);
    }

    /**
     * Returns the language node of this snapshot if present.
     *
     * @return the language node.
     */
    public Optional<String> getLanguageNode() {
        return Optional.ofNullable(languageNode);
    }

    /**
     * Returns the {@link BarButton button} currently containing this snapshot.
     * <p>
     * This {@link BarButton button} will change if the user moves this snapshot to another {@link Bar}!
     *
     * @return the {@link BarButton button} currently containing this snapshot.
     */
    public Optional<BarButton> getButton() {
        return Optional.ofNullable(button);
    }

    void setButton(BarButton button) {
        this.button = button;
    }

    /**
     * Returns the {@link BarMap} where this snapshot is registered at if present.
     *
     * @return the {@link BarMap} if present.
     */
    public Optional<BarMap> getMap() {
        return Optional.ofNullable(map);
    }

    void setMap(BarMap map) {
        this.map = map;
    }

    /**
     * Returns the current {@link BarSnapshotViewMode view mode} of this snapshot.
     *
     * @return the {@link BarSnapshotViewMode view mode}.
     */
    public BarSnapshotViewMode getViewMode() {
        return viewMode;
    }

    /**
     * Sets the {@link BarSnapshotViewMode view mode} for this snapshot.
     * <p>
     * This method updates the display if this snapshot is registered and visible.
     *
     * @param viewMode the {@link BarSnapshotViewMode view mode}.
     */
    public void setViewMode(BarSnapshotViewMode viewMode) {
        Validate.notNull(viewMode, "View mode cannot be null!");
        if (this.viewMode == viewMode) return;
        this.viewMode = viewMode;
        if (button != null && button.isSelected()) {
            button.hide();
            button.show();
        }
    }

    /**
     * Returns the {@link BarPosition position} of the bar where the
     * {@link BarButton button} representing this snapshot is registered at.
     *
     * @return the {@link BarPosition position}.
     */
    public BarPosition getPosition() {
        return position;
    }

    /**
     * Sets  the {@link BarPosition position} of the bar where the
     * {@link BarButton button} representing this snapshot is registered at.
     * <p>
     * This method automatically moves this snapshot to the corresponding {@link Bar}.
     *
     * @param position the {@link BarPosition position}.
     */
    public void setPosition(BarPosition position) {
        Validate.notNull(position, "Position cannot be null!");
        if (this.position == position) return;
        this.position = position;
        if (button != null) {
            var aux = button;
            aux.getBar().remove(this);
            aux.getBar().getMap().get(position).ifPresent(bar -> bar.add(this));
        }
    }

    /**
     * Returns whether this snapshot is enabled. Disabled snapshots don't appear in any {@link Bar}.
     *
     * @return whether this snapshot is enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether this snapshot is enabled. Disabled snapshots don't appear in any {@link Bar}.
     * <p>
     * This method updates the corresponding {@link Bar} automatically.
     *
     * @param enabled whether this snapshot is enabled.
     */
    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;
        this.enabled = enabled;

        if (enabled) {
            map.get(position).ifPresent(value -> value.add(this));
        } else {
            if (button == null) return;
            button.getBar().remove(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BarSnapshot that = (BarSnapshot) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }


    @Listener
    private void onConfigurationNodeChange(ConfigurationNodeChangeEvent.After event) {
        if (event.getNode().equals(String.format(CONFIGURATION_NODE_VIEW_MODE, name))) {
            setViewMode(event.getNewValue()
                    .map(Object::toString)
                    .flatMap(JamsApplication.getBarSnapshotViewModeManager()::get)
                    .orElse(viewMode));
        } else if (event.getNode().equals(String.format(CONFIGURATION_NODE_POSITION, name))) {
            setPosition(event.getNewValue().map(target ->
                    BarPosition.valueOf(target.toString())).orElse(BarPosition.LEFT_TOP));
        } else if (event.getNode().equals(String.format(CONFIGURATION_NODE_ENABLED, name))) {
            setEnabled((boolean) event.getNewValueAs().orElse(true));
        }
    }
}
