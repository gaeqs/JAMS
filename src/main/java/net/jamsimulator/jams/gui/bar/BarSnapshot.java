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

    public String getName() {
        return name;
    }

    public Node getNode() {
        return node;
    }

    public Image getIcon() {
        return icon;
    }

    public String getLanguageNode() {
        return languageNode;
    }

    public Optional<BarButton> getButton() {
        return Optional.ofNullable(button);
    }

    void setButton(BarButton button) {
        this.button = button;
    }

    public Optional<BarMap> getMap() {
        return Optional.ofNullable(map);
    }

    void setMap(BarMap map) {
        this.map = map;
    }

    public BarSnapshotViewMode getViewMode() {
        return viewMode;
    }

    public void setViewMode(BarSnapshotViewMode viewMode) {
        if (this.viewMode == viewMode) return;
        this.viewMode = viewMode;
        if (button != null && button.isSelected()) {
            button.hide();
            button.show();
        }
    }

    public BarPosition getPosition() {
        return position;
    }

    public void setPosition(BarPosition position) {
        if (this.position == position) return;
        this.position = position;
        if (button != null) {
            var aux = button;
            aux.getBar().remove(this);
            aux.getBar().getMap().get(position).ifPresent(bar -> bar.add(this));
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

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
