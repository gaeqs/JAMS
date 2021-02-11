package net.jamsimulator.jams.gui.editor;

public enum FileOpenPosition {

    TOP, BOTTOM, LEFT, RIGHT;

    public boolean isHorizontal() {
        return this == LEFT || this == RIGHT;
    }

    public static FileOpenPosition getBestPositionByDistance(double x, double y) {
        if (y < 0.5 && x < 0.5) {
            return y < x ? TOP : LEFT;
        } else if (y < 0.5 && x >= 0.5) {
            return y < (1 - x) ? TOP : RIGHT;
        } else if (y >= 0.5 && x < 0.5) {
            return (1 - y) < x ? BOTTOM : LEFT;
        } else {
            return y > x ? BOTTOM : RIGHT;
        }
    }

}
