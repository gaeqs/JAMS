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
            double sensibility = Jams.getMainConfiguration().getNumber("editor.zoom_sensibility").orElse(0.2).doubleValue();
            if (event.isControlDown()
                    && (boolean) Jams.getMainConfiguration().get("editor.zoom_using_mouse_wheel").orElse(true)) {

                double current = zoom.getZoom().getX();
                double value = Math.max(0.4, current + sensibility * event.getDeltaY());
                zoom.getZoom().setX(value);
                zoom.getZoom().setY(value);
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
