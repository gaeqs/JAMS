package net.jamsimulator.jams.gui.util;

import javafx.scene.control.ScrollBar;
import org.fxmisc.flowless.VirtualizedScrollPane;

import java.util.Optional;

public class GUIReflectionUtils {

    public static Optional<ScrollBar> getVerticalScrollBar(VirtualizedScrollPane<?> scrollPane) {
        try {
            var field = VirtualizedScrollPane.class.getDeclaredField("vbar");
            field.setAccessible(true);
            return Optional.ofNullable((ScrollBar) field.get(scrollPane));
        } catch (Exception ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
    }

}
