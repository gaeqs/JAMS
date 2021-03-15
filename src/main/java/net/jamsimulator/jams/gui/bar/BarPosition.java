package net.jamsimulator.jams.gui.bar;

import javafx.geometry.Orientation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

public enum BarPosition {

    TOP_LEFT(Orientation.HORIZONTAL, true),
    TOP_RIGHT(Orientation.HORIZONTAL, false),
    BOTTOM_LEFT(Orientation.HORIZONTAL, true),
    BOTTOM_RIGHT(Orientation.HORIZONTAL, false),
    LEFT_TOP(Orientation.VERTICAL, true),
    LEFT_BOTTOM(Orientation.VERTICAL, false),
    RIGHT_TOP(Orientation.VERTICAL, true),
    RIGHT_BOTTOM(Orientation.VERTICAL, false);

    private final Orientation orientation;
    private final boolean firstPane;
    private final String[] styleClasses;

    BarPosition(Orientation orientation, boolean firstPane) {
        this.orientation = orientation;
        this.firstPane = firstPane;
        this.styleClasses = new String[]{"bar-" + orientation.name().toLowerCase(Locale.ROOT),
                "bar-" + name().toLowerCase(Locale.ROOT).replace("_", "-")};
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public boolean shouldUseFirstPane() {
        return firstPane;
    }

    public void addStyleClasses(Collection<String> classes) {
        classes.addAll(Arrays.asList(styleClasses));
    }
}
