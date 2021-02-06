package net.jamsimulator.jams.gui.util;

import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import net.jamsimulator.jams.Jams;
import org.fxmisc.flowless.ScaledVirtualized;

public class ZoomUtils {

    public static void applyZoomListener(Node node, ScaledVirtualized<?> zoom) {
        node.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.isControlDown()
                    && (boolean) Jams.getMainConfiguration().get("editor.zoom_using_mouse_wheel").orElse(true)) {

                double current = zoom.getZoom().getX();
                if (event.getDeltaY() < 0) {
                    if (current > 0.4) {
                        zoom.getZoom().setX(current - 0.2);
                        zoom.getZoom().setY(current - 0.2);
                    }
                } else if (event.getDeltaY() > 0) {
                    zoom.getZoom().setX(current + 0.2);
                    zoom.getZoom().setY(current + 0.2);
                }
                event.consume();
            }
        });

        //RESET
        node.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.isControlDown() && event.getButton() == MouseButton.MIDDLE
                    && (boolean) Jams.getMainConfiguration().get("editor.reset_zoom_using_middle_button").orElse(true)) {
                zoom.getZoom().setX(1);
                zoom.getZoom().setY(1);
                zoom.getZoom().setZ(1);
            }
        });
    }

}
