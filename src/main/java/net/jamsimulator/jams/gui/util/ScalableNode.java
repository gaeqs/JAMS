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

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import net.jamsimulator.jams.utils.Validate;

/**
 * This is a wrapper that adds scale controls to a node.
 * This wrapper can be used on {@link javafx.scene.control.ScrollPane}s.
 */
public class ScalableNode extends Group {

    private final Node node;

    /**
     * Creates the wrapper.
     *
     * @param node            the node to wrap.
     * @param addListenersToo a node to add the scale listeners too. This should be the used {@link javafx.scene.control.ScrollPane}.
     */
    public ScalableNode(Node node, Node addListenersToo) {
        super(node);
        Validate.notNull(node, "Node cannot be null!");
        this.node = node;
        initializeScaleListeners(this);
        if (addListenersToo != null) {
            initializeScaleListeners(addListenersToo);
        }
    }

    public Node getNode() {
        return node;
    }

    public double scaleX() {
        return node.getScaleX();
    }

    public double scaleY() {
        return node.getScaleY();
    }

    public double scaleZ() {
        return node.getScaleZ();
    }

    public void scaleX(double scale) {
        node.setScaleX(scale);
    }

    public void scaleY(double scale) {
        node.setScaleY(scale);
    }

    public void scaleZ(double scale) {
        node.setScaleZ(scale);
    }

    public void scale(double x, double y, double z) {
        node.setScaleX(x);
        node.setScaleY(y);
        node.setScaleZ(z);
    }

    public void scale(double x, double y) {
        node.setScaleX(x);
        node.setScaleY(y);
    }


    private void initializeScaleListeners(Node node) {
        node.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.isControlDown()) {
                double current = scaleX();
                if (event.getDeltaY() < 0) {
                    if (current > 0.4) {
                        scaleX(current - 0.2);
                        scaleY(current - 0.2);
                    }
                } else if (event.getDeltaY() > 0) {
                    scaleX(current + 0.2);
                    scaleY(current + 0.2);
                }
                event.consume();
            }
        });

        //RESET
        node.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.isControlDown() && event.getButton() == MouseButton.MIDDLE) {
                scale(1, 1, 1);
                event.consume();
            }
        });
    }

}
