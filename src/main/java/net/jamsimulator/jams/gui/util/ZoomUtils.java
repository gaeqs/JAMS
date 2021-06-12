/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

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
